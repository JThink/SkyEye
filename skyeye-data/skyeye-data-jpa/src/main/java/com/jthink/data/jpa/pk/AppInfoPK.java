package com.jthink.skyeye.data.jpa.pk;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-10-09 09:59:45
 */
@Embeddable
public class AppInfoPK implements Serializable {

    @Column(name = "host", nullable = false)
    private String host;
    @Column(name = "app", nullable = false)
    private String app;
    @Column(name = "type", nullable = false)
    private int type;

    public AppInfoPK() {
    }

    public AppInfoPK(String host, String app, int type) {
        this.host = host;
        this.app = app;
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppInfoPK appInfoPK = (AppInfoPK) o;

        if (type != appInfoPK.type) return false;
        if (host != null ? !host.equals(appInfoPK.host) : appInfoPK.host != null) return false;
        return app != null ? app.equals(appInfoPK.app) : appInfoPK.app == null;

    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + (app != null ? app.hashCode() : 0);
        result = 31 * result + type;
        return result;
    }
}
