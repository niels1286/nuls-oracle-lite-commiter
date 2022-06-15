package io.nuls.oracle.commiter.utils.common;
 
import io.nuls.core.log.Log;
import io.nuls.core.model.StringUtils;
import io.nuls.core.parse.JSONUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 封装HTTP get post请求，简化发送http请求
 *
 * @author zhangchi
 */ 
public class HttpUtilManager {

    public static class HttpDelete extends HttpPost {

        public HttpDelete(String url) {
            super(url);
        }

        @Override
        public String getMethod() {
            return "DELETE";
        }
    }

    public static final int CONNECT_TIMEOUT = 20 * 1000;

    public static final int SOCKET_TIMEOUT = 20 * 1000;

    public static final int CONNECT_REQUEST_TIMEOUT = 500;

    private static HttpUtilManager instance;

    private static HttpClient client;

    private static long startTime = System.currentTimeMillis();

    public static PoolingHttpClientConnectionManager cm;

    private static ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy() {

        public long getKeepAliveDuration(
                HttpResponse response,
                HttpContext context) {
            long keepAlive = super.getKeepAliveDuration(response, context);

            if (keepAlive == -1) {
                keepAlive = 5000;
            }
            return keepAlive;
        }

    };

    private HttpUtilManager(PoolingHttpClientConnectionManager cm) {
        client = HttpClients.custom()
                .setConnectionManager(cm)
                .setKeepAliveStrategy(keepAliveStrat)
                .disableCookieManagement()
                .build();
        requestConfig = REQUEST_CONFIG;
    }

    public static void IdleConnectionMonitor() {
        if (System.currentTimeMillis() - startTime > 30000) {
            startTime = System.currentTimeMillis();
            cm.closeExpiredConnections();
            cm.closeIdleConnections(30, TimeUnit.SECONDS);
        }
    }

    private static RequestConfig REQUEST_CONFIG = RequestConfig.custom()
            .setSocketTimeout(SOCKET_TIMEOUT)
            .setConnectTimeout(CONNECT_TIMEOUT)
            .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
            .setConnectionRequestTimeout(CONNECT_REQUEST_TIMEOUT)
            .build();

    private RequestConfig requestConfig;

    public static class RequestConfigBuilder {

        private int socketTimeout = SOCKET_TIMEOUT;

        private int connectTimeout = CONNECT_TIMEOUT;

        private int connectRequestTimeout = CONNECT_REQUEST_TIMEOUT;

        public RequestConfig build() {
            return RequestConfig.custom()
                    .setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectTimeout)
                    .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                    .setConnectionRequestTimeout(connectRequestTimeout)
                    .build();
        }

        public RequestConfigBuilder setConnectRequestTimeout(int connectRequestTimeout) {
            this.connectRequestTimeout = connectRequestTimeout;
            return this;
        }

