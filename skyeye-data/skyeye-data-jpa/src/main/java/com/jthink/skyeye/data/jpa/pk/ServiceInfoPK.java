package com.jthink.skyeye.data.jpa.pk;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc rpc服务注册表主键
 * @date 2017-03-29 15:19:46
 */
@Embeddable
public class ServiceInfoPK implements Serializable {

    // 服务的接口名
    @Column(name = "iface", nullable = false)
    private String iface;
    // 服务的方法名
    @Column(name = "method", nullable = false)
    private String method;

    public ServiceInfoPK() {

    }

    public ServiceInfoPK(String iface, String method) {
        this.iface = iface;
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceInfoPK that = (ServiceInfoPK) o;

        if (iface != null ? !iface.equals(that.iface) : that.iface != null) return false;
        return method != null ? method.equals(that.method) : that.method == null;
    }

    @Override
    public int hashCode() {
        int result = iface != null ? iface.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }

    public String getIface() {
        return iface;
    }

    public ServiceInfoPK setIface(String iface) {
        this.iface = iface;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public ServiceInfoPK setMethod(String method) {
        this.method = method;
        return this;
    }
}
