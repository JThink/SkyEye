package com.jthink.skyeye.base.sql;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc sql构造的自定义异常
 * @date 2016-11-29 21:10:31
 */
public class SqlBuildException extends Exception {

    public SqlBuildException() {
        super();
    }

    public SqlBuildException(String message) {
        super(message);
    }

    public SqlBuildException(Throwable cause) {
        super(cause);
    }

    public SqlBuildException(String message, Throwable cause) {
        super(message, cause);
    }
}
