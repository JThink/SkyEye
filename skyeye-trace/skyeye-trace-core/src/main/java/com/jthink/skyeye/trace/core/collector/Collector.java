package com.jthink.skyeye.trace.core.collector;

import com.jthink.skyeye.base.dapper.Span;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc span的采集器接口
 * @date 2017-02-15 14:21:53
 */
public interface Collector {

    /**
     * 采集
     */
    void collect(Span span);
}
