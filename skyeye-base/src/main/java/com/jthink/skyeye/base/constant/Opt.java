package com.jthink.skyeye.base.constant;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 操作符
 * @date 2016-10-13 08:59:52
 */
public enum Opt {

    EQUAL(Constants.EQUAL, "等于"),
    GREATER(Constants.GREATER, "大于"),
    GREATER_EQUAL(Constants.GEQUAL, "大于等于"),
    LESS(Constants.LESS, "小于"),
    LESS_EQUAL(Constants.LEQUAL, "小于等于");

    private String symbol;

    private String label;

    private Opt(String symbol, String label) {
        this.symbol = symbol;
        this.label = label;
    }

    public String symbol() {
        return this.symbol;
    }

    public String label() {
        return label;
    }
}
