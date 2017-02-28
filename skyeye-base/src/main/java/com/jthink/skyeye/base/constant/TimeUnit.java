package com.jthink.skyeye.base.constant;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 
 * @date 2016-10-13 08:59:52
 */
public enum TimeUnit {

    SECOND("s", "秒"),
    MINUTE("m", "分"),
    HOUR("h", "时"),
    DAY("d", "天");

    private String symbol;

    private String label;

    TimeUnit(String symbol, String label) {
        this.symbol = symbol;
        this.label = label;
    }

    public String symbol() {
        return symbol;
    }


    public String label() {
        return label;
    }

    @Override
    public String toString() {
        return symbol + "\t" + label;
    }

}
