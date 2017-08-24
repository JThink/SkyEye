package com.jthink.skyeye.trace.core.trace;

import com.jthink.skyeye.base.dapper.*;
import com.jthink.skyeye.trace.core.collector.Collector;
import com.jthink.skyeye.trace.core.collector.KafkaCollector;
import com.jthink.skyeye.trace.core.sampler.Sampler;
import com.jthink.skyeye.trace.core.generater.IncrementIdGen;
import com.jthink.skyeye.trace.core.generater.UniqueIdGen;
import com.jthink.skyeye.trace.core.sampler.PercentageSampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 具体的跟踪器(单例)
 * @date 2016-12-16 09:45:28
 */
public class Tracer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tracer.class);

    // 保存parentSpan
    private ThreadLocal<Span> parentSpan = new ThreadLocal<Span>();

    // 单例
    private static volatile Tracer tracer;

    // 采样器实例
    private Sampler sampler = new PercentageSampler();

    // 收集器实例
    private Collector collector = new KafkaCollector();

    // 保证单例
    private Tracer() {

    }

    /**
     * double check 保证实例化的Tracer对象为单例
     * @return
     */
    public static Tracer getInstance() {
        if (tracer == null) {
            synchronized(Tracer.class) {
                if (tracer == null) {
                    tracer = new Tracer();
                }
            }
        }
        return tracer;
    }

    /**
     * 是否初始化
     * @return
     */
    public static boolean isInstanced() {
        return tracer != null;
    }

    public void removeParentSpan() {
        this.parentSpan.remove();
    }

    public Span getParentSpan() {
        return this.parentSpan.get();
    }

    public void setParentSpan(Span span) {
        this.parentSpan.set(span);
    }

    /**
     * 构造span，参数通过上游或者parentSpan传递过来
     * @param traceId
     * @param parentId
     * @param id
     * @param name
     * @param isSample
     * @param serviceId
     * @return
     */
    public Span buildSpan(String traceId, String parentId, String id, String name, boolean isSample, String serviceId) {
        Span span = new Span();
        span.setId(id);
        span.setParentId(parentId);
        span.setName(name);
        span.setSample(isSample);
        span.setTraceId(traceId);
        span.setServiceId(serviceId);
        return span;
    }

    /**
     * 构造root span, 生成id, 并确定是否采样
     * @param name
     * @param serviceId
     * @return
     */
    public Span newSpan(String name, String serviceId) {
        boolean s = this.sampler.isCollect();
        Span span = new Span();
        String traceId = this.generateTraceId();
        String spanId = this.generateSpanId();
        span.setTraceId(s ? traceId : null);
        span.setId(s ? spanId : null);
        span.setName(name);
        span.setServiceId(serviceId);
        span.setSample(s);
        if (traceId.equals("-1") || spanId.equals("-1")) {
            span.setSample(false);
        }
        return span;
    }

    /**
     * 构造EndPoint
     * @return
     */
    public EndPoint buildEndPoint(String ip, Integer port) {
        return new EndPoint(ip, port);
    }

    /**
     * 生成spanId
     * @return
     */
    public String generateSpanId() {
        return UniqueIdGen.getInstance(Long.parseLong(IncrementIdGen.getId())).nextId();
    }

    /**
     * 生成traceId
     * @return
     */
    public String generateTraceId() {
        return UniqueIdGen.getInstance(Long.parseLong(IncrementIdGen.getId())).nextId();
    }

    /**
     * consumer向provider发送请求
     * @param span
     * @param endPoint
     * @param start
     */
    public void clientSend(Span span, EndPoint endPoint, long start) {
        Annotation annotation = this.buildAnnotation(endPoint, start, AnnotationType.CS);
        span.addAnnotation(annotation);
    }

    /**
     * provider接受到consumer的请求
     * @param span
     * @param endPoint
     * @param end
     */
    public void serverReceive(Span span, EndPoint endPoint, long end) {
        Annotation annotation = this.buildAnnotation(endPoint, end, AnnotationType.SR);
        span.addAnnotation(annotation);
    }

    /**
     * provider处理完业务逻辑响应consumer
     * @param span
     * @param endPoint
     * @param start
     */
    public void serverSend(Span span, EndPoint endPoint, long start) {
        Annotation annotation = this.buildAnnotation(endPoint, start, AnnotationType.SS);
        span.addAnnotation(annotation);
        this.collector.collect(span);
        LOGGER.info("SS, " + span.toString());
    }

    /**
     * consumer接收到provider的响应
     * @param span
     * @param endPoint
     * @param end
     */
    public void clientReceive(Span span, EndPoint endPoint, long end) {
        Annotation annotation = this.buildAnnotation(endPoint, end, AnnotationType.CR);
        span.addAnnotation(annotation);
        this.collector.collect(span);
        LOGGER.info("CR, " + span.toString());
    }

    /**
     * 构造annotation
     * @param endPoint
     * @param timestamp
     * @param type
     * @return
     */
    private Annotation buildAnnotation(EndPoint endPoint, long timestamp, AnnotationType type) {
        Annotation annotation = new Annotation();
        annotation.setEndPoint(endPoint);
        annotation.setTimestamp(timestamp);
        annotation.setValue(type.symbol());
        return annotation;
    }

    /**
     * 添加自定义的annotation
     * @param annotation
     */
    public void addBinaryAnntation(BinaryAnnotation annotation) {
        Span span = parentSpan.get();
        if (span != null) {
            span.addBinaryAnnotation(annotation);
        }
    }

}
