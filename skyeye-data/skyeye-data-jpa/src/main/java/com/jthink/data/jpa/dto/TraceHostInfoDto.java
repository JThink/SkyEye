package com.jthink.skyeye.data.jpa.dto;

/**
 * 项目名:　 monitor-center
 * 包名: 　　com.jthink.skyeye.data.jpa.dto
 * 创建日期: 17-2-27 上午10:34
 * Copyright (c) 2017, 银联智惠 All Rights Reserved.
 * 创建者: 　yuxiaof
 * 描述:
 * <p>
 * 修改日期:  | 修改人:  | 修改原因:
 */
public class TraceHostInfoDto {

    private String mac;
    private String hostName;
    private Integer hostId;
    private String remark;

    public TraceHostInfoDto() {
    }

    public TraceHostInfoDto(String mac, String hostName, Integer hostId, String remark) {
        this.mac = mac;
        this.hostName = hostName;
        this.hostId = hostId;
        this.remark = remark;
    }

    @Override
    public String toString() {

        return mac + "\t" +
                hostName + "\t" +
                hostId + "\t" +
                remark;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
