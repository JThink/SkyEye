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
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
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
            @PluginElement("Properties") final Property[] properties,
            @PluginConfiguration final Configuration configuration) {
        final KafkaManager kafkaManager = new KafkaManager(configuration.getLoggerContext(), name, topic, zkServers, mail, rpc, app, SysUtil.host, properties);
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
                // 对value的大小进行判定，当大于某个值认为该日志太大直接丢弃（防止影响到kafka）
                if (value.length() > 10000) {
                    return;
                }
                final ProducerRecord<byte[], String> record = new ProducerRecord<>(this.manager.getTopic(), this.manager.getKey(),
                        value.replaceFirst(this.manager.getOrginApp(), this.manager.getApp()).replaceFirst(Constants.HOSTNAME, this.manager.getHost()));
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
                                // 启动心跳检测机制
                                KafkaAppender.this.heartbeatStart();
                                // 向zk通知
                                KafkaAppender.this.manager.getZkRegister().write(Constants.SLASH + KafkaAppender.this.manager.getApp() + Constants.SLASH +
                                                KafkaAppender.this.manager.getHost(), NodeMode.EPHEMERAL,
                                        String.valueOf(Constants.APP_APPENDER_STOP_KEY + Constants.SEMICOLON + System.currentTimeMillis()) + Constants.SEMICOLON + SysUtil.userDir);
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

    /**
     * 心跳检测开始
     */
    public void heartbeatStart() {
        // 心跳检测定时器初始化
        this.manager.setTimer(new Timer());
        Timer timer = this.manager.getTimer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                byte[] key = ByteBuffer.allocate(4).putInt(Constants.HEARTBEAT_KEY.hashCode()).array();
                final ProducerRecord<byte[], String> record = new ProducerRecord<>(KafkaAppender.this.manager.getTopic(), key, Constants.HEARTBEAT_VALUE);

                // java 8 lambda
//                LazySingletonProducer.getInstance(config).send(record, (RecordMetadata recordMetadata, Exception e) -> {
                // logic code
//                });

                LazySingletonProducer.getInstance(KafkaAppender.this.manager.getConfig()).send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if (null == e) {
                            // 更新flag状态
                            flag.compareAndSet(false, true);
                            // 如果没有发生异常, 说明kafka从异常状态切换为正常状态, 将开始状态设置为true
                            setStarted();
                            LOGGER.info("kafka send normal in appender", e);
                            // 关闭心跳检测机制
                            KafkaAppender.this.heartbeatStop();
                            KafkaAppender.this.manager.getZkRegister().write(Constants.SLASH + KafkaAppender.this.manager.getApp() +
                                            Constants.SLASH + KafkaAppender.this.manager.getHost(), NodeMode.EPHEMERAL,
                                    String.valueOf(Constants.APP_APPENDER_RESTART_KEY + Constants.SEMICOLON + System.currentTimeMillis()) + Constants.SEMICOLON + SysUtil.userDir);
                        }
                    }
                });
            }
        }, 10000,60000);
    }

    /**
     * 心跳检测停止
     */
    private void heartbeatStop() {
        if (null != this.manager.getTimer()) {
            this.manager.getTimer().cancel();
        }
    }

    @Override
    public void start() {
        super.start();

        // 添加hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                KafkaAppender.this.manager.closeResources();
            }
        });

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
