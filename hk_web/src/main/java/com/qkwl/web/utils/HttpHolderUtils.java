package com.qkwl.web.utils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class HttpHolderUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpHolderUtils.class);

    public static HttpServletRequest getHttpServletRequest(){
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getHttpServletResponse(){
        return  ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

}
