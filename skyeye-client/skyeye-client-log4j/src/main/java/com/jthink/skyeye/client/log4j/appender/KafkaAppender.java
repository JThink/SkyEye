package com.jthink.skyeye.client.log4j.appender;

import com.jthink.skyeye.base.constant.RpcType;
import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.util.StringUtil;
import com.jthink.skyeye.client.core.constant.KafkaConfig;
import com.jthink.skyeye.client.core.constant.NodeMode;
import com.jthink.skyeye.client.core.kafka.partitioner.KeyModPartitioner;
import com.jthink.skyeye.client.core.producer.LazySingletonProducer;
import com.jthink.skyeye.client.core.register.ZkRegister;
import com.jthink.skyeye.client.core.util.SysUtil;
import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc KafkaAppender, 包含log4j kafka appender的配置
 * @date 2016-09-27 09:30:45
 */
public class KafkaAppender extends AppenderSkeleton {

    // kafka topic
    private String topic;
    // 生产日志的host
    private String host = SysUtil.host;
    // 生产日志的app，多节点部署会使日志有序
    private String app;
    // zookeeper的地址
    private String zkServers;
    // 接受报警邮件的接收方
    private String mail;
    // 标记是否为rpc服务, 取值为RpcType.java
    private String rpc;
    // KafkaProducer类的配置
    private Map<String, Object> config = new HashMap<>();
    // zk注册器
    private ZkRegister zkRegister;
    // kafka producer是否正在初始化
    private volatile AtomicBoolean isInitializing = new AtomicBoolean(false);
    // kafka producer未完成初始化之前的消息存放的队列
    private ConcurrentLinkedQueue<String> msgQueue = new ConcurrentLinkedQueue<>();

    // kafka server
    private String bootstrapServers;
    // 消息确认模式
    private String acks;
    // linger.ms
    private String lingerMs;
    // max.block.ms
    private String maxBlockMs;
    // kafkaAppender遇到异常需要向zk进行写入数据，由于onCompletion()的调用在kafka集群完全挂掉时会有很多阻塞的日志会调用，所以我们需要保证只向zk写一次数据，监控中心只会发生一次报警
    private volatile AtomicBoolean flag = new AtomicBoolean(true);
    // 心跳检测
    private Timer timer;
    // key
    private byte[] key;

