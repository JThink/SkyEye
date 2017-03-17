package com.jthink.skyeye.alarm.test.mail;

import com.jthink.skyeye.alarm.service.MailService;
import com.unionpaysmart.shaker.dto.MailDto;

import java.util.Arrays;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 发送邮件test
 * @date 2016-09-23 16:52:02
 */
public class MailTest {

    public static void main(String[] args) throws InterruptedException {

        MailDto mailDto = new MailDto();
        mailDto.setFrom("xxx@unionpaysmart.com");
        mailDto.setCc(Arrays.asList("xxx@unionpaysmart.com"));
        mailDto.setTo(Arrays.asList("xxx@unionpaysmart.com"));
        mailDto.setPassword("xxx");
        mailDto.setSmtpHost("smtp.unionpaysmart.com");
        mailDto.setPort("25");
        mailDto.setContent("报警测试");
        mailDto.setSubject("[monitor-center]-bane app alert");

        for (int i = 0; i < 100; ++i) {
            new MailService().sendMail(mailDto);
//            Thread.sleep(2000);
        }
    }
}
