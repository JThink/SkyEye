package com.jthink.skyeye.data.dubbo;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-12-15 15:09:15
 */
@ConfigurationProperties(prefix = "spring.service.dubbo.registry")
public class DubboProperties {

    private String name;
    private String address;
    private boolean check;
    private boolean register;
    private String protocol;
    private Integer retries;
    private Integer timeOut;
    private String protocolName;
    private Integer payload;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isRegister() {
        return register;
    }

    public void setRegister(boolean register) {
        this.register = register;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public String getProtocolName() {
        return protocolName;
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    public Integer getPayload() {
        return payload;
    }

    public void setPayload(Integer payload) {
        this.payload = payload;
    }
}
