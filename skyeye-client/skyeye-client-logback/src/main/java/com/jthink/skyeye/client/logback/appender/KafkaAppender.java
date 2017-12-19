package com.jthink.skyeye.client.logback.appender;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.hook.DelayingShutdownHook;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;
import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.constant.RpcType;
import com.jthink.skyeye.base.util.StringUtil;
import com.jthink.skyeye.client.core.constant.KafkaConfig;
import com.jthink.skyeye.client.core.constant.NodeMode;
import com.jthink.skyeye.client.core.kafka.partitioner.KeyModPartitioner;
import com.jthink.skyeye.client.core.producer.LazySingletonProducer;
import com.jthink.skyeye.client.core.register.ZkRegister;
import com.jthink.skyeye.client.core.util.SysUtil;
import com.jthink.skyeye.client.logback.builder.KeyBuilder;
import com.jthink.skyeye.client.logback.encoder.KafkaLayoutEncoder;
import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc KafkaAppender, 包含logback kafka appender的配置
 * @date 2016-09-08 20:10:21
 */
public class KafkaAppender<E> extends UnsynchronizedAppenderBase<E>  {

    // kafka topic
    private String topic;
    // 生产日志的host
    private String host;
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
    // key生成器
    private KeyBuilder<? super E> keyBuilder;
    // 编码器
    private KafkaLayoutEncoder<E> encoder;
    // zk注册器
    private ZkRegister zkRegister;
    // hook
    private DelayingShutdownHook shutdownHook;
    // kafkaAppender遇到异常需要向zk进行写入数据，由于onCompletion()的调用在kafka集群完全挂掉时会有很多阻塞的日志会调用，所以我们需要保证只向zk写一次数据，监控中心只会发生一次报警
    private volatile AtomicBoolean flag = new AtomicBoolean(true);
    // 心跳检测
    private Timer timer;
    // key
    private byte[] key;
    // 原始app
    private String orginApp;

