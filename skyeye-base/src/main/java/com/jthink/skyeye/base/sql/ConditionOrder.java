package com.jthink.skyeye.base.sql;

import com.jthink.skyeye.base.constant.Constants;

import java.io.Serializable;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc order by 的条件描述
 * @date 2016-11-29 20:15:41
 */
public class ConditionOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    // 列名
    private String column;

    // 排序
    private OrderDesc desc;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public OrderDesc getDesc() {
        return desc;
    }

    public void setDesc(OrderDesc desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return this.column + Constants.SPACE + this.desc.name();
    }
}
