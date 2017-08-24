package com.jthink.skyeye.trace.core.collector;

import com.alibaba.fastjson.JSON;
import com.jthink.skyeye.base.constant.EventType;
import com.jthink.skyeye.base.dapper.Annotation;
import com.jthink.skyeye.base.dapper.EndPoint;
import com.jthink.skyeye.base.dapper.Span;
import com.jthink.skyeye.base.dto.RpcTraceLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 采集span存入kafka等待消费入库
 * @date 2017-02-15 14:22:56
 */
public class KafkaCollector implements Collector {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaCollector.class);

    @Override
    public void collect(Span span) {
        LOGGER.info(RpcTraceLog.buildRpcTraceLog(EventType.rpc_trace, JSON.toJSONString(span)).toString());
    }

    public static void main(String[] args) {
        List<Annotation> annotations = new ArrayList<Annotation>();
        EndPoint endPoint = new EndPoint();
        endPoint.setIp("192.168.87.128");
        endPoint.setPort(1245);
        Annotation annotation = new Annotation();
        annotation.setValue("annv");
        annotation.setTimestamp(56565464L);
        annotation.setEndPoint(endPoint);
        Annotation annotation2 = new Annotation();
        annotation2.setValue("annv2");
        annotation2.setTimestamp(565654642L);
        annotations.add(annotation);
        annotations.add(annotation2);
        Span span = new Span();
        span.setAnnotations(annotations);
        span.setId("123456");
        span.setName("span name");
//        span.setParentId(234556L);
        span.setSample(true);
//        span.setServiceId("dsadasd");
        span.setTraceId("5432534");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 20000; ++i) {
            String json = JSON.toJSONString(span);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
//        System.out.println(json);

//        Span span1 = JSON.parseObject(json, Span.class);
//        System.out.println(span1.getName());
//        System.out.println(span1.getServiceId());
//        System.out.println(span.getServiceId());
    }
}
