package com.jthink.skyeye.data.jpa.domain;

import com.jthink.skyeye.data.jpa.pk.NameInfoPK;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc name info entity
 * @date 2016-11-17 09:17:19
 */
@Entity
@Table(name = "name_info")
public class NameInfo {

    // 联合主键
    @Id
    private NameInfoPK nameInfoPK;
    // 来自哪个app
    @Column(name = "app")
    private String app;

    public NameInfoPK getNameInfoPK() {
        return nameInfoPK;
    }

    public void setNameInfoPK(NameInfoPK nameInfoPK) {
        this.nameInfoPK = nameInfoPK;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }
}
