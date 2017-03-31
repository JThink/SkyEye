package com.jthink.skyeye.benchmark.performance.test.task;

import com.jthink.skyeye.data.http.HttpRequest;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc rpc调用线程
 * @date 2017-03-31 10:31:29
 */
public class RpcCaller implements Runnable {

    private static final String BASE_URL = "http://localhost:8888/";

    private String url;

    public RpcCaller() {

    }

    public RpcCaller(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        while (true) {
            HttpRequest.get(BASE_URL + this.url, "");
        }
    }
}

