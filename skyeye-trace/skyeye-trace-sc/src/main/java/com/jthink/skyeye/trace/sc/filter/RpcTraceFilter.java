package com.jthink.skyeye.trace.sc.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 基于zuul filter来实现spring cloud的rpc trace
 * @date 2017-08-24 11:07:38
 */
public class RpcTraceFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcTraceFilter.class);

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        Map<String, String> headers = context.getZuulRequestHeaders();

        headers.forEach((k, v) -> LOGGER.info("key: {}, value: {}", k, v));

        return null;
    }
}
