package com.jthink.skyeye.alarm.configuration.dingding;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 钉钉配置文件
 * @date 2017-09-29 09:16:16
 */
@ConfigurationProperties(prefix = "spring.alert.dingding")
public class DingdingProperties {

    private String url;

    private boolean switchFlag;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSwitchFlag() {
        return switchFlag;
    }

    public void setSwitchFlag(boolean switchFlag) {
        this.switchFlag = switchFlag;
    }
}
