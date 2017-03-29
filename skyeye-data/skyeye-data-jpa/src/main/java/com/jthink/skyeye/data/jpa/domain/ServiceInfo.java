package com.jthink.skyeye.data.jpa.domain;

import com.jthink.skyeye.data.jpa.pk.ServiceInfoPK;
import org.springframework.data.annotation.Id;

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

    @Id
    private ServiceInfoPK serviceInfoPK;
    // 为该服务分配的id, 每个iface作为一个service，同一个iface有1或多个method, 该id设置为serviceId（即，iface_method）
    @Column(name = "id", nullable = false)
    private String id;
    // 从zk分配的哪个服务采集而来
    @Column(name = "from", nullable = false)
    private String from;

    public ServiceInfoPK getServiceInfoPK() {
        return serviceInfoPK;
    }

    public ServiceInfo setServiceInfoPK(ServiceInfoPK serviceInfoPK) {
        this.serviceInfoPK = serviceInfoPK;
        return this;
    }

    public String getId() {
        return id;
    }

    public ServiceInfo setId(String id) {
        this.id = id;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public ServiceInfo setFrom(String from) {
        this.from = from;
        return this;
    }
}
