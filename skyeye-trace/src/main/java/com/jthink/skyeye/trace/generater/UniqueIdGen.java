package com.jthink.skyeye.trace.generater;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 唯一ID生成器，用来生成traceID和spanID
 * @date 2017-03-24 11:25:31
 */
public class UniqueIdGen implements IdGen {

    /**
     * 利用twitter的snowflake（做了些微修改）算法来实现
     * @return
     */
    @Override
    public String nextId() {
        return null;
    }
}
