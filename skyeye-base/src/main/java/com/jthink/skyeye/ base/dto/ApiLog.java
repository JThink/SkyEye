package com.jthink.skyeye.base.dto;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.constant.EventType;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc api调用日志事件
 * @date 2016-11-14 10:17:08
 */
public class ApiLog extends EventLog {

    // 具体请求api的账户
    private String account;

    /**
     * 不可主动new
     */
    private ApiLog() {

    }

    public static ApiLog buildApiLog(EventType eventType, String uniqueName, String account, long cost, String status, String log) {
        ApiLog apiLog = new ApiLog();
        apiLog.setEventType(eventType);
        apiLog.setUniqueName(uniqueName);
        apiLog.setCost(cost);
        apiLog.setStatus(status);
        apiLog.setLog(log);
        apiLog.setAccount(account);
        return apiLog;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.eventType.symbol());
        sb.append(Constants.VERTICAL_LINE);
        sb.append(this.uniqueName);
        sb.append(Constants.VERTICAL_LINE);
        sb.append(this.account);
        sb.append(Constants.VERTICAL_LINE);
        sb.append(this.cost);
        sb.append(Constants.VERTICAL_LINE);
        sb.append(this.status);
        sb.append(Constants.VERTICAL_LINE);
        sb.append(this.log);
        return sb.toString();
    }

    /**
     * 根据字符串解析成EventLog
     * @param line
     * @return
     */
    public static ApiLog parseEventLog(String line) {
        String[] detail = line.split(Constants.VERTICAL_LINE_SPLIT);
        return buildApiLog(EventType.valueOf(detail[0]), detail[1], detail[2], Long.parseLong(detail[3]), detail[4], detail[5]);
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
