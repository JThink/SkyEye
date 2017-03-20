package com.jthink.skyeye.web.dto;

import com.jthink.skyeye.base.constant.Constants;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 历史查询前端传递的dto
 * @date 2016-10-13 09:39:15
 */
public class FilterDto {

    private String host;

    private String app;

    private String date;

    private String opt;

    private String time;

    private String level;

    private String eventType;

    public FilterDto() {
    }

    public FilterDto(String host, String app, String date, String opt, String time, String level, String eventType) {
        this.host = host;
        this.app = app;
        this.date = date;
        this.opt = opt;
        this.time = time;
        this.level = level;
        this.eventType = eventType;
    }

    public String buildSql() {
        StringBuffer sb = new StringBuffer();
        sb.append(Constants.HOST).append(Constants.EQUAL).append(Constants.SINGLE_PHE).append(this.host).append(Constants.SINGLE_PHE)
                .append(Constants.SPACE).append(Constants.AND).append(Constants.SPACE)
                .append(Constants.APP).append(Constants.EQUAL).append(Constants.SINGLE_PHE).append(this.app).append(Constants.SINGLE_PHE)
                .append(Constants.SPACE).append(Constants.AND).append(Constants.SPACE)
                .append(Constants.DAY).append(Constants.EQUAL).append(Constants.SINGLE_PHE).append(this.date).append(Constants.SINGLE_PHE)
                .append(Constants.SPACE).append(Constants.AND).append(Constants.SPACE)
                .append(Constants.TIME).append(this.opt).append(Constants.SINGLE_PHE).append(this.time).append(Constants.SINGLE_PHE)
                .append(Constants.SPACE).append(Constants.AND).append(Constants.SPACE)
                .append(Constants.LEVEL).append(Constants.EQUAL).append(Constants.SINGLE_PHE).append(this.level).append(Constants.SINGLE_PHE)
                .append(Constants.SPACE).append(Constants.AND).append(Constants.SPACE)
                .append(Constants.EVENT_TYPE).append(Constants.EQUAL).append(Constants.SINGLE_PHE).append(this.eventType).append(Constants.SINGLE_PHE)
                .append(Constants.NANO_TIME_ORDER_BY_ASC);

        return sb.toString();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
