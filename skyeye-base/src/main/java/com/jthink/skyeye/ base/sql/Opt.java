package com.jthink.skyeye.base.sql;

import com.jthink.skyeye.base.constant.Constants;

import java.io.Serializable;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 6/7/16
 */
public enum Opt implements Serializable {

    TRUE(Constants.TRUE, "是"),
    FALSE(Constants.FALSE, "否"),
    EQUAL(Constants.EQUAL, "等于"),
    GREATER(Constants.GREATER, "大于"),
    GREATER_EQUAL(Constants.GEQUAL, "大于等于"),
    LESS(Constants.LESS, "小于"),
    LESS_EQUAL(Constants.LEQUAL, "小于等于"),
    BETWEEN(Constants.BETWEEN, "闭区间"),
    IN(Constants.IN, "包含"),
    NOT_IN(Constants.NOT_IN, "包含"),

    LIKE(Constants.LIKE, "模糊匹配"),
    NOT_LIKE(Constants.NOT_LIKE, "模糊匹配"),
    IS_NULL(Constants.IS_NULL, "为空");

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
        return this.label;
    }
}
