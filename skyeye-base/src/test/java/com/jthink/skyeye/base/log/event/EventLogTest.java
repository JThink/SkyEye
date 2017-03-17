package com.jthink.skyeye.base.log.event;


import com.jthink.skyeye.base.constant.EventType;
import com.jthink.skyeye.base.constant.MiddleWare;
import com.jthink.skyeye.base.dto.ApiLog;
import com.jthink.skyeye.base.dto.EventLog;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 日志事件相关测试
 * @date 2016-08-24 20:23:16
 */
public class EventLogTest {

    public static void main(String[] args) {
        EventLog e = EventLog.buildEventLog(EventType.middleware_opt, MiddleWare.HBASE.symbol(), 100, EventLog.MONITOR_STATUS_SUCCESS, "测试构造日志事件类型");
        System.out.println(e);

        EventType et = EventLog.parseEventType(e.toString());
        System.out.println(et);

        EventLog er = EventLog.parseEventLog(e.toString());
        System.out.println(er);



        ApiLog a = ApiLog.buildApiLog(EventType.invoke_interface, MiddleWare.HBASE.symbol(), "800001", 100, EventLog.MONITOR_STATUS_SUCCESS, "测试构造日志事件类型");
        System.out.println(a);

        EventType et1 = EventLog.parseEventType(a.toString());
        System.out.println(et1);

        ApiLog ar = ApiLog.parseEventLog(a.toString());
        System.out.println(ar);
    }

}
