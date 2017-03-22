package com.unionpaysmart.sniper.tracer.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-12-15 15:05:06
 */
@RestController
@RequestMapping("id")
public class IdController {

    private AtomicLong traceId = new AtomicLong(0);

    private AtomicLong spanId = new AtomicLong(0);

    @RequestMapping(path = "span", method = RequestMethod.GET)
    public String span() {
        return String.valueOf(this.spanId.incrementAndGet());
    }

    @RequestMapping(path = "trace", method = RequestMethod.GET)
    public String trace() {
        return String.valueOf(this.traceId.incrementAndGet());
    }
}
