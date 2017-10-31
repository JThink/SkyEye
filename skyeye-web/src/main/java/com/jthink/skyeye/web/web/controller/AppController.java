package com.jthink.skyeye.web.web.controller;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.web.message.BaseMessage;
import com.jthink.skyeye.web.message.MessageCode;
import com.jthink.skyeye.web.message.StatusCode;
import com.jthink.skyeye.web.service.AppMonitorService;
import com.jthink.skyeye.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-10-09 09:19:47
 */
@RestController
@RequestMapping("app")
public class AppController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private AppMonitorService appMonitorService;

    @RequestMapping(path = "statusHost", method = RequestMethod.GET)
    public BaseMessage statusHostInfo(@RequestParam(value = "app", required = false) final String app,
            @RequestParam(value = "host", required = false) final String host) {
        BaseMessage msg = new BaseMessage();
        try {
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
            msg.setData(this.appMonitorService.getHostAppInfo(this.convertStr(host), this.convertStr(app), Constants.ZK_NODE_TYPE_EPHEMERAL));
            return msg;
        } catch (Exception e) {
            LOGGER.error("app 状态查询失败(host)");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }

    @RequestMapping(path = "deployHost", method = RequestMethod.GET)
    public BaseMessage deployHostInfo(@RequestParam(value = "app", required = false) final String app,
            @RequestParam(value = "host", required = false) final String host) {
        BaseMessage msg = new BaseMessage();
        try {
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
            msg.setData(this.appMonitorService.getHostAppInfo(this.convertStr(host), this.convertStr(app), Constants.ZK_NODE_TYPE_PERSISTENT));
            return msg;
        } catch (Exception e) {
            LOGGER.error("app 部署查询失败(host)");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }

    @RequestMapping(path = "hostApp", method = RequestMethod.GET)
    public BaseMessage getHostApp(@RequestParam(value = "type", required = false) final int type,
            @RequestParam(value = "isDeploy", required = false) final boolean isDeploy) {
        BaseMessage msg = new BaseMessage();
        try {
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
            msg.setData(this.appMonitorService.getHostApp(type, isDeploy));
            return msg;
        } catch (Exception e) {
            LOGGER.error("host app 查询失败(app)");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }

    @RequestMapping(path = "statusApp", method = RequestMethod.GET)
    public BaseMessage statusAppInfo(@RequestParam(value = "app", required = false) final String app,
            @RequestParam(value = "host", required = false) final String host) {
        BaseMessage msg = new BaseMessage();
        try {
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
            msg.setData(this.appMonitorService.getAppHostInfo(this.convertStr(host), this.convertStr(app), Constants.ZK_NODE_TYPE_EPHEMERAL));
            return msg;
        } catch (Exception e) {
            LOGGER.error("app 状态查询失败(app)");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }

    @RequestMapping(path = "deployApp", method = RequestMethod.GET)
    public BaseMessage deployAppInfo(@RequestParam(value = "app", required = false) final String app,
            @RequestParam(value = "host", required = false) final String host) {
        BaseMessage msg = new BaseMessage();
        try {
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
            msg.setData(this.appMonitorService.getAppHostInfo(this.convertStr(host), this.convertStr(app), Constants.ZK_NODE_TYPE_PERSISTENT));
            return msg;
        } catch (Exception e) {
            LOGGER.error("app 部署查询失败(app)");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }

    @RequestMapping(path = "appHost", method = RequestMethod.GET)
    public BaseMessage getAppHost(@RequestParam(value = "type", required = false) final int type) {
        BaseMessage msg = new BaseMessage();
        try {
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
            msg.setData(this.appMonitorService.getAppHost(type));
            return msg;
        } catch (Exception e) {
            LOGGER.error("app host 查询失败");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }

    /**
     * 转换
     * @param str
     * @return
     */
    private String convertStr(String str) {
        return null == str || str.contains(Constants.PLEASE_CHOOSE) ? null : str;
    }

}
