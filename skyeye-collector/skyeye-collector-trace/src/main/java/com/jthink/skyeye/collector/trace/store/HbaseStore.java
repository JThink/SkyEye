package com.jthink.skyeye.collector.trace.store;

import com.google.common.collect.Lists;
import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.dapper.*;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc span 相关的信息存入hbase
 * @date 2017-02-17 09:47:42
 */
@Component
public class HbaseStore implements Store {

    private static final Logger LOGGER = LoggerFactory.getLogger(HbaseStore.class);

    @Override
    public Map<String, List<Put>> store(String spanJson, Span span) {
        // 将所有的Put返回到上游
        Map<String, List<Put>> puts = new HashMap<String, List<Put>>();
        if (span.getSample()) {
            // 区分出所有的annotation
            Map<String, Annotation> annotationMap = this.distinguishAnnotation(span.getAnnotations());
            Put spanPut = this.storeSpan(span, spanJson, annotationMap);
            Put tracePut = this.storeTrace(span, annotationMap);
            List<Put> annotationPuts = this.storeAnnotation(span, annotationMap);

            puts.put(Constants.TABLE_TRACE, Lists.newArrayList(spanPut));
            if (null != tracePut) {
                puts.put(Constants.TABLE_TIME_CONSUME, Lists.newArrayList(tracePut));
            }
            if (null != annotationPuts) {
                puts.put(Constants.TABLE_ANNOTATION, annotationPuts);
            }

        }
        return puts;
    }

    /**
     * 数据表trace, 保存一个跟踪链条的所有span信息
     * rowkey: traceId
     * columnFamily: span
     * qualifier: [spanId+c, spanId+s ...](有N个span就有N*2个, c/s表示是client还是server采集到的)
     * value: span json value
     * @param span
     * @param spanJson
     * @param annotationMap
     * @return
     */
    @Override
    public Put storeSpan(Span span, String spanJson, Map<String, Annotation> annotationMap) {
        String traceId = span.getTraceId();
        String spanId = span.getId();
        if (annotationMap.containsKey(AnnotationType.CS.symbol())) {
            // 如果是client
            spanId += NodeProperty.C.symbol();
        } else {
            // 如果是server
            spanId += NodeProperty.S.symbol();
        }
        Put put = new Put(Bytes.toBytes(traceId));
        put.addColumn(Bytes.toBytes(Constants.TABLE_TRACE_COLUMN_FAMILY), Bytes.toBytes(spanId), Bytes.toBytes(spanJson));

        return put;
    }

    /**
     * 索引表time_consume, 保存每个trace耗时
     * rowkey: serviceId + cs时间
     * columnFamily: trace
     * qualifier: traceId ...
     * value: 整个调用链条耗时
     * @param span
     * @param annotationMap
     * @return
     */
    @Override
    public Put storeTrace(Span span, Map<String, Annotation> annotationMap) {
        if (null == span.getParentId() && annotationMap.containsKey(AnnotationType.CS.symbol())) {
            // 是root span, 并且是client端
            Annotation csAnnotation = annotationMap.get(AnnotationType.CS.symbol());
            Annotation crAnnotation = annotationMap.get(AnnotationType.CR.symbol());

            long consumeTime = crAnnotation.getTimestamp() - csAnnotation.getTimestamp();
            String rowKey = span.getServiceId() + Constants.UNDER_LINE + csAnnotation.getTimestamp();
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes(Constants.TABLE_TIME_CONSUME_COLUMN_FAMILY), Bytes.toBytes(span.getTraceId()),
                    Bytes.toBytes(consumeTime));
            return put;
        }
        return null;
    }

    /**
     * 索引表annotation, 保存自定义的异常信息
     * rowkey: serviceId + ExceptionType + cs/sr时间
     * columnFamily: trace
     * qualifier: traceId ...
     * value: binaryAnnotation的value
     * @param span
     * @param annotationMap
     * @return
     */
    @Override
    public List<Put> storeAnnotation(Span span, Map<String, Annotation> annotationMap) {
        List<BinaryAnnotation> annotations = span.getBinaryAnnotations();
        if (null != annotations && annotations.size() != 0) {
            List<Put> puts = new ArrayList<Put>();
            // 如果有自定义异常
            for (BinaryAnnotation annotation : annotations) {
                String rowKey = span.getServiceId() + Constants.UNDER_LINE + annotation.getType()
                        + Constants.UNDER_LINE + this.getBinaryAnnotationTimestamp(annotationMap);
                Put put = new Put(Bytes.toBytes(rowKey));
                put.addColumn(Bytes.toBytes(Constants.TABLE_ANNOTATION_COLUMN_FAMILY), Bytes.toBytes(span.getTraceId()),
                        Bytes.toBytes(annotation.getValue() == null ? annotation.getType() : annotation.getValue()));
                puts.add(put);
            }
            return puts;
        }
        return null;
    }

    /**
     * 获取binaryAnnotation的发生时间戳，这里取的是客户端发送时间或者服务端接受时间（每个span要么是客户端，要么是服务端，这两个时间仅仅相差一个网络传输时间）
     * @param annotationMap
     * @return
     */
    private Long getBinaryAnnotationTimestamp(Map<String, Annotation> annotationMap) {
        Long timestamp = System.currentTimeMillis();
        if (annotationMap.containsKey(AnnotationType.CS.symbol())) {
            // cs
            timestamp = annotationMap.get(AnnotationType.CS.symbol()).getTimestamp();
        }
        if (annotationMap.containsKey(AnnotationType.SR.symbol())) {
            // sr
            timestamp = annotationMap.get(AnnotationType.SR.symbol()).getTimestamp();
        }
        return timestamp;
    }


    /**
     * 区分出不同的annotation
     * @param annotations
     * @return
     */
    private Map<String, Annotation> distinguishAnnotation(List<Annotation> annotations) {
        Map<String, Annotation> annotationMap = new HashMap<String, Annotation>();
        for (Annotation annotation : annotations) {
            annotationMap.put(annotation.getValue(), annotation);
        }
        return annotationMap;
    }

}
