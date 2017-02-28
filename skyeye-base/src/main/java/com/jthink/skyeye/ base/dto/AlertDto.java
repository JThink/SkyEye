package com.jthink.skyeye.base.dto;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.util.StringUtil;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 报警dto
 * @date 2016-09-29 21:25:17
 */
public class AlertDto {

    private String time;

    private String app;

    private String host;

    private String deploy;

    private String msg;

    public AlertDto() {
    }

    public AlertDto(String time, String app, String host, String deploy, String msg) {
        this.time = time;
        this.app = app;
        this.host = host;
        this.deploy = deploy;
        this.msg = msg;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.MONITOR_APP_ALERT).append(Constants.LINE_FEED);
        if (StringUtil.isNotBlank(this.app)) {
            sb.append(Constants.WECHAT_ALERT_APP).append(this.app).append(Constants.LINE_FEED);
        }
        if (StringUtil.isNotBlank(this.host)) {
            sb.append(Constants.WECHAT_ALERT_HOST).append(this.host).append(Constants.LINE_FEED);
        }
        if (StringUtil.isNotBlank(this.deploy)) {
            sb.append(Constants.WECHAT_ALERT_DEPOLY).append(this.deploy).append(Constants.LINE_FEED);
        }
        if (StringUtil.isNotBlank(this.time)) {
            sb.append(Constants.WECHAT_ALERT_TIME).append(this.time).append(Constants.LINE_FEED);
        }
        if (StringUtil.isNotBlank(this.msg)) {
            sb.append(Constants.WECHAT_ALERT_MSG).append(this.msg);
        }

        return sb.toString();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDeploy() {
        return deploy;
    }

    public void setDeploy(String deploy) {
        this.deploy = deploy;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
