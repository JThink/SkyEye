package com.jthink.skyeye.data.jpa.dto;

/**
 * 项目名:　 monitor-center
 * 包名: 　　com.jthink.skyeye.data.jpa.dto
 * 创建日期: 16-12-7 上午10:18
 * Copyright (c) 2016, 银联智惠 All Rights Reserved.
 * 创建者: 　yuxiaof
 */
public class NameInfoDto {

    private String name;
    private String type;
    private String app;

    public NameInfoDto() {
    }

    public NameInfoDto(String name, String type, String app) {
        this.name = name;
        this.type = type;
        this.app = app;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }
}
