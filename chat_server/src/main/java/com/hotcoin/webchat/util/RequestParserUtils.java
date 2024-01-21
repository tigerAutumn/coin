package com.hotcoin.webchat.util;

import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotcoin.webchat.common.TextWebSocketFrameHandler;
import com.hotcoin.webchat.exception.MethodNotSupportedException;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class RequestParserUtils {

    private static final Logger logger = LoggerFactory.getLogger(RequestParserUtils.class);

    private FullHttpRequest fullReq;

    /**
     * 构造一个解析器
     * @param req
     */
    public RequestParserUtils(FullHttpRequest req) {
        this.fullReq = req;
    }

    /**
     * 解析请求参数
     * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
     *
     * @throws MethodNotSupportedException
     * @throws IOException
     */
    public  Map<String, String> parse() throws IOException, MethodNotSupportedException {
        HttpMethod method = fullReq.method();

        Map<String, String> parmMap = new HashMap<>();

        if (HttpMethod.GET == method) {
            // 是GET请求
            QueryStringDecoder decoder = new QueryStringDecoder(fullReq.uri());
            decoder.parameters().entrySet().forEach( entry -> {
                // entry.getValue()是一个List, 只取第一个元素
                parmMap.put(entry.getKey(), entry.getValue().get(0));
            });
            logger.info("GET jsonObj:"+parmMap.toString());
        } else if (HttpMethod.POST == method) {
            // 是POST请求
            ByteBuf jsonBuf = fullReq.content();
            String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
            if(jsonStr.indexOf("&") >0){
                String formBody[] = jsonStr.split("&");
                Arrays.asList(formBody).forEach(entry->{
                    parmMap.put(entry.split("=")[0], entry.split("=")[1]);
                });

            }else{
                JSONObject jsonObj = JSONObject.fromObject(jsonStr);
                logger.info("post jsonObj:"+jsonObj.toString());
                ObjectMapper mapper = new ObjectMapper();
                Map<String,Object> temp = mapper.readValue(jsonObj.toString(), Map.class);
                temp.entrySet().forEach(entry->{
                    parmMap.put(entry.getKey().trim(), String.valueOf(entry.getValue()).trim());
                });
            }
            // 是POST请求
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(fullReq);
            decoder.offer(fullReq);
            List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
            for (InterfaceHttpData parm : parmList) {
                Attribute data = (Attribute) parm;
                parmMap.put(data.getName(), data.getValue());

            }

        } else {
            // 不支持其它方法
            throw new MethodNotSupportedException(""); // 这是个自定义的异常, 可删掉这一行
        }
        return parmMap;
    }
}
