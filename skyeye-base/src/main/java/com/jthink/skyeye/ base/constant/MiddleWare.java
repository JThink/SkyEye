package com.jthink.skyeye.base.constant;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 支持监控的中间件
 * @date 2016-11-14 10:08:32
 */
public enum MiddleWare {

    HBASE(Constants.MIDDLEWARE_HBASE, "hbase"),
    MONGO(Constants.MIDDLEWARE_MONGO, "mongo");

    private String symbol;

    private String label;

    private MiddleWare(String symbol, String label) {
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