        public RequestConfigBuilder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public RequestConfigBuilder setSocketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
            return this;
        }
    }

    public static RequestConfigBuilder buildRequestConfig() {
        return new RequestConfigBuilder();
    }

    public static HttpUtilManager getInstance(PoolingHttpClientConnectionManager cm) {
        if (instance == null) {
            synchronized (HttpUtilManager.class) {
                if (instance == null) {
                    instance = new HttpUtilManager(cm);
                    return instance;
                }
                return instance;
            }
        }
        return instance;
    }

    public static HttpUtilManager getInstance(int maxTotal, int maxPerRoute) {
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(maxTotal);
        cm.setDefaultMaxPerRoute(maxPerRoute);
        return getInstance(cm);
    }

    public static HttpUtilManager getInstance() {
        return getInstance(100, 20);
    }

    public HttpClient getHttpClient() {
        return client;
    }

    private HttpPost httpPostMethod(String url) {
        return new HttpPost(url);
    }

    private HttpRequestBase httpGetMethod(String url) {
        return new HttpGet(url);
    }

    private String pathQuery(String url, Map<String, Object> param) {
        if (param != null) {
            String query = param.keySet().stream().reduce((r, d) -> r + "=" + param.get(r) + "&" + d + "=" + param.get(d)).get();
            if (url.endsWith("?")) {
                url = url + query;
            } else {
                url = url + "?" + query;
            }
        }
        return url;
    }

    public HttpResponse requestHttpGet(String url_prex, String url, Map<String, Object> param, Header... headers) throws HttpException, IOException {
        return requestHttp(this.httpGetMethod(pathQuery(url_prex + url, param)), null, headers);

    }

    public HttpResponse requestHttpGet(String url_prex, String url, Map<String, Object> param, RequestConfig requestConfig, Header... headers) throws HttpException, IOException {
        return requestHttp(this.httpGetMethod(pathQuery(url_prex + url, param)), requestConfig, headers);

    }

    private HttpResponse requestHttp(HttpRequestBase method, RequestConfig rc, Header... headers) throws IOException {
        IdleConnectionMonitor();
        method.setConfig(rc == null ? requestConfig : rc);
        Arrays.stream(headers).forEach(header -> method.addHeader(header));
        Long start = System.currentTimeMillis();
        HttpResponse response = client.execute(method);
        Long time = System.currentTimeMillis() - start;
        if (time > method.getConfig().getConnectTimeout() - 1) {
            Log.warn("http 请求时间异常:{}", time);
        }
        return response;
    }

    public HttpResponse requestHttpDelete(String url_prex, String url, Map<String, Object> param, Header... headers) throws IOException {
        return requestHttpDelete(url_prex, url, param, requestConfig, headers);
    }


    public HttpResponse requestHttpDelete(String url_prex, String url, Map<String, Object> param, RequestConfig rc, Header... headers) throws IOException {
        HttpDelete method = new HttpDelete(url_prex + url);
        return requestHttpPostForJson(method, param, rc, headers);
    }

    public HttpResponse requestHttpPostForJson(String url_prex, String url, Map<String, Object> params, Header... headers) throws IOException {
        return requestHttpPostForJson(url_prex, url, params, requestConfig, headers);
    }

    public HttpResponse requestHttpPostForJson(String url_prex, String url, Map<String, Object> params, RequestConfig rc, Header... headers) throws IOException {
        IdleConnectionMonitor();
        url = url_prex + url;
        HttpPost method = this.httpPostMethod(url);
        return requestHttpPostForJson(method, params, rc, headers);
    }

    public HttpResponse requestHttpPostForJson(HttpPost method, Map<String, Object> params, RequestConfig rc, Header... headers) throws IOException {
        IdleConnectionMonitor();
        StringEntity entity = new StringEntity(JSONUtils.obj2json(params));
        entity.setContentType("application/json");
        method.setEntity(entity);
        method.setConfig(rc == null ? requestConfig : rc);
        Arrays.stream(headers).forEach(header -> method.addHeader(header));
        Long start = System.currentTimeMillis();
        HttpResponse response = client.execute(method);
        Long time = System.currentTimeMillis() - start;
        if (time > method.getConfig().getConnectTimeout() - 1) {
            Log.warn("http 请求时间异常:{}", time);
        }
        return response;
    }

    public HttpResponse requestHttpPostForJson(HttpPost method, Map<String, Object> params, Header... headers) throws IOException {
        return requestHttpPostForJson(method, params, requestConfig, headers);
    }

    public HttpResponse requestHttpPost(String url_prex, String url, Map<String, Object> params, Header... headers) throws HttpException, IOException {
        return requestHttpPost(url_prex, url, params, requestConfig, headers);
    }

    public HttpResponse requestHttpPost(String url_prex, String url, Map<String, Object> params, RequestConfig rc, Header... headers) throws HttpException, IOException {
        IdleConnectionMonitor();
        url = url_prex + url;
        HttpPost method = this.httpPostMethod(url);
        List<NameValuePair> valuePairs = this.convertMap2PostParams(params);
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
        method.setEntity(urlEncodedFormEntity);
        method.setConfig(rc == null ? requestConfig : rc);
        Arrays.stream(headers).forEach(header -> method.addHeader(header));
        Long start = System.currentTimeMillis();
        HttpResponse response = client.execute(method);
        Long time = System.currentTimeMillis() - start;
        if (time > method.getConfig().getConnectTimeout() - 1) {
            Log.warn("http 请求时间异常:{}", time);
        }
        return response;
    }

    private List<NameValuePair> convertMap2PostParams(Map<String, Object> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        if (keys.isEmpty()) {
            return null;
        }
        int keySize = keys.size();
        List<NameValuePair> data = new LinkedList<NameValuePair>();
        for (int i = 0; i < keySize; i++) {
            String key = keys.get(i);
            String value = String.valueOf(params.get(key));
            if (!StringUtils.isBlank(value)) {
                data.add(new BasicNameValuePair(key, value));
            }
        }
        return data;
    }

    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

}

