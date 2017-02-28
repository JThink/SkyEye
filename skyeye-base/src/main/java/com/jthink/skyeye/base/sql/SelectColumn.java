package com.jthink.skyeye.base.sql;

import com.jthink.skyeye.base.constant.Constants;

import java.io.Serializable;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc select投影的列
 * @date 2016-11-29 21:10:31
 */
public class SelectColumn implements Serializable {

    private static final long serialVersionUID = 1L;

    // 表名
    private String table;
    // 列名
    private String column;
    // 去重
    private boolean distinct;
    // 加在列上的函数
    private Func func;
    // 别名
    private String alias;

    public SelectColumn() {

    }

    public SelectColumn(String column) {
        this.column = column;
    }

    public SelectColumn(String table, String column) {
        this.table = table;
        this.column = column;
    }

    @Override
    public String toString() {
        String str = this.column;
        if (this.table != null) {
            str = this.table + Constants.POINT + this.column;
        }
        if (this.distinct) {
            str = "DISTINCT " + str;
        }
        if (this.func != null) {
            if (Func.COLLECT.equals(this.func)) {
                str = "CONCAT_WS(',', COLLECT_SET(" + str + "))";
            } else {
                str = func.name() + "(" + str + ")";
            }
        }
        if (this.alias != null) {
            str += " AS " + this.alias;
        }
        return str;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public Func getFunc() {
        return func;
    }

    public void setFunc(Func func) {
        this.func = func;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
