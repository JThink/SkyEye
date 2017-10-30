package com.jthink.skyeye.alarm.service;

import com.jthink.skyeye.alarm.configuration.mail.MailProperties;
import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.dto.FileDto;
import com.jthink.skyeye.base.dto.MailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StopWatch;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 发送邮件具体实现
 * @date 2016-09-23 16:51:38
 */
@org.springframework.stereotype.Service
@Scope("prototype")
@EnableConfigurationProperties(MailProperties.class)
public class MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    private transient Properties props = null;
    // 发送邮件session
    private transient Session session;
    private MimeMessage message;

    @Autowired
    private MailProperties mailProperties;

    /**
     * 发送邮件
     * @param mailDto
     */
    public void sendMail(MailDto mailDto) {
        LOGGER.info("send mail start...");
        StopWatch sw = new StopWatch();
        sw.start();
        this.init(mailDto);
        try {
            Transport transport = session.getTransport("smtp");
            InternetAddress from = new InternetAddress(this.mailProperties.getFrom().trim());
            message.setFrom(from);
            if (null != mailDto.getTo() && mailDto.getTo().size() > 0) {
                message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(this.changeListToString(mailDto.getTo())));
            }
            if (null != this.mailProperties.getCcs() && this.mailProperties.getCcs().size() > 0) {
                message.addRecipients(Message.RecipientType.CC,  InternetAddress.parse(this.changeListToString(this.mailProperties.getCcs())));
            }
            message.setSubject(mailDto.getSubject());
            Multipart multipart = new MimeMultipart("mixed");
            // 设置邮件的文本内容
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setText(mailDto.getContent());
            multipart.addBodyPart(contentPart);
            // 添加附件
            if (null != mailDto.getFiles() && mailDto.getFiles().size() > 0) {
                this.addTach(mailDto.getFiles(), multipart);
            }
            message.setContent(multipart);
            message.saveChanges();
            // 连接服务器的邮箱
            transport.connect(this.mailProperties.getSmtphost().trim(), this.mailProperties.getFrom().trim(), this.mailProperties.getPassword().trim());
            transport.sendMessage(message, message.getAllRecipients());
            LOGGER.info("邮件发送成功");
            sw.stop();
            LOGGER.info("send mail end, and it takes " + sw.getTotalTimeMillis() + " ms");
        } catch (NoSuchProviderException e) {
            sw.stop();
            LOGGER.info("send mail end, and it takes " + sw.getTotalTimeMillis() + " ms");
            LOGGER.error("邮件发送失败", e);
        } catch (AddressException e) {
            sw.stop();
            LOGGER.info("send mail end, and it takes " + sw.getTotalTimeMillis() + " ms");
            LOGGER.error("邮件发送失败", e);
        } catch (MessagingException e) {
            sw.stop();
            LOGGER.info("send mail end, and it takes " + sw.getTotalTimeMillis() + " ms");
            LOGGER.error("邮件发送失败", e);
        } catch (IOException e) {
            sw.stop();
            LOGGER.info("send mail end, and it takes " + sw.getTotalTimeMillis() + " ms");
            LOGGER.error("附件添加失败", e);
        }
    }

    /**
     * 添加附件
     * @param files
     * @param multipart
     */
    public void addTach(List<FileDto> files, Multipart multipart) throws IOException, MessagingException {
        for (FileDto file: files) {
            MimeBodyPart mailArchieve = new MimeBodyPart();
            File f = new File("/tmp/mail/" + file.getFileName());
            FileCopyUtils.copy(file.getFileBytes(), f);
            FileDataSource fd = new FileDataSource(f);
            mailArchieve.setDataHandler(new DataHandler(fd));
            mailArchieve.setFileName(MimeUtility.encodeText(fd.getName(), "utf-8", "B"));
            multipart.addBodyPart(mailArchieve);
        }
    }

    /**
     * 邮箱初始化
     * @param mailDto
     */
    private void init(MailDto mailDto) {
        if (null == props) {
            props = new Properties();
        }
        String path = "/tmp/mail/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", this.mailProperties.getPort().trim());
        props.put("mail.smtp.host", this.mailProperties.getSmtphost().trim());
        // 创建session
        session = Session.getInstance(props);
        message = new MimeMessage(session);
    }

    /**
     * 字符串拼接
     * @param list
     * @return
     */
    private String changeListToString(List<String> list) {
        StringBuffer toStr = new StringBuffer();
        int length = list.size();
        if (null != list && length < 2) {
            toStr.append(list.get(0));
        } else {
            for (int i = 0; i < length; i++) {
                toStr.append(list.get(i));
                if (i != (length - 1)) {
                    toStr.append(Constants.COMMA);
                }
            }
        }
        return toStr.toString();
    }
}
