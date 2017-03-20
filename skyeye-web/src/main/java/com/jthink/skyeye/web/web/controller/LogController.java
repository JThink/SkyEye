package com.jthink.skyeye.web.web.controller;

import com.jthink.skyeye.web.dto.FilterDto;
import com.jthink.skyeye.web.message.BaseMessage;
import com.jthink.skyeye.web.message.MessageCode;
import com.jthink.skyeye.web.message.StatusCode;
import com.jthink.skyeye.web.service.LogQueryService;
import com.jthink.skyeye.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 日志控制器
 * @date 2016-10-08 11:11:57
 */
@RestController
@RequestMapping("log")
public class LogController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogController.class);

    @Autowired
    private LogQueryService logQueryService;

    @RequestMapping(path = "realtime", method = RequestMethod.GET)
    public BaseMessage realtime(@RequestParam(value = "app", required = false) final String app,
            @RequestParam(value = "host", required = false) final String host,
            @RequestParam(value = "keyword", required = false) final String keyword,
            @RequestParam(value = "interval", required = false) final int interval) {
        BaseMessage msg = new BaseMessage();
        try {
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
            msg.setData(this.logQueryService.getRealtimeLog(host, app, keyword, interval));
            return msg;
        } catch (Exception e) {
            LOGGER.error("实时日志查询失败");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }

    @RequestMapping(path = "history", method = RequestMethod.POST)
    public BaseMessage history(@RequestBody(required = false) final FilterDto filterDto) {
        BaseMessage msg = new BaseMessage();
        try {
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
            Map<String, Object> data = this.logQueryService.getHistoryLog(filterDto);
            if (null == data) {
                ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.CONDITION_ERROR);
            } else {
                msg.setData(data);
            }
            return msg;
        } catch (Exception e) {
            LOGGER.error("历史日志查询失败");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }

    @RequestMapping(path = "query", method = RequestMethod.GET)
    public BaseMessage query(@RequestParam(value = "sql", required = false) final String sql) {
        BaseMessage msg = new BaseMessage();
        try {
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
            Map<String, Object> data = this.logQueryService.getQueryLog(sql);
            if (null == data) {
                ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SQL_ERROR);
            } else {
                msg.setData(data);
            }
            return msg;
        } catch (Exception e) {
            LOGGER.error("sql日志查询失败");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }

    @RequestMapping(path = "level", method = RequestMethod.GET)
    public BaseMessage level() {
        BaseMessage msg = new BaseMessage();
        try {
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
            List<String> data = this.logQueryService.getLogLevel();
            if (null == data) {
                ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SQL_ERROR);
            } else {
                msg.setData(data);
            }
            return msg;
        } catch (Exception e) {
            LOGGER.error("日志级别查询失败");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }

    @RequestMapping(path = "eventType", method = RequestMethod.GET)
    public BaseMessage eventType() {
        BaseMessage msg = new BaseMessage();
        try {
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
            List<String> data = this.logQueryService.getEventType();
            if (null == data) {
                ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SQL_ERROR);
            } else {
                msg.setData(data);
            }
            return msg;
        } catch (Exception e) {
            LOGGER.error("日志事件查询失败");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }

    @RequestMapping(path = "opt", method = RequestMethod.GET)
    public BaseMessage opt() {
        BaseMessage msg = new BaseMessage();
        try {
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
            List<String> data = this.logQueryService.getOpt();
            if (null == data) {
                ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SQL_ERROR);
            } else {
                msg.setData(data);
            }
            return msg;
        } catch (Exception e) {
            LOGGER.error("日志操作符查询失败");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }
}
