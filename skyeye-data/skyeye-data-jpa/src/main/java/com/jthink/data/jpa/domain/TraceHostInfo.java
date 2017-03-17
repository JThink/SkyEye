package com.jthink.skyeye.data.jpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 项目名:　 monitor-center
 * 包名: 　　com.jthink.skyeye.data.jpa.domain
 * 创建日期: 17-2-27 上午9:59
 * Copyright (c) 2017, 银联智惠 All Rights Reserved.
 * 创建者: 　yuxiaof
 * 描述:
 * 主机信息
 * 修改日期:  | 修改人:  | 修改原因:
 */
@Entity
@Table(name = "trace_host_info")
public class TraceHostInfo implements Serializable {


    @Id
    private String mac;
    @Column(name = "host_id")
    private Integer hostId;
    @Column(name = "host_name")
    private String hostName;
    @Column(name = "remark")
    private String remark = "";


    public TraceHostInfo() {
    }

    public TraceHostInfo(Integer hostId, String hostName, String mac, String remark) {
        this.hostId = hostId;
        this.hostName = hostName;
        this.mac = mac;
        this.remark = remark;
    }

    @Override
    public String toString() {

        return mac + "\t" +
                hostId + "\t" +
                hostName + "\t" +
                remark;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
