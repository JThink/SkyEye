package com.jthink.skyeye.base.dapper;

import java.io.Serializable;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc endpoint定义
 * @date 2016-11-04 17:32:03
 */
public class EndPoint implements Serializable {

    // rpc服务启动的ip
    private String ip;
    // rpc服务启动的端口
    private Integer port;

    public EndPoint() {

    }

    public EndPoint(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EndPoint endPoint = (EndPoint) o;

        if (ip != null ? !ip.equals(endPoint.ip) : endPoint.ip != null) return false;
        return port != null ? port.equals(endPoint.port) : endPoint.port == null;
    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + (port != null ? port.hashCode() : 0);
        return result;
    }

    public String getIp() {
        return ip;
    }

    public EndPoint setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public EndPoint setPort(Integer port) {
        this.port = port;
        return this;
    }
}
