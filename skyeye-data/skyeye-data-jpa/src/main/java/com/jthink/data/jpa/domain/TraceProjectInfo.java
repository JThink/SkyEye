package com.jthink.skyeye.data.jpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 项目名:　 monitor-center
 * 包名: 　　com.jthink.skyeye.data.jpa.domain
 * 创建日期: 17-2-27 上午10:04
 * Copyright (c) 2017, 银联智惠 All Rights Reserved.
 * 创建者: 　yuxiaof
 * 描述:
 * <p>
 * 修改日期:  | 修改人:  | 修改原因:
 */
@Entity
@Table(name = "trace_project_info")
public class TraceProjectInfo implements Serializable {

    @Id
    private String projectName;
    @Column(name = "project_id")
    private Integer projectId;
    @Column(name = "remark")
    private String remark = "";

    public TraceProjectInfo() {
    }

    public TraceProjectInfo(Integer projectId, String projectName, String remark) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.remark = remark;
    }

    @Override
    public String toString() {

        return projectName + "\t" +
                projectId + "\t" +
                remark;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}