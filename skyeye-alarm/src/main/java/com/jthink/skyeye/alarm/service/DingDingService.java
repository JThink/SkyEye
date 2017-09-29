package com.jthink.skyeye.alarm.service;

import com.alibaba.fastjson.JSON;
import com.jthink.skyeye.alarm.configuration.dingding.DingdingProperties;
import com.jthink.skyeye.alarm.dto.DingDingDto;
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
 * @desc 钉钉报警
 * @date 2017-09-29 17:32:33
 */
@Service
@EnableConfigurationProperties(DingdingProperties.class)
public class DingDingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatService.class);

    @Autowired
    private DingdingProperties dingdingProperties;

    public void send(String msg) {
        DingDingDto dingDingDto = new DingDingDto();
        dingDingDto.setAt(new DingDingDto.At(true)).setText(new DingDingDto.Text(msg));
        String reponse = HttpRequest.post(this.dingdingProperties.getUrl(), JSON.toJSONString(dingDingDto));
        if (reponse != null) {
            // 请求成功
            LOGGER.info("发送钉钉成功");
        } else {
            LOGGER.info("发送钉钉失败");
        }
    }

}
