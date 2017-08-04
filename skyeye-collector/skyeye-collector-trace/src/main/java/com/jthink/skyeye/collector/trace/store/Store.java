package com.jthink.skyeye.collector.trace.store;

import com.jthink.skyeye.base.dapper.Annotation;
import com.jthink.skyeye.base.dapper.Span;
import org.apache.hadoop.hbase.client.Put;

import java.util.List;
import java.util.Map;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc span信息存储入库接口
 * @date 2017-02-17 09:46:22
 */
public interface Store {

    /**
     * 存储span
     * @param span
     * @param spanJson
     * @param annotationMap
     * @return
     */
    Put storeSpan(Span span, String spanJson, Map<String, Annotation> annotationMap);

    /**
     * 存储trace, 保存每个trace耗时
     * @param span
     * @param annotationMap
     * @return
     */
    Put storeTrace(Span span, Map<String, Annotation> annotationMap);

    /**
     * 存储annotation, 保存自定义的异常信息
     * @param span
     * @param annotationMap
     * @return
     */
    List<Put> storeAnnotation(Span span, Map<String, Annotation> annotationMap);

    /**
     * 存储所有的信息
     * @param spanJson
     * @param span
     * @return
     */
    Map<String, List<Put>> store(String spanJson, Span span);
}
