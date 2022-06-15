package io.nuls.oracle.commiter.utils.common;

import io.nuls.core.log.Log;
import io.nuls.core.parse.JSONUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhoulijun
 * @Time: 2021/4/2 15:14
 * @Description: 功能描述
 */
public class NulsJsonRpcHttpClient {

    private static final String ID = "id";
    private static final String JSONRPC = "jsonrpc";
    private static final String METHOD = "method";
    private static final String PARAMS = "params";
    private static final String DEFAULT_ID = "1";
    private static final String JSONRPC_VERSION = "2.0";

    private static final int CONNECT_TIMEOUT = 2000;

    private static final int SOCKET_TIMEOUT = 5000;

    private static final int CONNECT_REQUEST_TIMEOUT = 200;

    private static RequestConfig REQUEST_CONFIG = HttpUtilManager.buildRequestConfig()
            .setConnectRequestTimeout(CONNECT_REQUEST_TIMEOUT)
            .setConnectTimeout(CONNECT_TIMEOUT)
            .setSocketTimeout(SOCKET_TIMEOUT).build();

    public static class RestResDataError {
        private String code;
        private String message;
        private Object data;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

    public static class RestResData<T> {
        public String getJsonrpc() {
            return jsonrpc;
        }

        public void setJsonrpc(String jsonrpc) {
            this.jsonrpc = jsonrpc;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public T getResult() {
            return result;
        }

        public void setResult(T result) {
            this.result = result;
        }

        public String getResponseBody() {
            return responseBody;
        }

        public void setResponseBody(String responseBody) {
            this.responseBody = responseBody;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public RestResDataError getError() {
            return error;
        }

        public void setError(RestResDataError error) {
            this.error = error;
        }

        private String jsonrpc;

        private int id;

        private T result;

        private String responseBody;

        private boolean success = true;

        private String msg;

        private RestResDataError error;

        public RestResData(String msg) {
            this.success = false;
            this.msg = msg;
        }

        public RestResData() {
        }
    }

    private String url;

    private HttpUtilManager httpUtilManager;

    public static NulsJsonRpcHttpClient getInstance(String url) {
        NulsJsonRpcHttpClient client = new NulsJsonRpcHttpClient();
        client.url = url;
        client.httpUtilManager = HttpUtilManager.getInstance();
        client.httpUtilManager.setRequestConfig(REQUEST_CONFIG);

        client.config(new HttpUtilManager.RequestConfigBuilder()
                .setConnectRequestTimeout(HttpUtilManager.CONNECT_REQUEST_TIMEOUT)
                .setConnectTimeout(HttpUtilManager.CONNECT_TIMEOUT)
                .setSocketTimeout(HttpUtilManager.SOCKET_TIMEOUT).build());
        return client;
    }

    public void config(RequestConfig requestConfig) {
        this.httpUtilManager.setRequestConfig(requestConfig);
    }

    public <T> RestResData<T> request(String method, List<Object> params, Class<T> dataClass) {
        long start = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>(8);
        map.put(ID, DEFAULT_ID);
        map.put(JSONRPC, JSONRPC_VERSION);
        map.put(METHOD, method);
        map.put(PARAMS, params);
        final HttpResponse response;
        try {
            response = httpUtilManager.requestHttpPostForJson(url, "", map);
            long execTime = System.currentTimeMillis() - start;
            if (execTime > 3000) {
                Log.warn("调用" + url + "接口 调用时间异常 time:{}ms", execTime);
            }
            return handleResponse(response, dataClass);
        } catch (Exception e) {
            return new RestResData<>("请求失败:" + e.getMessage());
        }

    }

    public <T> RestResData<T> handleResponse(final HttpResponse response, Class<T> dataClass) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return new RestResData<>("没有获取到返回值");
        }
        InputStream is = null;
        String responseData = null;
        try {
            is = entity.getContent();
            responseData = IOUtils.toString(is, "UTF-8");
            RestResData<T> res = JSONUtils.json2pojo(responseData, RestResData.class);
            if (res.getError() != null) {
                return new RestResData<>(res.getError().getMessage());
            }
            T data = JSONUtils.json2pojo(JSONUtils.obj2json(res.getResult()), dataClass);
            res.setResult(data);
            res.setResponseBody(responseData);
            return res;
        } catch (RuntimeException e) {
            return new RestResData<>("没有获取到返回值");
        } finally {
            if (response != null) {
                //会自动释放连接
                EntityUtils.consume(response.getEntity());
            }
            if (is != null) {
                is.close();
            }
        }

    }


}
