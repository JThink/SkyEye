package com.jthink.skyeye.data.rabbitmq.service;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.dto.MailDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 封装monitor-center需要的rabbit操作
 * @date 2016-11-23 09:03:16
 */
@Service
public class RabbitmqService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String info, String mail) {
        this.rabbitTemplate.convertAndSend(this.buildMailDto(info, mail));
    }

    /**
     * 构造mailDto
     * @param info
     * @param mail
     * @return
     */
    private MailDto buildMailDto(String info, String mail) {
        MailDto mailDto = new MailDto();
        mailDto.setTo(Arrays.asList(mail.split(Constants.COMMA)));
        mailDto.setContent(info);
        mailDto.setSubject(Constants.MONITOR_MAIL_SUBJECT);
        return mailDto;
    }
}
