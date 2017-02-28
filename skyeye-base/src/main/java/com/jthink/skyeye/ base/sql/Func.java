package com.jthink.skyeye.base.sql;

import java.io.Serializable;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc sql支持在select语句的函数
 * @date 2016-11-29 21:10:31
 */
public enum Func implements Serializable {

    // 可以扩展
    SUM(),
    MAX(),
    MIN(),
    COUNT(),
    COLLECT()
}
