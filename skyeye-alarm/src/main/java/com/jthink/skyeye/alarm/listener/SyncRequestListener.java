package com.jthink.skyeye.alarm.listener;

import com.jthink.skyeye.alarm.configuration.wechat.WechatProperties;
import com.jthink.skyeye.alarm.service.MailService;
import com.jthink.skyeye.alarm.service.WechatService;
import com.jthink.skyeye.base.dto.MailDto;
import com.jthink.skyeye.data.rabbitmq.service.RabbitmqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 数据进入队列的监听器，负责处理
 * @date 2017-09-29 09:16:16
 */
@Component
public class SyncRequestListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncRequestListener.class);

    @Autowired
    private MailService mailService;
    @Autowired
    private RabbitmqService rabbitmqService;
    @Autowired
    private WechatService wechatService;
    @Autowired
    private WechatProperties wechatProperties;

    public void onMessage(Object object) {
        MailDto mailDto = null;
        try {
            if (null != object) {
                mailDto = (MailDto) object;
            }
            LOGGER.info("get a message, {}", object);

            // 发送邮件
            this.mailService.sendMail(mailDto);

            // 发送微信
            if (this.wechatProperties.isSwitchFlag()) {
                this.wechatService.send(mailDto.getContent());
            }

            // 发送钉钉

        }  catch (Exception e) {
            LOGGER.info("drop a error message, {}, {}", mailDto.getContent(), e);
        }
    }
}
