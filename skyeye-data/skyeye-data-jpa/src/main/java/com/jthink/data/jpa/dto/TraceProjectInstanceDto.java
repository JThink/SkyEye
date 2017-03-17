package com.jthink.skyeye.data.jpa.dto;

/**
 * 项目名:　 monitor-center
 * 包名: 　　com.jthink.skyeye.data.jpa.dto
 * 创建日期: 17-2-27 上午11:01
 * Copyright (c) 2017, 银联智惠 All Rights Reserved.
 * 创建者: 　yuxiaof
 * 描述:
 * <p>
 * 修改日期:  | 修改人:  | 修改原因:
 */
public class TraceProjectInstanceDto {

    private String hostName;
    private String hostMac;
    private String projectName;
    private String instancePath;
    private Integer hostId;
    private Integer projectId;
    private Integer instanceId;
    private String remark;


    public TraceProjectInstanceDto() {
    }

    public TraceProjectInstanceDto(String hostName, String hostMac, String projectName, String instancePath, Integer
            hostId, Integer projectId, Integer instanceId, String remark) {
        this.hostName = hostName;
        this.hostMac = hostMac;
        this.projectName = projectName;
        this.instancePath = instancePath;
        this.hostId = hostId;
        this.projectId = projectId;
        this.instanceId = instanceId;
        this.remark = remark;
    }

    @Override
    public String toString() {

        return hostName + "\t" +
                hostMac + "\t" +
                projectName + "\t" +
                instancePath + "\t" +
                hostId + "\t" +
                projectId + "\t" +
                instanceId + "\t" +
                remark;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostMac() {
        return hostMac;
    }

    public void setHostMac(String hostMac) {
        this.hostMac = hostMac;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getInstancePath() {
        return instancePath;
    }

    public void setInstancePath(String instancePath) {
        this.instancePath = instancePath;
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
