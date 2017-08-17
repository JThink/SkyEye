package com.jthink.skyeye.client.log4j2.appender;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.client.core.constant.NodeMode;
import com.jthink.skyeye.client.core.producer.LazySingletonProducer;
import com.jthink.skyeye.client.core.util.SysUtil;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.SerializedLayout;
import org.apache.logging.log4j.core.util.StringEncoder;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc KafkaAppender, 包含log4j2 kafka appender的配置, 仿照官网的appender进行修改加入自己的功能
 * @date 2017-08-14 09:30:45
 */
@Plugin(name = "KafkaCustomize", category = Node.CATEGORY, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class KafkaAppender extends AbstractAppender {

    private final KafkaManager manager;

    // kafkaAppender遇到异常需要向zk进行写入数据，由于onCompletion()的调用在kafka集群完全挂掉时会有很多阻塞的日志会调用，所以我们需要保证只向zk写一次数据，监控中心只会发生一次报警
    private volatile AtomicBoolean flag = new AtomicBoolean(true);

    @PluginFactory
    public static KafkaAppender createAppender(
            @PluginElement("Layout") final Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @Required(message = "No name provided for KafkaAppender") @PluginAttribute("name") final String name,
            @Required(message = "No topic provided for KafkaAppender") @PluginAttribute("topic") final String topic,
            @Required(message = "No zkServers provided for KafkaAppender") @PluginAttribute("zkServers") final String zkServers,
            @Required(message = "No mail provided for KafkaAppender") @PluginAttribute("mail") final String mail,
            @Required(message = "No rpc provided for KafkaAppender") @PluginAttribute("rpc") final String rpc,
            @Required(message = "No app provided for KafkaAppender") @PluginAttribute("app") final String app,
            @Required(message = "No host provided for KafkaAppender") @PluginAttribute("host") final String host,
            @PluginElement("Properties") final Property[] properties,
            @PluginConfiguration final Configuration configuration) {
        final KafkaManager kafkaManager = new KafkaManager(configuration.getLoggerContext(), name, topic, zkServers, mail, rpc, app, host, properties);
        return new KafkaAppender(name, layout, filter, kafkaManager);
    }

    private KafkaAppender(final String name, final Layout<? extends Serializable> layout, final Filter filter, final KafkaManager manager) {
        super(name, filter, layout, true);
        this.manager = manager;
    }

    @Override
    public void append(LogEvent event) {
        if (!isStarted()) {
            return;
        }

        if (event.getLoggerName().startsWith("org.apache.kafka")) {
            LOGGER.warn("Recursive logging from [{}] for appender [{}].", event.getLoggerName(), getName());
        } else {
            try {
                final Layout<? extends Serializable> layout = getLayout();
                byte[] data;
                if (layout != null) {
                    if (layout instanceof SerializedLayout) {
                        final byte[] header = layout.getHeader();
                        final byte[] body = layout.toByteArray(event);
                        data = new byte[header.length + body.length];
                        System.arraycopy(header, 0, data, 0, header.length);
                        System.arraycopy(body, 0, data, header.length, body.length);
                    } else {
                        data = layout.toByteArray(event);
                    }
                } else {
                    data = StringEncoder.toBytes(event.getMessage().getFormattedMessage(), StandardCharsets.UTF_8);
                }
                // 发送数据到kafka
                String value = System.nanoTime() + Constants.SEMICOLON + new String(data);
                final ProducerRecord<byte[], String> record = new ProducerRecord<>(this.manager.getTopic(), this.manager.getKey(), value);
                LazySingletonProducer.getInstance(this.manager.getConfig()).send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        // TODO: 异常发生如何处理(目前使用RollingFileAppender.java中的方法)
                        if (null != e) {
                            // 设置停止
                            setStopped();
                            LOGGER.error("kafka send error in appender", e);
                            // 发生异常，kafkaAppender 停止收集，向节点写入数据（监控系统会感知进行报警）
                            if (flag.get() == true) {
                                KafkaAppender.this.manager.getZkRegister().write(Constants.SLASH + KafkaAppender.this.manager.getApp() + Constants.SLASH +
                                                KafkaAppender.this.manager.getHost(), NodeMode.EPHEMERAL,
                                        String.valueOf(System.currentTimeMillis()) + Constants.SEMICOLON + SysUtil.userDir);
                                flag.compareAndSet(true, false);
                            }
                        }
                    }
                });
            } catch (final Exception e) {
                LOGGER.error("Unable to write to Kafka [{}] for appender [{}].", manager.getName(), getName(), e);
                throw new AppenderLoggingException("Unable to write to Kafka in appender: " + e.getMessage(), e);
            }
        }


    }

    @Override
    public void start() {
        super.start();

        this.manager.startup();
    }

    @Override
    public boolean stop(long timeout, TimeUnit timeUnit) {
        setStopping();
        boolean stopped = super.stop(timeout, timeUnit, false);
        stopped &= this.manager.stop(timeout, timeUnit);
        setStopped();
        return stopped;
    }
}
