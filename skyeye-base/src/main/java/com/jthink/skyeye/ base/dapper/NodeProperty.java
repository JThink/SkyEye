package com.jthink.skyeye.base.dapper;

import com.jthink.skyeye.base.constant.Constants;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 节点性质
 * @date 2017-02-17 17:28:01
 */
public enum NodeProperty {

    C(Constants.CLIENT_KEY, Constants.CLIENT_VALUE),
    S(Constants.SERVER_KEY, Constants.SERVER_VALUE);

    private String symbol;

    private String label;

    private NodeProperty(String symbol, String label) {
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
