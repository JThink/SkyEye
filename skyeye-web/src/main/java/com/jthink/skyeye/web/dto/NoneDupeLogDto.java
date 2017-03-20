package com.jthink.skyeye.web.dto;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-10-11 17:14:13
 */
public class NoneDupeLogDto {

    private String flag;

    private String log;

    public NoneDupeLogDto() {
    }

    public NoneDupeLogDto(String flag, String log) {
        this.flag = flag;
        this.log = log;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
