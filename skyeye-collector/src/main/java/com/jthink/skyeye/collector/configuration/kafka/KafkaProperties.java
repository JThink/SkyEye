package com.jthink.skyeye.collector.configuration.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc kafka配置项
 * @date 2016-09-20 10:15:05
 */
@ConfigurationProperties(prefix = "spring.message.kafka")
public class KafkaProperties {

    private String topic;

    private String brokers;

    private String indexerGroup;

    private long pollTimeout;

    private String collectGroup;

    private String backupGroup;

    private String rpcTraceGroup;

    private String fileRoot;

    private String serverId;

    public String getFileRoot() {
        return fileRoot;
    }

    public void setFileRoot(String fileRoot) {
        this.fileRoot = fileRoot;
    }

    public String getBackupGroup() {
        return backupGroup;
    }

    public void setBackupGroup(String backupGroup) {
        this.backupGroup = backupGroup;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBrokers() {
        return brokers;
    }

    public void setBrokers(String brokers) {
        this.brokers = brokers;
    }

    public String getIndexerGroup() {
        return indexerGroup;
    }

    public void setIndexerGroup(String indexerGroup) {
        this.indexerGroup = indexerGroup;
    }

    public long getPollTimeout() {
        return pollTimeout;
    }

    public void setPollTimeout(long pollTimeout) {
        this.pollTimeout = pollTimeout;
    }

    public String getCollectGroup() {
        return collectGroup;
    }

    public void setCollectGroup(String collectGroup) {
        this.collectGroup = collectGroup;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getRpcTraceGroup() {
        return rpcTraceGroup;
    }

    public void setRpcTraceGroup(String rpcTraceGroup) {
        this.rpcTraceGroup = rpcTraceGroup;
    }
}
