package com.jthink.skyeye.base.sql;

import com.jthink.skyeye.base.constant.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc where子句, 复合条件的组合
 * @date 2016-11-29 21:10:31
 */
public class Where implements Serializable {

    private static final long serialVersionUID = 1L;

    // 条件
    private List<Condition> conditions;

    public void addCondition(Condition condition) {
        if (this.conditions == null) {
            this.conditions = new ArrayList<Condition>();
        }
        this.conditions.add(condition);
    }

    @Override
    public String toString() {
        if ((this.conditions == null || this.conditions.isEmpty())) {
            // 没有条件
            return Constants.EMPTY_STR;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("WHERE ");
        if (this.conditions != null) {
            sb.append(Constants.LEFT_S_BRACKETS).append(this.conditions.get(0).toString());
            for(int i = 1; i < this.conditions.size(); i++) {
                if (Constants.OR.equals(this.conditions.get(i).getFlag())) {
                    sb.append(Constants.OR_SPACE).append(this.conditions.get(i).toString());
                } else {
                    sb.append(Constants.AND_SPACE).append(this.conditions.get(i).toString());
                }
            }
            sb.append(Constants.RIGHT_S_BRACKETS);
        }
        return sb.toString();
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
