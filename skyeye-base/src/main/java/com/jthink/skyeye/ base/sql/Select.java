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
 * @desc select原语
 * @date 2016-11-29 21:10:31
 */
public class Select implements Serializable {

    private static final long serialVersionUID = 1L;

    // 数据表名
    private String table;

    // 查询列描述
    private List<SelectColumn> columns;

    /**
     * 添加一个查询的列
     * @param column
     */
    public void addColumn(String column) {
        if (this.columns == null) {
            this.columns = new ArrayList<SelectColumn>();
        }
        SelectColumn selectColumn = new SelectColumn(column);
        this.columns.add(selectColumn);
    }

    /**
     * 添加一个查询的列
     * @param table
     * @param column
     */
    public void addColumn(String table, String column) {
        this.addColumn(new SelectColumn(table, column));
    }

    /**
     * 添加一个查询的列
     * @param column
     */
    public void addColumn(SelectColumn column) {
        if(this.columns == null) {
            this.columns = new ArrayList<SelectColumn>();
        }
        this.columns.add(column);
    }

    /**
     * 覆写toString, 生成局部sql
     * @return
     */
    @Override
    public String toString() {
        StringBuffer select = new StringBuffer("SELECT ");
        if (columns == null || columns.isEmpty()) {
            if (this.table != null) {
                select.append(this.table).append(Constants.POINT);
            }
            select.append("*");
        } else {
            boolean first = true;
            for (SelectColumn column : columns) {
                if (first) {
                    first = false;
                } else {
                    select.append(Constants.COMMA);
                }
                select.append(column.toString());
            }
        }
        return select.toString();
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<SelectColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<SelectColumn> columns) {
        this.columns = columns;
    }

}
