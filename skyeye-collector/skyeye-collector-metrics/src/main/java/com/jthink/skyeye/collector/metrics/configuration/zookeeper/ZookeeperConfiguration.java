package com.jthink.skyeye.collector.metrics.configuration.zookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc zk的配置
 * @date 2016-11-22 08:45:53
 */
@Configuration
@ConfigurationProperties(prefix = "spring.coordinate.zookeeper")
public class ZookeeperConfiguration {

    private String zkServers;

    private int sessionTimeout;

    private int connectionTimeout;

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
}
