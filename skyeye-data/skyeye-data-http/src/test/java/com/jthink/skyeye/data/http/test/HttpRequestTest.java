package com.jthink.skyeye.data.http.test;

import com.jthink.skyeye.data.http.HttpRequest;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc http请求
 * @date 2016-11-17 10:49:52
 */
public class HttpRequestTest {

    public static void main(String[] args) {
        System.out.println(HttpRequest.get("http://api.map.baidu.com/geocoder/v2/?output=json&ak=20089d4833c1db58e8642708f9281ab1&address=", "上海市浦东新区"));
    }
}

