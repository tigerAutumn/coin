package com.qkwl.web.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.qkwl.common.framework.redis.RedisHelper;


@Component("exceptionResolver")
public class ExceptionHandler implements HandlerExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Autowired
    private RedisHelper redisHelper;

    /**
     * 异常错误页
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ex.printStackTrace();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("localeStr", "cn");
        modelAndView.addObject("staticurl", redisHelper.getSystemArgs("staticurl"));
        modelAndView.setViewName("front/error/error");
        logger.error("ex:{}",ex);
        return modelAndView;
    }

}
