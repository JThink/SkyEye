package com.jthink.skyeye.base.sql;

import com.jthink.skyeye.base.constant.Constants;

import java.io.Serializable;
import java.util.List;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc order by
 * @date 2016-11-29 20:18:24
 */
public class OrderBy implements Serializable {

    private static final long serialVersionUID = 1L;
    // 排序条件
    private List<ConditionOrder> conditions;

    public List<ConditionOrder> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionOrder> conditions) {
        this.conditions = conditions;
    }

    @Override
    public String toString() {

        StringBuffer str = new StringBuffer();
        if (this.conditions != null && !this.conditions.isEmpty()) {
            str.append("ORDER BY ").append(this.conditions.get(0).toString());
            for (int i = 1; i < this.conditions.size(); i++) {
                str.append(Constants.COMMA).append(this.conditions.get(i).toString());
            }
        }
        return str.toString();
    }
}