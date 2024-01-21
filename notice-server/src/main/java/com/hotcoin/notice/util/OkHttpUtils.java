package com.hotcoin.notice.util;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {
    private final static Logger logger = LoggerFactory.getLogger(OkHttpUtils.class);
    private static OkHttpClient mHttpClient;
    static{
        if(mHttpClient == null) {
            mHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectionPool(new ConnectionPool(50, 5L, TimeUnit.MINUTES))
                    .build();
        }

    }
    /**
     * 发起异步post请求
     * @param url
     * @param params
     * @return
     */
    public static void postAsync(String url, Map<String,Object> params, Consumer<Response> success, Consumer<Exception> fail){
        FormBody.Builder builder = new FormBody.Builder();
        if(params!=null){
            params.forEach((k,v)->{
                if(null != v) {
                    builder.add(k, String.valueOf(v));
                }
            });
        }

        RequestBody body = builder.build();
        Request req = new Request.Builder().url(url).post(body)
                .build();
        logger.info("postAsync request url:{}; params: {}", url, JSON.toJSON(params));
        mHttpClient.newCall(req).enqueue(new Callback(){
            //成功回调
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                logger.info("postAsync response url:{}; HTTP status message: {}", url, response.message());
                if(response.isSuccessful()){
                    if(null != success) {
                        success.accept(response);
                    }
                }else{
                    if(null != fail) {
                        fail.accept(new Exception(response.message()));
                    }
                }
                response.body().close();
            }
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                if(null != fail) {
                    fail.accept(e);
                }
            }
        });
    }
    /**
     * 发起异步post请求
     * @param url
     * @param params
     * @return
     */
    public static void postAsync(String url, Map<String,Object> params, Consumer<Response> success){
        OkHttpUtils.postAsync(url, params, success, e->logger.error("http post error:" + url, e));
    }

    /**
     * 发起同步post请求
     * @param url
     * @param params
     * @return
     */
    public static String post(String url, Map<String,Object> params) throws Exception {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            params.forEach((k, v) -> {
                if (null != v) {
                    builder.add(k, String.valueOf(v));
                }
            });
        }

        RequestBody body = builder.build();
        Request req = new Request.Builder().url(url).post(body)
                .build();
        Response response = null;
        try {
            response = mHttpClient.newCall(req).execute();
            if (response.isSuccessful()) {
                return response.body().toString();
            } else {
                throw new Exception(response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (null != response) {
                response.body().close();
            }
        }
    }
}
