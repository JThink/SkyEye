package com.jthink.skyeye.data.jpa.domain;

import com.jthink.skyeye.data.jpa.pk.TraceProjectInstanceInfoPK;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 项目名:　 monitor-center
 * 包名: 　　com.jthink.skyeye.data.jpa.domain
 * 创建日期: 17-2-27 上午10:15
 * Copyright (c) 2017, 银联智惠 All Rights Reserved.
 * 创建者: 　yuxiaof
 * 描述:
 * 项目实例信息
 * 修改日期:  | 修改人:  | 修改原因:
 */
@Entity
@Table(name = "trace_project_instance_info")
public class TraceProjectInstanceInfo {

    @Id
    private TraceProjectInstanceInfoPK primary;

    @Column(name = "host_name")
    private String hostName;
    @Column(name = "host_id")
    private Integer hostId;
    @Column(name = "project_id")
    private Integer projectId;
    @Column(name = "instance_id")
    private Integer instanceId;
    @Column(name = "remark")
    private String remark = "";

    public TraceProjectInstanceInfo() {
    }

    public TraceProjectInstanceInfo(TraceProjectInstanceInfoPK primary, String hostName, Integer hostId, Integer
            projectId, Integer instanceId, String remark) {
        this.primary = primary;
        this.hostName = hostName;
        this.hostId = hostId;
        this.projectId = projectId;
        this.instanceId = instanceId;
        this.remark = remark;
    }

    @Override
    public String toString() {

        return primary + "\t" +
                hostName + "\t" +
                hostId + "\t" +
                projectId + "\t" +
                instanceId + "\t" +
                remark;
    }

    public TraceProjectInstanceInfoPK getPrimary() {
        return primary;
    }

    public void setPrimary(TraceProjectInstanceInfoPK primary) {
        this.primary = primary;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Integer instanceId) {
        this.instanceId = instanceId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
