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
 * @desc group by子句
 * @date 2016-11-29 21:10:31
 */
public class GroupBy implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> columns;

    /**
     * 添加group by的列
     * @param column
     */
    public void addColumn(String column) {
        if(this.columns == null) {
            this.columns = new ArrayList<String>();
        }
        this.columns.add(column);
    }

    @Override
    public String toString() {
        if (this.columns == null || this.columns.isEmpty()) {
            return Constants.EMPTY_STR;
        }
        StringBuffer str = new StringBuffer("GROUP BY ");
        boolean first = true;
        for (String column : columns) {
            if (first) {
                first = false;
            } else {
                str.append(Constants.COMMA);
            }
            str.append(column);
        }
        return str.toString();
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
}