    /**
     * 构造方法
     */
    public KafkaAppender() {
        this.checkAndSetConfig(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        this.checkAndSetConfig(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // 设置分区类, 使用自定义的KeyModPartitioner，同样的key进入相同的partition
        this.checkAndSetConfig(ProducerConfig.PARTITIONER_CLASS_CONFIG, KeyModPartitioner.class.getName());

        // 由于容器部署需要从外部获取host
        this.checkAndSetConfig(ProducerConfig.CLIENT_ID_CONFIG, this.app + Constants.MIDDLE_LINE + this.host + Constants.MIDDLE_LINE + "logback");

        shutdownHook = new DelayingShutdownHook();
    }

    @Override
    public void start() {
        // xml配置校验
        if (!this.checkNecessaryConfig()) {
            addError("necessary config is not set, kafka appender is not started");
            return;
        }

        super.start();

        // 添加logback shutdown hook, 关闭所有的appender, 调用stop()方法
        shutdownHook.setContext(this.getContext());
        Runtime.getRuntime().addShutdownHook(new Thread(this.shutdownHook));

        // 初始化zk
        this.zkRegister = new ZkRegister(new ZkClient(this.zkServers, 60000, 5000));
        // 对app重新编号，防止一台host部署一个app的多个实例
        this.orginApp = app;
        this.app = this.zkRegister.mark(this.app, this.host);
        this.key = ByteBuffer.allocate(4).putInt(new StringBuilder(this.app).append(this.host).toString().hashCode()).array();

        // 注册节点
        this.zkRegister.registerNode(this.host, this.app, this.mail);

        // rpc trace注册中心
        this.zkRegister.registerRpc(this.host, this.app, this.rpc);
    }

    @Override
    public void stop() {
        super.stop();

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
    protected void append(E e) {
        if (!isStarted()) {
            return;
        }
        final String value = System.nanoTime() + Constants.SEMICOLON + this.encoder.doEncode(e);
        // 对value的大小进行判定，当大于某个值认为该日志太大直接丢弃（防止影响到kafka）
        if (value.length() > 10000) {
            return;
        }
        final ProducerRecord<byte[], String> record = new ProducerRecord<>(this.topic, this.key, value.replaceFirst(this.orginApp, this.app).replaceFirst(Constants.HOSTNAME, this.host));
        LazySingletonProducer.getInstance(this.config).send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (null != e) {
                    // 如果发生异常, 将开始状态设置为false, 并每次append的时候都先check该状态
                    started = false;
                    addStatus(new ErrorStatus("kafka send error in appender", this, e));
                    // 发生异常，kafkaAppender 停止收集，向节点写入数据（监控系统会感知进行报警）
                    if (flag.get()) {
                        // 启动心跳检测机制
                        KafkaAppender.this.heartbeatStart();
                        // 向zk通知
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
                            started = true;
                            addStatus(new InfoStatus("kafka send normal in appender", this, e));
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

    @Override
    public void setContext(Context context) {
        super.setContext(context);

        this.host = SysUtil.host;
        this.app = context.getName();
    }

    /**
     * 校验最基本的配置是否在logback.xml进行配置
     * @return
     */
    private boolean checkNecessaryConfig() {

        boolean flag = true;

        // app配置
        if (StringUtil.isBlank(this.host)) {
            // host未获取到
            addError("can't get the host");
            flag = false;
        }

        if (StringUtil.isBlank(this.app)) {
            // app name未设置
            addError("logback.xml is not set the <contextName></contextName> node");
            flag = false;
        }

        // zk配置
        if (StringUtil.isBlank(this.zkServers)) {
            // zk地址未设置
            addError("can't get zkServers");
            flag = false;
        }

        // kafka配置
        if (null == config.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)) {
            // kafka的bootstrap.servers未设置
            addError("kafka's " + ProducerConfig.BOOTSTRAP_SERVERS_CONFIG + " do not set, appender: " + name);
            flag = false;
        }

        if (StringUtil.isBlank(this.topic)) {
            // topic未设置（或者设置成了""），无法写入kafka
            addError("topic is not set, appender: " + name);
            flag = false;
        }

        if (StringUtil.isBlank(this.mail)) {
            // 报警mail未设置
            addError("mail is not set, appender: " + name);
            flag = false;
        }

        if (StringUtil.isBlank(this.rpc) || !this.checkRpcType(this.rpc)) {
            // rpc未设置或者rpc值不对
            addError("rpc is not set or value not right, appender: " + name);
            flag = false;
        }

        if (null == this.keyBuilder) {
            // key生成器为设置
            addError("key builder is not set, appender: " + name);
            flag = false;
        }

        if (null == this.encoder) {
            // 编码器未设置
            addError("encoder is not set, appender: " + name);
            flag = false;
        }
        return flag;
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
     * 将logback配置文件中<config></config>节点中的值读入Map<String, String> config
     * @param kv
     */
    public void addConfig(String kv) {
        String[] keyValue = kv.split(Constants.EQUAL, 2);
        if (keyValue.length == 2) {
            this.checkAndSetConfig(keyValue[0], keyValue[1]);
        } else {
            // 值设置得不对
            addError("config item value is wrong, appender: " + name);
        }
    }

    /**
     * 进行kafka配置设置
     * @param key
     * @param value
     */
    public void checkAndSetConfig(String key, String value) {
        if (!KafkaConfig.PRODUCER_CONFIG_KEYS.contains(key)) {
            // 当前kafka版本没有该配置项
            addWarn("in this kafka version don't has this config: " + key);
        }
        this.config.put(key, value);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
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

    public KeyBuilder<? super E> getKeyBuilder() {
        return keyBuilder;
    }

    public void setKeyBuilder(KeyBuilder<? super E> keyBuilder) {
        this.keyBuilder = keyBuilder;
    }

    public KafkaLayoutEncoder<E> getEncoder() {
        return encoder;
    }

    public void setEncoder(KafkaLayoutEncoder<E> encoder) {
        this.encoder = encoder;
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

    public String getRpc() {
        return rpc;
    }

    public void setRpc(String rpc) {
        this.rpc = rpc;
    }
}
