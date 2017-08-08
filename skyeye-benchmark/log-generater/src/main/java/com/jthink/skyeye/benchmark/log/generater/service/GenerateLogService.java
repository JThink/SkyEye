package com.jthink.skyeye.benchmark.log.generater.service;

import com.jthink.skyeye.base.constant.EventType;
import com.jthink.skyeye.base.constant.MiddleWare;
import com.jthink.skyeye.base.dto.ApiLog;
import com.jthink.skyeye.base.dto.EventLog;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 日志生成器
 * @date 2016-11-24 16:56:47
 */
@Service
public class GenerateLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateLogService.class);

    public void generateNormalLog() {
        LOGGER.info("我是mock normal日志");
    }

    public void generateApiLog() {
        ApiLog log = ApiLog.buildApiLog(EventType.invoke_interface, "/app/status", "800001", 100, EventLog.MONITOR_STATUS_SUCCESS, "我是mock api日志");
        LOGGER.info(log.toString());
    }

    public void generateMiddleWareLog() {
        EventLog log = EventLog.buildEventLog(EventType.middleware_opt, MiddleWare.HBASE.symbol(), 100, EventLog.MONITOR_STATUS_SUCCESS, "我是mock middle ware日志");
        LOGGER.info(log.toString());
    }

    public void generateJobExecLog() {
        EventLog log = EventLog.buildEventLog(EventType.job_execute, "application_1477705439920_0544", 100, EventLog.MONITOR_STATUS_FAILED, "我是mock job exec日志");
        LOGGER.info(log.toString());
    }

    public void generateThirdLog() {
        EventLog log = EventLog.buildEventLog(EventType.thirdparty_call, "信联", 100, EventLog.MONITOR_STATUS_FAILED, "我是mock third 日志");
        LOGGER.info(log.toString());
    }

    public void generateCoverLog() {
        // 生成正常入库日志
        LOGGER.info("我是mock normal日志");
        // 生成api日志
        LOGGER.info(ApiLog.buildApiLog(EventType.invoke_interface, "/app/status", "800001", 2000, EventLog.MONITOR_STATUS_SUCCESS, "我是mock api成功日志").toString());
        LOGGER.info(ApiLog.buildApiLog(EventType.invoke_interface, "/app/status", "800001", 2000, EventLog.MONITOR_STATUS_FAILED, "我是mock api失败日志").toString());
        LOGGER.info(ApiLog.buildApiLog(EventType.invoke_interface, "/log/realtime", "800002", 2000, EventLog.MONITOR_STATUS_SUCCESS, "我是mock api成功日志").toString());
        // 生成中间件日志
        LOGGER.info(EventLog.buildEventLog(EventType.middleware_opt, MiddleWare.HBASE.symbol(), 2000, EventLog.MONITOR_STATUS_SUCCESS, "我是mock middle ware成功日志").toString());
        LOGGER.info(EventLog.buildEventLog(EventType.middleware_opt, MiddleWare.MONGO.symbol(), 2000, EventLog.MONITOR_STATUS_FAILED, "我是mock middle ware失败日志").toString());
        // 生成任务执行日志
        LOGGER.info(EventLog.buildEventLog(EventType.job_execute, "application_1477705439920_0544", 2000, EventLog.MONITOR_STATUS_FAILED, "我是mock job exec失败日志").toString());
        LOGGER.info(EventLog.buildEventLog(EventType.job_execute, "application_1477705439920_0545", 2000, EventLog.MONITOR_STATUS_SUCCESS, "我是mock job exec成功日志").toString());
        // 生成第三方日志
        LOGGER.info(EventLog.buildEventLog(EventType.thirdparty_call, "信联", 2000, EventLog.MONITOR_STATUS_FAILED, "我是mock third 失败日志").toString());
        LOGGER.info(EventLog.buildEventLog(EventType.thirdparty_call, "信联", 2000, EventLog.MONITOR_STATUS_SUCCESS, "我是mock third 成功日志").toString());
        LOGGER.info(EventLog.buildEventLog(EventType.thirdparty_call, "百付", 2000, EventLog.MONITOR_STATUS_SUCCESS, "我是mock third 成功日志").toString());
        LOGGER.info(EventLog.buildEventLog(EventType.thirdparty_call, "百付", 2000, EventLog.MONITOR_STATUS_FAILED, "我是mock third 失败日志").toString());
    }
}
