package com.jthink.skyeye.base.constant;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc rpc type类型
 * @date 2017-03-27 10:38:32
 */
public enum RpcType {

    none(Constants.RPC_TYPE_NONE, "none"),
    dubbo(Constants.RPC_TYPE_DUBBO, "dubbo"),
    sc(Constants.RPC_TYPE_SC, "spring-cloud");

    private String symbol;

    private String label;

    private RpcType(String symbol, String label) {
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
