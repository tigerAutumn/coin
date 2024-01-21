package com.hotcoin.oss.controller;

import com.alibaba.fastjson.JSON;
import com.hotcoin.oss.vo.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String exceptionHandler(Exception ex, HttpServletResponse response) {
        ResponseMessage<Map<String,String>> responseMessage = new ResponseMessage<>();
        Map<String,String> filePathMap = new HashMap<String,String>(1);
        if( ex instanceof MultipartException) {
            logger.error("文件大小超过限制.ex:{}",ex);
            responseMessage = responseMessage.error(filePathMap, "文件大小超过限制");
        }else{
            logger.error("非法请求.ex:{}",ex);
            responseMessage = responseMessage.error(filePathMap, "非法请求");
        }
        return JSON.toJSONString(responseMessage);
    }
}
