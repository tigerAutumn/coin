package com.hotcoin.notice.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NetUtils {
    private Logger logger = LoggerFactory.getLogger(NetUtils.class);
    private static final byte[] LOCKER = new byte[0];
    private static NetUtils mInstance;
    private OkHttpClient mOkHttpClient;

    private NetUtils() {
        okhttp3.OkHttpClient.Builder ClientBuilder=new okhttp3.OkHttpClient.Builder();
        ClientBuilder.readTimeout(30, TimeUnit.SECONDS);//读取超时
        ClientBuilder.connectTimeout(30, TimeUnit.SECONDS);//连接超时
        ClientBuilder.writeTimeout(60, TimeUnit.SECONDS);//写入超时
        mOkHttpClient=ClientBuilder.build();
    }

    /**
     * 单例模式获取NetUtils
     * @return
     */
    public static NetUtils getInstance() {
        if (mInstance == null) {
            synchronized (LOCKER) {
                if (mInstance == null) {
                    mInstance = new NetUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * get请求，同步方式，获取网络数据，是在主线程中执行的，需要新起线程，将其放到子线程中执行
     * @param url
     * @return
     */
    public  Response getDataSynFromNet(String url) {
        //1 构造Request
        Request.Builder builder = new Request.Builder();
        Request request=builder.get().url(url).build();
        //2 将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //3 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
    /**
     * post请求，同步方式，提交数据，是在主线程中执行的，需要新起线程，将其放到子线程中执行
     * @param url
     * @param bodyParams
     * @return
     */
    public  Response postDataSynToNet(String url,Map<String,String> bodyParams) {
        //1构造RequestBody
        RequestBody body = setRequestBody(bodyParams);
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(body).url(url).build();
        //3 将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //4 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * post的请求参数，构造RequestBody
     * @param BodyParams
     * @return
     */
    private RequestBody setRequestBody(Map<String, String> BodyParams){
        RequestBody body=null;
        okhttp3.FormBody.Builder formEncodingBuilder=new okhttp3.FormBody.Builder();
        if(BodyParams != null){
            Iterator<String> iterator = BodyParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                formEncodingBuilder.add(key, BodyParams.get(key));
                logger.info("post http", "post_Params==="+key+"===="+BodyParams.get(key));
            }
        }
        body=formEncodingBuilder.build();
        return body;

    }

}
