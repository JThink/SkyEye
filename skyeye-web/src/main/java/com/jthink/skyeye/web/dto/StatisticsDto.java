package com.jthink.skyeye.web.dto;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 实时统计的数据返回的
 * @date 2016-11-30 15:15:06
 */
public class StatisticsDto {

    private String name;

    private String time;

    private int succ;

    private int fail;

    public StatisticsDto() {
    }

    public StatisticsDto(String name, String time, int succ, int fail) {
        this.name = name;
        this.time = time;
        this.succ = succ;
        this.fail = fail;
    }

    public int getSucc() {
        return succ;
    }

    public void setSucc(int succ) {
        this.succ = succ;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getFail() {
        return fail;
    }

    public void setFail(int fail) {
        this.fail = fail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
