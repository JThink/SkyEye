package com.jthink.skyeye.client.logback.builder;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc ProducerRecord需要的key参数，根据该值进行分区
 * @date 2016-09-09 13:23:18
 */
public interface KeyBuilder<E> {

    /**
     * 生成ProducerRecord需要的key参数
     * @param e log event, ch.qos.logback.classic.spi.ILoggingEvent
     * @return
     */
    byte[] build(E e);

}
