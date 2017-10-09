package com.jthink.skyeye.benchmark.log.generater.service;

import com.jthink.skyeye.base.constant.EventType;
import com.jthink.skyeye.base.constant.MiddleWare;
import com.jthink.skyeye.base.dto.ApiLog;
import com.jthink.skyeye.base.dto.EventLog;
import com.jthink.skyeye.benchmark.log.generater.util.RandomSeed;
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

    private static String[] urls = new String[] {"/app/status", "/app/find", "/app/search"};
    private static String[] thirdpartys = new String[] {"第三方A", "第三方B", "第三方C"};
    private static String[] accounts = new String[] {"800001", "800002", "800003"};

    public void generateNormalLog() {
        LOGGER.info("我是mock normal日志");
    }

    public void generateApiLog() {
        int urlIndex = RandomSeed.nextInt(3);
        int time = RandomSeed.nextInt(1001);
        int accountIndex = RandomSeed.nextInt(3);
        int success = RandomSeed.nextInt(2);
        ApiLog log = ApiLog.buildApiLog(EventType.invoke_interface, urls[urlIndex], accounts[accountIndex], time, success == 0 ? EventLog.MONITOR_STATUS_SUCCESS : EventLog.MONITOR_STATUS_FAILED, "我是mock api日志");
        LOGGER.info(log.toString());
    }

    public void generateMiddleWareLog() {
        int middleware = RandomSeed.nextInt(2);
        int time = RandomSeed.nextInt(1001);
        int success = RandomSeed.nextInt(2);
        EventLog log = EventLog.buildEventLog(EventType.middleware_opt, middleware == 0 ? MiddleWare.HBASE.symbol() : MiddleWare.MONGO.symbol(), time, success == 0 ? EventLog.MONITOR_STATUS_SUCCESS : EventLog.MONITOR_STATUS_FAILED, "我是mock middle ware日志");
        LOGGER.info(log.toString());
    }

    public void generateJobExecLog() {
        int success = RandomSeed.nextInt(2);
        int time = RandomSeed.nextInt(100000);
        int jobId = RandomSeed.nextInt(10000);
        EventLog log = EventLog.buildEventLog(EventType.job_execute, "application_" + jobId, time, success == 0 ? EventLog.MONITOR_STATUS_SUCCESS : EventLog.MONITOR_STATUS_FAILED, "我是mock job exec日志");
        LOGGER.info(log.toString());
    }

    public void generateThirdLog() {
        int partyIndex = RandomSeed.nextInt(3);
        int time = RandomSeed.nextInt(1001);
        int success = RandomSeed.nextInt(2);
        EventLog log = EventLog.buildEventLog(EventType.thirdparty_call, thirdpartys[partyIndex], time, success == 0 ? EventLog.MONITOR_STATUS_SUCCESS : EventLog.MONITOR_STATUS_FAILED, "我是mock third 日志");
        LOGGER.info(log.toString());
    }

    public void generateCoverLog() {
        // 生成正常入库日志
        LOGGER.info("我是mock normal日志");
        int apiExecuteCnt = RandomSeed.nextInt(10);
        for (int i = 0; i < apiExecuteCnt; i++) {
            this.generateApiLog();
        }
        int middlewareExecuteCnt = RandomSeed.nextInt(10);
        for (int i = 0; i < middlewareExecuteCnt; i++) {
            this.generateMiddleWareLog();
        }
        this.generateJobExecLog();
        int thirdExecuteCnt = RandomSeed.nextInt(10);
        for (int i = 0; i < thirdExecuteCnt; i++) {
            this.generateThirdLog();
        }
        // 生成api日志
//        LOGGER.info(ApiLog.buildApiLog(EventType.invoke_interface, "/app/status", "800001", 2000, EventLog.MONITOR_STATUS_SUCCESS, "我是mock api成功日志").toString());
//        LOGGER.info(ApiLog.buildApiLog(EventType.invoke_interface, "/app/status", "800001", 2000, EventLog.MONITOR_STATUS_FAILED, "我是mock api失败日志").toString());
//        LOGGER.info(ApiLog.buildApiLog(EventType.invoke_interface, "/log/realtime", "800002", 2000, EventLog.MONITOR_STATUS_SUCCESS, "我是mock api成功日志").toString());
//        // 生成中间件日志
//        LOGGER.info(EventLog.buildEventLog(EventType.middleware_opt, MiddleWare.HBASE.symbol(), 2000, EventLog.MONITOR_STATUS_SUCCESS, "我是mock middle ware成功日志").toString());
//        LOGGER.info(EventLog.buildEventLog(EventType.middleware_opt, MiddleWare.MONGO.symbol(), 2000, EventLog.MONITOR_STATUS_FAILED, "我是mock middle ware失败日志").toString());
//        // 生成任务执行日志
//        LOGGER.info(EventLog.buildEventLog(EventType.job_execute, "application_1477705439920_0544", 2000, EventLog.MONITOR_STATUS_FAILED, "我是mock job exec失败日志").toString());
//        LOGGER.info(EventLog.buildEventLog(EventType.job_execute, "application_1477705439920_0545", 2000, EventLog.MONITOR_STATUS_SUCCESS, "我是mock job exec成功日志").toString());
//        // 生成第三方日志
//        LOGGER.info(EventLog.buildEventLog(EventType.thirdparty_call, "信联", 2000, EventLog.MONITOR_STATUS_FAILED, "我是mock third 失败日志").toString());
//        LOGGER.info(EventLog.buildEventLog(EventType.thirdparty_call, "信联", 2000, EventLog.MONITOR_STATUS_SUCCESS, "我是mock third 成功日志").toString());
//        LOGGER.info(EventLog.buildEventLog(EventType.thirdparty_call, "百付", 2000, EventLog.MONITOR_STATUS_SUCCESS, "我是mock third 成功日志").toString());
//        LOGGER.info(EventLog.buildEventLog(EventType.thirdparty_call, "百付", 2000, EventLog.MONITOR_STATUS_FAILED, "我是mock third 失败日志").toString());
    }
}
