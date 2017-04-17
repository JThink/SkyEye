package com.jthink.skyeye.statistics.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 任务相关的task
 * @date 2017-01-11 16:17:27
 */
@ConfigurationProperties(prefix = "spring.spark.task")
public class TaskProperties {

    // spark job名字
    private String rpcJobName;

    public String getRpcJobName() {
        return rpcJobName;
    }

    public void setRpcJobName(String rpcJobName) {
        this.rpcJobName = rpcJobName;
    }
}
