package com.jthink.skyeye.data.http;

import com.jthink.skyeye.base.constant.Constants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc http请求
 * @date 2016-11-17 10:49:52
 */
public class HttpRequest {

    private static Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);

    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    private static PoolingHttpClientConnectionManager cm = null;

    static {
        LayeredConnectionSocketFactory sslsf = null;
        try {
            X509TrustManager x509mgr = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] xcs, String string) {
                }
                @Override
                public void checkServerTrusted(X509Certificate[] xcs, String string) {
                }
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            // 信任所有
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
            sslContext.init(null, new TrustManager[] { x509mgr }, null);
            sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {
            LOGGER.info("创建SSL连接失败");
        }
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
    }

    private static CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
        return httpClient;
    }

    /**
     * 高效Get请求
     * @param url
     * @param param
     * @return
     */
    public static String get(String url, String param) {
        // 创建默认的httpClient实例
        CloseableHttpClient httpClient = HttpRequest.getHttpClient();
        CloseableHttpResponse httpResponse = null;
        // 发送get请求
        try {
            // 用get方法发送http请求
            HttpGet get = new HttpGet(url + URLEncoder.encode(param, "UTF-8"));
            LOGGER.info("执行get请求, uri: " + get.getURI());
            httpResponse = httpClient.execute(get);
            // response实体
            HttpEntity entity = httpResponse.getEntity();
            if (null != entity) {
                String response = EntityUtils.toString(entity);
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                LOGGER.info("响应状态码:" + statusCode);
//                LOGGER.info("响应内容:" + response);
                if (statusCode == HttpStatus.SC_OK) {
                    // 成功
                    return response;
                } else {
                    return null;
                }
            }
            return null;
        } catch (IOException e) {
            LOGGER.error("httpclient请求失败", e);
            return null;
        } finally {
            if (httpResponse != null) {
                try {
                    EntityUtils.consume(httpResponse.getEntity());
                    httpResponse.close();
                } catch (IOException e) {
                    LOGGER.error("关闭response失败", e);
                }
            }
        }
    }

    /**
     * 高效Get请求
     * @param url
     * @param params
     * @return
     */
    public static String get(String url, Map<String, String> params) {
        // 创建默认的httpClient实例
        CloseableHttpClient httpClient = HttpRequest.getHttpClient();
        CloseableHttpResponse httpResponse = null;
        // 发送get请求
        try {
            // 用get方法发送http请求
            HttpGet get = new HttpGet(url + Constants.QUESTION_MARK + EntityUtils.toString(new UrlEncodedFormEntity(packageParams(params), CHARSET_UTF8)));
            LOGGER.info("执行get请求, uri: " + get.getURI());
            httpResponse = httpClient.execute(get);
            // response实体
            HttpEntity entity = httpResponse.getEntity();
            if (null != entity) {
                String response = EntityUtils.toString(entity);
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                LOGGER.info("当前请求返回码: {}", statusCode);
                if (statusCode == HttpStatus.SC_OK) {
                    // 成功
                    return response;
                } else {
                    return null;
                }
            }
            return null;
        } catch (IOException e) {
            LOGGER.info("httpclient请求失败", e);
            return null;
        } finally {
            if (httpResponse != null) {
                try {
                    EntityUtils.consume(httpResponse.getEntity());
                    httpResponse.close();
                } catch (IOException e) {
                    LOGGER.info("关闭response失败", e);
                }
            }
        }
    }

    /**
     * 封装请求参数
     * @param params
     * @return
     */
    private static List<NameValuePair> packageParams(Map<String, String> params) {
        List<NameValuePair> pairs = new ArrayList<>();
        params.forEach((key, value) -> pairs.add(new BasicNameValuePair(key, value)));
        return pairs;
    }

    /**
     * 高效Post body json请求
     * @param url
     * @param data
     * @return
     */
    public static String post(String url, String data) {
        // 创建默认的httpClient实例
        CloseableHttpClient httpClient = HttpRequest.getHttpClient();
        CloseableHttpResponse httpResponse = null;
        try {
            HttpPost post = new HttpPost(url);
            Charset charset = CHARSET_UTF8;
            StringEntity entity = new StringEntity(data, charset);
            entity.setContentEncoding(charset.name());
            entity.setContentType("application/json");
            post.setEntity(entity);

            httpResponse = httpClient.execute(post);
            // response实体
            HttpEntity httpEntity = httpResponse.getEntity();
            if (null != httpEntity) {
                String response = EntityUtils.toString(httpEntity);
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                LOGGER.info("当前请求返回码: {}", statusCode);
                if (statusCode == HttpStatus.SC_OK) {
                    // 成功
                    return response;
                } else {
                    return null;
                }
            }
            return null;
        } catch (IOException e) {
            LOGGER.info("httpclient请求失败", e);
            return null;
        } finally {
            if (httpResponse != null) {
                try {
                    EntityUtils.consume(httpResponse.getEntity());
                    httpResponse.close();
                } catch (IOException e) {
                    LOGGER.info("关闭response失败", e);
                }
            }
        }
    }


    /**
     * 高效表单Post请求
     * @param url
     * @param map
     * @return
     */
    public static String post(String url, Map<String, String> map) {
        // 创建默认的httpClient实例
        CloseableHttpClient httpClient = HttpRequest.getHttpClient();
        CloseableHttpResponse httpResponse = null;
        try {
            HttpPost post = new HttpPost(url);
            Charset charset = CHARSET_UTF8;
            List<NameValuePair> params = new ArrayList<>();
            map.forEach((key, value) -> params.add(new BasicNameValuePair(key, value)));

            post.setEntity(new UrlEncodedFormEntity(params, charset));

            httpResponse = httpClient.execute(post);
            // response实体
            HttpEntity httpEntity = httpResponse.getEntity();
            if (null != httpEntity) {
                String response = EntityUtils.toString(httpEntity);
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                LOGGER.info("当前请求返回码: {}", statusCode);
                if (statusCode == HttpStatus.SC_OK) {
                    // 成功
                    return response;
                } else {
                    return null;
                }
            }
            return null;
        } catch (IOException e) {
            LOGGER.info("httpclient请求失败", e);
            return null;
        } finally {
            if (httpResponse != null) {
                try {
                    EntityUtils.consume(httpResponse.getEntity());
                    httpResponse.close();
                } catch (IOException e) {
                    LOGGER.info("关闭response失败", e);
                }
            }
        }
    }
}

