package com.jthink.skyeye.monitor.configuration.zookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc zk的配置
 * @date 2016-09-23 08:45:53
 */
@Configuration
@ConfigurationProperties(prefix = "spring.coordinate.zookeeper")
public class ZookeeperConfiguration {

    private String zkServers;

    private int sessionTimeout;

    private int connectionTimeout;

    private int baseSleepTimeMs;

    private int maxRetries;

    @Bean
    public CuratorFramework curatorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(this.baseSleepTimeMs, this.maxRetries);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().
                connectString(this.zkServers).
                sessionTimeoutMs(this.sessionTimeout).
                connectionTimeoutMs(this.connectionTimeout).
                retryPolicy(retryPolicy).
                build();

        curatorFramework.start();
        return curatorFramework;
    }

    @Bean
    public ZkClient zkClient() {
        return new ZkClient(this.zkServers, this.sessionTimeout, this.connectionTimeout);
    }

    public String getZkServers() {
        return zkServers;
    }

    public void setZkServers(String zkServers) {
        this.zkServers = zkServers;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }

    public void setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
}
