package com.jthink.skyeye.data.jpa.pk;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * 项目名:　 monitor-center
 * 包名: 　　com.jthink.skyeye.data.jpa.pk
 * 创建日期: 17-2-27 上午10:08
 * Copyright (c) 2017, 银联智惠 All Rights Reserved.
 * 创建者: 　yuxiaof
 * 描述:
 * 项目实例信息的主键
 * 修改日期:  | 修改人:  | 修改原因:
 */
@Embeddable
public class TraceProjectInstanceInfoPK implements Serializable {


    @Column(name = "host_mac")
    private String hostMac;
    @Column(name = "project_name")
    private String projectName;
    @Column(name = "instance_path")
    private String instancePath;

    public TraceProjectInstanceInfoPK() {
    }

    public TraceProjectInstanceInfoPK(String hostMac, String projectName, String instancePath) {
        this.hostMac = hostMac;
        this.projectName = projectName;
        this.instancePath = instancePath;
    }

    @Override
    public String toString() {

        return hostMac + "\t" +
                projectName + "\t" +
                instancePath;
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
}
