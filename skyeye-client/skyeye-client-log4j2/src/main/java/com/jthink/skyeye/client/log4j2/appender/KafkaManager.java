package com.jthink.skyeye.client.log4j2.appender;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.client.core.constant.KafkaConfig;
import com.jthink.skyeye.client.core.kafka.partitioner.KeyModPartitioner;
import com.jthink.skyeye.client.core.producer.LazySingletonProducer;
import com.jthink.skyeye.client.core.register.ZkRegister;
import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.config.Property;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc KafkaMangager, 对kafka以及别的相关操作, 修改自官网
 * @date 2017-08-15 09:30:45
 */
public class KafkaManager extends AbstractManager {

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
    // zk注册器
    private ZkRegister zkRegister;
    private byte[] key;
    // 心跳检测
    private Timer timer;
    // 原始app
    private String orginApp;

    public KafkaManager(final LoggerContext loggerContext, final String name, final String topic, final String zkServers, final String mail, final  String rpc,
                        final String app, final String host, final Property[] properties) {
        super(loggerContext, name);
        this.topic = topic;
        this.zkServers = zkServers;
        this.mail = mail;
        this.rpc = rpc;
        this.app = app;
        this.orginApp = app;
        this.host = host;
        this.checkAndSetConfig(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        this.checkAndSetConfig(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // 设置分区类, 使用自定义的KeyModPartitioner，同样的key进入相同的partition
        this.checkAndSetConfig(ProducerConfig.PARTITIONER_CLASS_CONFIG, KeyModPartitioner.class.getName());
        // xml配置里面的参数
        for (final Property property : properties) {
            this.config.put(property.getName(), property.getValue());
        }
        // 由于容器部署需要从外部获取host
        this.config.put(ProducerConfig.CLIENT_ID_CONFIG, this.app + Constants.MIDDLE_LINE + this.host + Constants.MIDDLE_LINE + "log4j2");
    }

    /**
     * appender初始化
     */
    public void startup() {
        // 初始化zk
        this.zkRegister = new ZkRegister(new ZkClient(this.zkServers, 60000, 5000));
        // 对app重新编号，防止一台host部署一个app的多个实例
        this.app = this.zkRegister.mark(this.app, this.host);
        // 设置key
        this.key = ByteBuffer.allocate(4).putInt(new StringBuilder(this.app).append(this.host).toString().hashCode()).array();

        // 注册节点
        this.zkRegister.registerNode(this.host, this.app, this.mail);
        // rpc trace注册中心
        this.zkRegister.registerRpc(this.host, this.app, this.rpc);
    }

    @Override
    public boolean releaseSub(final long timeout, final TimeUnit timeUnit) {
        // 关闭KafkaProuder和zk
        // producer实际上已经初始化
        // This thread is a workaround for this Kafka issue: https://issues.apache.org/jira/browse/KAFKA-1660

        // JDK1.8, 为了向下兼容1.7, 故不采用此种方式
        // final Runnable task = () -> KafkaManager.this.closeResources();
        final Runnable task = new Runnable() {
            @Override
            public void run() {
                KafkaManager.this.closeResources();
            }
        };

        try {
            getLoggerContext().submitDaemon(task).get(timeout, timeUnit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // ignore
        }

        return true;
    }

    /**
     * 关闭zk和kafka、心跳检测
     */
    public void closeResources() {
        if (null != this.timer) {
            this.timer.cancel();
        }
        if (LazySingletonProducer.isInstanced()) {
            LazySingletonProducer.getInstance(KafkaManager.this.config).close();
        }
        ZkClient client = this.zkRegister == null ? null : this.zkRegister.getClient();
        if (null != client) {
            client.close();
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
            LOGGER.warn("in this kafka version don't has this config: " + key);
        }
        this.config.put(key, value);
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public ZkRegister getZkRegister() {
        return zkRegister;
    }

    public void setZkRegister(ZkRegister zkRegister) {
        this.zkRegister = zkRegister;
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

    public String getRpc() {
        return rpc;
    }

    public void setRpc(String rpc) {
        this.rpc = rpc;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public Timer getTimer() {
        return timer;
    }

    public KafkaManager setTimer(Timer timer) {
        this.timer = timer;
        return this;
    }

    public String getOrginApp() {
        return orginApp;
    }

    public KafkaManager setOrginApp(String orginApp) {
        this.orginApp = orginApp;
        return this;
    }
}