    /**
     * 构造方法
     */
    public KafkaAppender() {
        this.checkAndSetConfig(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        this.checkAndSetConfig(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // 设置分区类, 使用自定义的KeyModPartitioner，同样的key进入相同的partition
        this.checkAndSetConfig(ProducerConfig.PARTITIONER_CLASS_CONFIG, KeyModPartitioner.class.getName());

        // 添加hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                close();
            }
        });
    }

    /**
     * 覆写doAppend, 去掉closed的log日志
     * @param event
     */
    @Override
    public synchronized void doAppend(LoggingEvent event) {
        if (closed) {
            return;
        }

        if (!isAsSevereAsThreshold(event.getLevel())) {
            return;
        }

        Filter f = this.headFilter;

        FILTER_LOOP:
        while(f != null) {
            switch(f.decide(event)) {
                case Filter.DENY: return;
                case Filter.ACCEPT: break FILTER_LOOP;
                case Filter.NEUTRAL: f = f.getNext();
            }
        }

        this.append(event);
    }

    @Override
    protected void append(LoggingEvent event) {
        if (closed) {
            return;
        }
        this.sendMessage(this.getMessage(event));
    }

    /**
     * 向kafka send
     * @param value
     */
    private void send(String value) {
        // 对value的大小进行判定，当大于某个值认为该日志太大直接丢弃（防止影响到kafka）
        if (value.length() > 10000) {
            return;
        }

        final ProducerRecord<byte[], String> record = new ProducerRecord<>(this.topic, this.key, value);
        LazySingletonProducer.getInstance(this.config).send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                // TODO: 异常发生如何处理(直接停掉appender)
                if (null != e) {
                    closed = true;
                    LogLog.error("kafka send error in appender", e);
                    // 发生异常，kafkaAppender 停止收集，向节点写入数据（监控系统会感知进行报警）
                    if (flag.get() == true) {
                        KafkaAppender.this.heartbeatStart();
                        zkRegister.write(Constants.SLASH + app + Constants.SLASH + host, NodeMode.EPHEMERAL,
                                String.valueOf(Constants.APP_APPENDER_STOP_KEY + Constants.SEMICOLON + System.currentTimeMillis()) + Constants.SEMICOLON + SysUtil.userDir);
                        flag.compareAndSet(true, false);
                    }
                }
            }
        });
    }

    /**
     * 心跳检测开始
     */
    public void heartbeatStart() {
        // 心跳检测定时器初始化
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                byte[] key = ByteBuffer.allocate(4).putInt(Constants.HEARTBEAT_KEY.hashCode()).array();
                final ProducerRecord<byte[], String> record = new ProducerRecord<>(topic, key, Constants.HEARTBEAT_VALUE);

                // java 8 lambda
//                LazySingletonProducer.getInstance(config).send(record, (RecordMetadata recordMetadata, Exception e) -> {
                // logic code
//                });

                LazySingletonProducer.getInstance(config).send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if (null == e) {
                            // 更新flag状态
                            flag.compareAndSet(false, true);
                            // 如果没有发生异常, 说明kafka从异常状态切换为正常状态, 将开始状态设置为true
                            closed = false;
                            LogLog.warn("kafka send normal in appender", e);
                            // 关闭心跳检测机制
                            KafkaAppender.this.heartbeatStop();
                            zkRegister.write(Constants.SLASH + app + Constants.SLASH + host, NodeMode.EPHEMERAL,
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
        if (null != this.timer) {
            this.timer.cancel();
        }
    }

    /**
     * 发送msg
     * @param msg
     */
    private void sendMessage(String msg) {
        if (!LazySingletonProducer.isInstanced()) {
            if (this.isInitializing.get() != true) {
                this.isInitializing.compareAndSet(false, true);
                this.initKafkaConfig();
                this.isInitializing.compareAndSet(true, false);
                this.send(msg);
            } else {
                this.msgQueue.add(msg);
            }
        } else if (this.msgQueue.size() > 0) {
            if (LazySingletonProducer.isInstanced() ) {
                this.msgQueue.add(msg);
                while (this.msgQueue.size() > 0) {
                    this.send(this.msgQueue.remove());
                }
            }
        } else {
            this.send(msg);
        }
    }

    /**
     * 初始化kafka config
     */
    private void initKafkaConfig() {

        if (!LazySingletonProducer.isInstanced()) {

            // app配置
            if (StringUtil.isBlank(this.host)) {
                // host未获取到
                LogLog.error("can't get the host");
                closed = true;
                return;
            }

            if (StringUtil.isBlank(this.app)) {
                // app name未设置
                LogLog.error("log4j.xml is not set the app");
                closed = true;
                return;
            }

            // zk配置
            if (StringUtil.isBlank(this.zkServers)) {
                // zk地址未设置
                LogLog.error("can't get zkServers");
                closed = true;
                return;
            }

            if (StringUtil.isBlank(this.topic)) {
                // topic未设置（或者设置成了""），无法写入kafka
                LogLog.error("topic is not set, appender: " + name);
                closed = true;
                return;
            }

            if (StringUtil.isBlank(this.mail)) {
                // 报警mail未设置
                LogLog.error("mail is not set, appender: " + name);
                closed = true;
                return;
            }

            if (StringUtil.isBlank(this.rpc) || !this.checkRpcType(this.rpc)) {
                // rpc未设置或者rpc值不对
                LogLog.error("rpc is not set or value not right, appender: " + name);
                closed = true;
                return;
            }

            new Thread() {
                @Override
                public void run() {
                    // 初始化zk
                    KafkaAppender.this.zkRegister = new ZkRegister(new ZkClient(zkServers, 60000, 5000));
                    // 对app重新编号，防止一台host部署一个app的多个实例
                    KafkaAppender.this.app = KafkaAppender.this.app + KafkaAppender.this.zkRegister.mark(KafkaAppender.this.app, KafkaAppender.this.host);
                    KafkaAppender.this.key = ByteBuffer.allocate(4).putInt(new StringBuilder(app).append(host).toString().hashCode()).array();

                    // 注册节点
                    KafkaAppender.this.zkRegister.registerNode(KafkaAppender.this.host, KafkaAppender.this.app, KafkaAppender.this.mail);

                    // rpc trace注册中心
                    KafkaAppender.this.zkRegister.registerRpc(KafkaAppender.this.host, KafkaAppender.this.app, KafkaAppender.this.rpc);
                }
            }.start();

            if (StringUtil.isNotBlank(this.bootstrapServers)) {
                this.config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
            }
            if (StringUtil.isNotBlank(this.acks)) {
                this.config.put(ProducerConfig.ACKS_CONFIG, this.acks);
            }
            if (StringUtil.isNotBlank(this.lingerMs)) {
                this.config.put(ProducerConfig.LINGER_MS_CONFIG, this.lingerMs);
            }
            if (StringUtil.isNotBlank(this.maxBlockMs)) {
                this.config.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, this.maxBlockMs);
            }
            if (StringUtil.isNotBlank(this.app) && StringUtil.isNotBlank(this.host)) {
                this.config.put(ProducerConfig.CLIENT_ID_CONFIG, this.app + Constants.MIDDLE_LINE + this.host + Constants.MIDDLE_LINE + "log4j");
            }

            LazySingletonProducer.getInstance(this.config);
        }
    }

    /**
     * 监察rpc type是否正确
     * @param rpcType
     * @return
     */
    private boolean checkRpcType(String rpcType) {
        try {
            RpcType.valueOf(rpcType);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获得message
     * @param event
     * @return
     */
    private String getMessage(LoggingEvent event) {
        if (this.layout == null) {
            return event.getRenderedMessage();
        } else {
            // 获取host和app
            String msg = System.nanoTime() + Constants.SEMICOLON + this.layout.format(event);
            return msg.replaceFirst(Constants.APP_NAME, this.app).replaceFirst(Constants.HOSTNAME, this.host);
        }
    }

    @Override
    public void close() {
        closed = true;

        // 停止心跳
        this.heartbeatStop();
        // 关闭KafkaProuder
        if (LazySingletonProducer.isInstanced()) {
            // producer实际上已经初始化
            LazySingletonProducer.getInstance(this.config).close();
        }

        // 关闭client，临时节点消失，监控系统进行感知报警
        ZkClient client = this.zkRegister == null ? null : this.zkRegister.getClient();
        if (null != client) {
            client.close();
        }
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    /**
     * 进行kafka配置设置
     * @param key
     * @param value
     */
    public void checkAndSetConfig(String key, String value) {
        if (!KafkaConfig.PRODUCER_CONFIG_KEYS.contains(key)) {
            // 当前kafka版本没有该配置项
            LogLog.warn("in this kafka version don't has this config: " + key);
        }
        this.config.put(key, value);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getZkServers() {
        return zkServers;
    }

    public void setZkServers(String zkServers) {
        this.zkServers = zkServers;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public String getLingerMs() {
        return lingerMs;
    }

    public void setLingerMs(String lingerMs) {
        this.lingerMs = lingerMs;
    }

    public String getMaxBlockMs() {
        return maxBlockMs;
    }

    public void setMaxBlockMs(String maxBlockMs) {
        this.maxBlockMs = maxBlockMs;
    }

    public String getRpc() {
        return rpc;
    }

    public void setRpc(String rpc) {
        this.rpc = rpc;
    }
}
