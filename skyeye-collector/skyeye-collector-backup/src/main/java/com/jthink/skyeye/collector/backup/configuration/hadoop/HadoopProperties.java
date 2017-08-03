package com.jthink.skyeye.collector.backup.configuration.hadoop;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc kafka配置项
 * @date 2016-09-20 10:15:05
 */
@ConfigurationProperties(prefix = "spring.bigdata.hadoop.hdfs")
public class HadoopProperties {

    private String host;

    private String port;

    private String user;

    private String baseDir;

    private String fileRoot;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getFileRoot() {
        return fileRoot;
    }

    public void setFileRoot(String fileRoot) {
        this.fileRoot = fileRoot;
    }
}
