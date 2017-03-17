package com.jthink.skyeye.data.jpa.dto;

/**
 * 项目名:　 monitor-center
 * 包名: 　　com.jthink.skyeye.data.jpa.dto
 * 创建日期: 17-2-27 上午11:00
 * Copyright (c) 2017, 银联智惠 All Rights Reserved.
 * 创建者: 　yuxiaof
 * 描述:
 * <p>
 * 修改日期:  | 修改人:  | 修改原因:
 */
public class TraceProjectInfoDto {

    private String projectName;
    private Integer projectId;
    private String remark;


    public TraceProjectInfoDto() {
    }

    public TraceProjectInfoDto(String projectName, Integer projectId, String remark) {
        this.projectName = projectName;
        this.projectId = projectId;
        this.remark = remark;
    }

    @Override
    public String toString() {

        return projectName + "\t" +
                projectId + "\t" +
                remark;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
