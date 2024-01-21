package com.qkwl.web.exception;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ReturnResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author futao
 * Created on 2018/9/21-15:13.
 * 异常统一处理，
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object exceptionHandler(HttpServletRequest request, Exception e, HttpServletResponse response) {
        //系统级异常，错误码固定为-1，提示语固定为系统繁忙，请稍后再试
        ReturnResult result = ReturnResult.FAILUER(I18NUtils.getString("GlobalExceptionHandler.0")); 
        //如果是业务逻辑异常，返回具体的错误码与提示信息
        if (e instanceof APISecurityPermissionException) {
            APISecurityPermissionException apiSecurityPermissionException = (APISecurityPermissionException) e;
            result.setCode(apiSecurityPermissionException.getStatusCode());
            result.setMsg(apiSecurityPermissionException.getMessage());
           // return JSON.toJSON(result);
        } else if (e instanceof APISignSecurityPermissionException) {
            APISignSecurityPermissionException apiSignSecurityPermissionException = (APISignSecurityPermissionException) e;
            result.setCode(apiSignSecurityPermissionException.getStatusCode());
            result.setMsg(apiSignSecurityPermissionException.getMessage());
           // return JSON.toJSON(result);
        }else if (e instanceof SignSecurityPermissionException) {
            SignSecurityPermissionException signSecurityPermissionException = (SignSecurityPermissionException) e;
            result.setCode(signSecurityPermissionException.getStatusCode());
            result.setMsg(signSecurityPermissionException.getMessage());
            return JSON.toJSON(result);
        } else if(e instanceof LoginException){
            result.setCode(ReturnResult.FAULURE_USER_NOT_LOGIN);
            result.setMsg(e.getMessage());
        }else {
            //对系统级异常进行日志记录
            logger.error("系统异常 e:{}", e);
            result.setMsg(e.getMessage());
        }
        return JSON.toJSON(result);

    }
}
