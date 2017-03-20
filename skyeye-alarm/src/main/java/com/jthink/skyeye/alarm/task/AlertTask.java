package com.jthink.skyeye.alarm.task;

import com.jthink.skyeye.alarm.service.MailService;
import com.jthink.skyeye.base.dto.MailDto;
import com.jthink.skyeye.data.rabbitmq.service.RabbitmqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 报警的task，包含微信报警和邮件报警
 * @date 2016-09-26 11:29:48
 */
@Configuration
@EnableScheduling
public class AlertTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlertTask.class);

    @Autowired
    private MailService mailService;
    @Autowired
    private RabbitmqService rabbitmqService;

    @Scheduled(cron = "*/10 * * * * ?")
    private void sendMail() {
        try {
            MailDto mailDto = this.rabbitmqService.getMessage();
            if (null != mailDto) {
                this.mailService.sendMail(mailDto);
            }
        } catch (Exception e) {
            LOGGER.error("报警失败，直接丢弃", e);
        }
    }
}
