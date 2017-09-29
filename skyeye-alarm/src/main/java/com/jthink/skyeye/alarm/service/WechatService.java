package com.jthink.skyeye.alarm.service;

import com.jthink.skyeye.alarm.configuration.wechat.WechatProperties;
import com.jthink.skyeye.data.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 微信报警
 * @date 2016-09-29 17:32:33
 */
@Service
@EnableConfigurationProperties(WechatProperties.class)
public class WechatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatService.class);

    @Autowired
    private WechatProperties wechatProperties;

    public void send(String msg) {
        String reponse = HttpRequest.get(this.wechatProperties.getUrl(), msg);
        if (reponse != null) {
            // 请求成功
            LOGGER.info("发送微信成功");
        } else {
            LOGGER.info("发送微信失败");
        }
    }
}
