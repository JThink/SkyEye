package com.jthink.skyeye.base.constant;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-11-17 10:02:53
 */
public enum NameInfoType {

    API(Constants.API, "api名称"),
    ACCOUNT(Constants.ACCOUNT, "account名称"),
    MIDDLEWARE(Constants.MIDDLEWARE, "中间件名称"),
    THIRD(Constants.THIRD, "第三方名称");

    private String symbol;

    private String label;

    private NameInfoType(String symbol, String label) {
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
