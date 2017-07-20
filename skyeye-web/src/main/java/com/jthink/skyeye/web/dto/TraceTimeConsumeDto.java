package com.jthink.skyeye.web.dto;

import java.io.Serializable;

/**
 * @author Aiur
 * @version 0.0.1
 * @desc 对应 HBase 表 time_consume
 * @date 2017-03-30 15:11:06
 */
public class TraceTimeConsumeDto implements Serializable {

    private String iface;
    private String method;
    private Long startTime;
    private Long endTime;
    private String traceId;
    private Long consumeTime;
    private String rowKey;


    public Long getStartTime() {
        return startTime;
    }

    public TraceTimeConsumeDto setStartTime(Long startTime) {
        this.startTime = startTime;
        return this;
    }

    public Long getEndTime() {
        return endTime;
    }

    public TraceTimeConsumeDto setEndTime(Long endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getTraceId() {
        return traceId;
    }

    public TraceTimeConsumeDto setTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public Long getConsumeTime() {
        return consumeTime;
    }

    public TraceTimeConsumeDto setConsumeTime(Long consumeTime) {
        this.consumeTime = consumeTime;
        return this;
    }

    public String getIface() {
        return iface;
    }

    public TraceTimeConsumeDto setIface(String iface) {
        this.iface = iface;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public TraceTimeConsumeDto setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getRowKey() {
        return rowKey;
    }

    public TraceTimeConsumeDto setRowKey(String rowKey) {
        this.rowKey = rowKey;
        return this;
    }

    @Override
    public String toString() {

        return iface + "\t" +
                method + "\t" +
                startTime + "\t" +
                endTime + "\t" +
                traceId + "\t" +
                consumeTime + "\t" +
                rowKey;
    }


}
