package com.jthink.skyeye.base.constant;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 日志采集状态
 * @date 2016-10-09 11:00:37
 */
public enum LogCollectionStatus {

    RUNNING(Constants.LOG_COLLECTION_RUNNING, "正常运行"),
    STOPPED(Constants.LOG_COLLECTION_STOPPED, "停止运行"),
    HISTORY(Constants.LOG_COLLECTION_HISTORY, "历史运行");

    private String symbol;

    private String label;

    private LogCollectionStatus(String symbol, String label) {
        this.symbol = symbol;
        this.label = label;
    }

    public String symbol() {
        return this.symbol;
    }

    public String label() {
        return this.label;
    }
}
