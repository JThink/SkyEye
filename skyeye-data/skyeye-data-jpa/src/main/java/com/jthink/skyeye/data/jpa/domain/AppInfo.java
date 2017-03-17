package com.jthink.skyeye.data.jpa.domain;

import com.jthink.skyeye.data.jpa.pk.AppInfoPK;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc app info
 * @date 2016-10-09 09:57:28
 */
@Entity
@Table(name = "app_info")
public class AppInfo {

    @Id
    private AppInfoPK appInfoPK;

    @Column(name = "status")
    private String status;

    @Column(name = "deploy", nullable = false)
    private String deploy;

    public AppInfoPK getAppInfoPK() {
        return appInfoPK;
    }

    public void setAppInfoPK(AppInfoPK appInfoPK) {
        this.appInfoPK = appInfoPK;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeploy() {
        return deploy;
    }

    public void setDeploy(String deploy) {
        this.deploy = deploy;
    }

}
