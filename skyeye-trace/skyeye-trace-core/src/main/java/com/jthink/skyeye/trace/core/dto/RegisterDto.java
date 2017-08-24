package com.jthink.skyeye.trace.core.dto;

import org.I0Itec.zkclient.ZkClient;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 向注册中心注册的信息dto
 * @date 2017-03-23 11:04:16
 */
public class RegisterDto {

    private String app;

    private String host;

    private ZkClient zkClient;

    public RegisterDto() {

    }

    public RegisterDto(String app, String host, ZkClient zkClient) {
        this.app = app;
        this.host = host;
        this.zkClient = zkClient;
    }

    public String getApp() {
        return app;
    }

    public RegisterDto setApp(String app) {
        this.app = app;
        return this;
    }

    public String getHost() {
        return host;
    }

    public RegisterDto setHost(String host) {
        this.host = host;
        return this;
    }

    public ZkClient getZkClient() {
        return zkClient;
    }

    public RegisterDto setZkClient(ZkClient zkClient) {
        this.zkClient = zkClient;
        return this;
    }
}
