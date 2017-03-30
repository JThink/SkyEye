package com.jthink.skyeye.data.jpa.domain;

import com.jthink.skyeye.data.jpa.pk.ServiceInfoPK;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc rpc服务注册表
 * @date 2017-02-23 15:42:31
 */
@Entity
@Table(name = "service_info")
public class ServiceInfo {

    @Id
    private ServiceInfoPK serviceInfoPK;
    // 为该服务分配的id, 每个iface作为一个service，同一个iface有1或多个method, 该id设置为serviceId（即，iface_method）
    @Column(name = "sid", nullable = false)
    private String sid;

    public ServiceInfoPK getServiceInfoPK() {
        return serviceInfoPK;
    }

    public ServiceInfo setServiceInfoPK(ServiceInfoPK serviceInfoPK) {
        this.serviceInfoPK = serviceInfoPK;
        return this;
    }

    public String getSid() {
        return sid;
    }

    public ServiceInfo setSid(String sid) {
        this.sid = sid;
        return this;
    }

}
