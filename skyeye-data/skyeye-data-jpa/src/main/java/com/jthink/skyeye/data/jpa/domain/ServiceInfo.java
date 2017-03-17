package com.jthink.skyeye.data.jpa.domain;

import javax.persistence.Column;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc rpc服务注册表
 * @date 2017-02-23 15:42:31
 */
public class ServiceInfo {

    // 服务的接口名
    @Column(name = "iface", nullable = false)
    private String iface;
    // 服务的方法名
    @Column(name = "method", nullable = false)
    private String method;
    // 为该服务分配的id, 每个iface作为一个service，同一个iface有1或多个method
    @Column(name = "id", nullable = false)
    private String id;

    public String getIface() {
        return iface;
    }

    public void setIface(String iface) {
        this.iface = iface;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
