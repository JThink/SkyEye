package com.jthink.skyeye.alarm.configuration.wechat;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Copyright (C), 2016, 银联智惠信息服务（上海）有限公司
 *
 * @author qianjc
 * @version 0.0.1
 * @desc
 * @date 2016-09-29 17:40:53
 */
@ConfigurationProperties(prefix = "spring.alert.wechat")
public class WechatProperties {

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
