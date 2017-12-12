package com.jthink.skyeye.data.jpa.domain;

import javax.persistence.*;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 监控模板, 一段时间内(window, 时间窗口)响应时间超过某个阈值(cost)的量占总量的百分比大于某一个值(threshold)，进行报警
 * @date 2017-12-08 09:57:28
 */
@Entity
@Table(name = "monitor_template")
public class MonitorTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;
    // 模板名字
    @Column(name = "name", nullable = false)
    private String name;
    // 时间窗口，表示监控的数据往前推多少时间，单位是分钟
    @Column(name = "window", nullable = false)
    private Integer window;
    // 占比阈值
    @Column(name = "threshold", nullable = false)
    private double threshold;
    // 响应时间
    @Column(name = "cost", nullable = false)
    private String cost;
    // 是否是预置的模板（预置的模板不能删除），1：预置，0：非预置
    @Column(name = "preset", nullable = false)
    private Integer preset;

    public Integer getId() {
        return id;
    }

    public MonitorTemplate setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public MonitorTemplate setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getWindow() {
        return window;
    }

    public MonitorTemplate setWindow(Integer window) {
        this.window = window;
        return this;
    }

    public double getThreshold() {
        return threshold;
    }

    public MonitorTemplate setThreshold(double threshold) {
        this.threshold = threshold;
        return this;
    }

    public String getCost() {
        return cost;
    }

    public MonitorTemplate setCost(String cost) {
        this.cost = cost;
        return this;
    }

    public Integer getPreset() {
        return preset;
    }

    public MonitorTemplate setPreset(Integer preset) {
        this.preset = preset;
        return this;
    }
}
