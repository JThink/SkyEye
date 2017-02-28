package com.jthink.skyeye.base.dto;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.constant.EventType;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 事件日志
 * @date 2016-11-08 11:06:09
 */
public class EventLog {

    // 事件日志成功还是失败
    public static final String MONITOR_STATUS_SUCCESS = "success";
    public static final String MONITOR_STATUS_FAILED = "failed";

    // 日志事件类型
    protected EventType eventType;
    // 日志事件名称，如果是api调用则设置成api，如果是任务调度则设置成appId，如果是第三方系统则设置成第三方对接的名字，如果是中间件请设置成MiddleWare
    protected String uniqueName;
    // 需要计算耗时的日志设置耗时, 毫秒
    protected long cost;
    // 状态
    protected String status;
    // 具体日志内容
    protected String log;

    /**
     * 不可主动new
     */
    protected EventLog() {

    }

    /**
     * 创建eventlog
     * @param eventType
     * @param log
     * @return
     */
    public static EventLog buildEventLog(EventType eventType, String uniqueName, long cost, String status, String log) {
        EventLog eventLog = new EventLog();
        eventLog.setEventType(eventType);
        eventLog.setUniqueName(uniqueName);
        eventLog.setCost(cost);
        eventLog.setStatus(status);
        eventLog.setLog(log);
        return eventLog;
    }

    /**
     * 根据一条日志的内容解析出该条日志
     * @param line
     * @return
     */
    public static EventType parseEventType(String line) {
        if (line.indexOf(Constants.VERTICAL_LINE) == -1) {
            // log中不包含|, 说明肯定是normal日志
            return EventType.normal;
        } else {
            // 首先判断是否是用户自己的日志中包含|
            String[] detail = line.split(Constants.VERTICAL_LINE_SPLIT);
            try {
                return EventType.valueOf(detail[0]);
            } catch (Exception e) {
                return EventType.normal;
            }
        }
    }

    /**
     * 根据字符串解析成EventLog
     * @param line
     * @return
     */
    public static EventLog parseEventLog(String line) {
        String[] detail = line.split(Constants.VERTICAL_LINE_SPLIT);
        return buildEventLog(EventType.valueOf(detail[0]), detail[1], Long.parseLong(detail[2]), detail[3], detail[4]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.eventType.symbol());
        sb.append(Constants.VERTICAL_LINE);
        sb.append(this.uniqueName);
        sb.append(Constants.VERTICAL_LINE);
        sb.append(this.cost);
        sb.append(Constants.VERTICAL_LINE);
        sb.append(this.status);
        sb.append(Constants.VERTICAL_LINE);
        sb.append(this.log);
        return sb.toString();
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
