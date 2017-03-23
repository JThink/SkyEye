package com.jthink.skyeye.trace.dto;

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

    private String zkServers;

    public RegisterDto() {

    }

    public RegisterDto(String app, String host, String zkServers) {
        this.app = app;
        this.host = host;
        this.zkServers = zkServers;
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

    public String getZkServers() {
        return zkServers;
    }

    public RegisterDto setZkServers(String zkServers) {
        this.zkServers = zkServers;
        return this;
    }
}
