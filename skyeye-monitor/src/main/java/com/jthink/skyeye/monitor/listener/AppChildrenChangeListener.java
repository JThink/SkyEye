package com.jthink.skyeye.monitor.listener;

import com.jthink.skyeye.data.rabbitmq.service.RabbitmqService;
import com.jthink.skyeye.monitor.service.AppInfoService;
import com.jthink.skyeye.monitor.service.CacheService;
import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.constant.LogCollectionStatus;
import com.jthink.skyeye.base.dto.AlertDto;
import com.jthink.skyeye.base.dto.MailDto;
import com.jthink.skyeye.base.util.DateUtil;
import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 监听临时节点变化
 * @date 2016-09-23 11:33:05
 */
public class AppChildrenChangeListener implements PathChildrenCacheListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppChildrenChangeListener.class);

    private RabbitmqService rabbitmqService;

    private ZkClient zkClient;

    private AppInfoService appInfoService;

    public AppChildrenChangeListener(RabbitmqService rabbitmqService, ZkClient zkClient, AppInfoService appInfoService) {
        this.rabbitmqService = rabbitmqService;
        this.zkClient = zkClient;
        this.appInfoService = appInfoService;
    }

    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
        String node = Constants.EMPTY_STR;
        String app = Constants.EMPTY_STR;
        String host = Constants.EMPTY_STR;
        String info = Constants.EMPTY_STR;
        String[] datas = null;
        switch (event.getType()) {
            case CHILD_ADDED:
                node = event.getData().getPath();
                app = this.getApp(node);
                host = this.getHost(node);
                if (!CacheService.appHosts.contains(node)) {
                    datas = this.zkClient.readData(Constants.ROOT_PATH_PERSISTENT + Constants.SLASH + app + Constants.SLASH + host).toString().split(Constants.SEMICOLON);

                    info = this.buildMsg(DateUtil.format(new Date(System.currentTimeMillis()), DateUtil.YYYYMMDDHHMMSS), app,
                            this.getHost(node), datas[1], Constants.APP_START);

                    // add to the queue
                    this.rabbitmqService.sendMessage(info, datas[0]);
                    LOGGER.info(info);
                    CacheService.appHosts.add(node);
                    this.appInfoService.add(host, app, Constants.ZK_NODE_TYPE_EPHEMERAL, this.calLogCollectionStatus(app, host));
                }
                this.appInfoService.add(host, app, Constants.ZK_NODE_TYPE_PERSISTENT, LogCollectionStatus.HISTORY);
                break;
            case CHILD_REMOVED:
                node = event.getData().getPath();
                app = this.getApp(node);
                host = this.getHost(node);
                datas = this.zkClient.readData(Constants.ROOT_PATH_PERSISTENT + Constants.SLASH + app + Constants.SLASH + host).toString().split(Constants.SEMICOLON);

                info = this.buildMsg(DateUtil.format(new Date(System.currentTimeMillis()), DateUtil.YYYYMMDDHHMMSS), app,
                        this.getHost(node), datas[1], Constants.APP_STOP);

                // add to the queue
                this.rabbitmqService.sendMessage(info, datas[0]);
                LOGGER.info(info);
                if (CacheService.appHosts.contains(node)) {
                    CacheService.appHosts.remove(node);
                    this.appInfoService.delete(host, app, Constants.ZK_NODE_TYPE_EPHEMERAL);
                }
                break;
            case CHILD_UPDATED:
                node = event.getData().getPath();
                datas = this.zkClient.readData(node).toString().split(Constants.SEMICOLON);
                app = this.getApp(node);
                host = this.getHost(node);

                String detail = Constants.APP_APPENDER_STOP;
                LogCollectionStatus status = LogCollectionStatus.STOPPED;

                if (datas[0].equals(Constants.APP_APPENDER_RESTART_KEY)) {
                    // 如果是kafka appender restart
                    detail = Constants.APP_APPENDER_RESTART;
                    status = LogCollectionStatus.RUNNING;
                }

                info = this.buildMsg(DateUtil.format(new Date(Long.parseLong(datas[1])), DateUtil.YYYYMMDDHHMMSS), app,
                        this.getHost(node), datas[2], detail);

                // add to the queue
                this.rabbitmqService.sendMessage(info, this.zkClient.readData(Constants.ROOT_PATH_PERSISTENT + Constants.SLASH + app + Constants.SLASH + host).toString().split(Constants.SEMICOLON)[0]);
                LOGGER.info(info);
                this.appInfoService.update(host, app, Constants.ZK_NODE_TYPE_EPHEMERAL, status);
                break;
        }
    }

    /**
     * 根据node获取app
     * @param node
     * @return
     */
    private String getApp(String node) {
        String tmp = node.substring(0, node.lastIndexOf(Constants.SLASH));
        return this.getLast(tmp);
    }

    /**
     * 根据node获取host
     * @param node
     * @return
     */
    private String getHost(String node) {
        return this.getLast(node);
    }

    /**
     * 返回末尾字符串
     * @param line
     * @return
     */
    private String getLast(String line) {
        return line.substring(line.lastIndexOf(Constants.SLASH) + 1);
    }

    /**
     * 构造报警msg
     * @param time
     * @param app
     * @param host
     * @param deploy
     * @param msg
     * @return
     */
    private String buildMsg(String time, String app, String host, String deploy, String msg) {
        AlertDto alertDto = new AlertDto(time, app, host, deploy, msg);
        return alertDto.toString();
    }

    /**
     * 根据app和host计算LogCollectionStatus
     * @param app
     * @param host
     * @return
     */
    public LogCollectionStatus calLogCollectionStatus(String app, String host) {
        String[] datas = this.zkClient.readData(Constants.ROOT_PATH_EPHEMERAL + Constants.SLASH + app + Constants.SLASH + host).toString().split(Constants.SEMICOLON);
        if (datas[0].equals(Constants.APPENDER_INIT_DATA) || datas[0].equals(Constants.APP_APPENDER_RESTART_KEY)) {
            return LogCollectionStatus.RUNNING;
        }
        return LogCollectionStatus.STOPPED;
    }
}
