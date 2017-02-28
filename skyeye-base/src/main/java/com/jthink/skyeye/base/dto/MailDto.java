package com.jthink.skyeye.base.dto;

import java.io.Serializable;
import java.util.List;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-09-23 16:59:39
 */
public class MailDto implements Serializable {

    private static final long serialVersionUID = 7447491327917924454L;

    // 附件文件
    private List<FileDto> files;
    // 主题
    private String subject;
    // 内容
    private String content;
    // 收件人
    private List<String> to;

    public MailDto() {

    }

    public MailDto(List<FileDto> files, String subject, String content, List<String> to) {
        this.files = files;
        this.subject = subject;
        this.content = content;
        this.to = to;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<FileDto> getFiles() {
        return files;
    }

    public void setFiles(List<FileDto> files) {
        this.files = files;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }
}