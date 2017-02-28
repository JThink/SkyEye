package com.jthink.skyeye.base.sql;

import com.jthink.skyeye.base.constant.Constants;

import java.io.Serializable;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc from子句
 * @date 2016-11-29 21:10:31
 */
public class From implements Serializable {

    private static final long serialVersionUID = 1L;

    // 数据库名
    private String database;
    // 表名
    private String table;
    // 别名
    private String alias;

    public From() {

    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (this.table == null) {
            return null;
        }
        // 从数据库表查询
        if (this.database != null) {
            sb.append(this.database + "." + this.table);
        } else {
            sb.append(this.table);
        }

        if (this.alias != null) {
            sb.append(Constants.SPACE).append(this.alias);
        }
        return sb.toString();
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
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
