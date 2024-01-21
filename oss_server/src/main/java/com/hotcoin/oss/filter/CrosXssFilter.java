package com.hotcoin.oss.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.hotcoin.oss.utils.XssHttpServletRequestWrapper;


@Configuration
@WebFilter(filterName = "crosXssFilter", urlPatterns = "/*")
@Order(value = 1)
public class CrosXssFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(CrosXssFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding("utf-8"); 
        response.setContentType("application/json;charset=UTF-8"); 
        //跨域设置
        if(response instanceof HttpServletResponse){
            HttpServletResponse httpServletResponse=(HttpServletResponse)response;
            //通过在响应 header 中设置 ‘*’ 来允许来自所有域的跨域请求访问。
            httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");  //$NON-NLS-2$
            //通过对 Credentials 参数的设置，就可以保持跨域 Ajax 时的 Cookie
            //设置了Allow-Credentials，Allow-Origin就不能为*,需要指明具体的url域
            //httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            //请求方式
            httpServletResponse.setHeader("Access-Control-Allow-Methods", "*");  //$NON-NLS-2$
            //（预检请求）的返回结果（即 Access-Control-Allow-Methods 和Access-Control-Allow-Headers 提供的信息） 可以被缓存多久
            httpServletResponse.setHeader("Access-Control-Max-Age", "86400");  //$NON-NLS-2$
            //首部字段用于预检请求的响应。其指明了实际请求中允许携带的首部字段
            httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");  //$NON-NLS-2$
            httpServletResponse.setHeader("Cache-Control", "no-cache");  //$NON-NLS-2$
            httpServletResponse.setHeader("Access-Control-Allow-Headers", "platform");  //$NON-NLS-2$
        }
        //sql,xss过滤
        HttpServletRequest httpServletRequest=(HttpServletRequest) request;
        XssHttpServletRequestWrapper xssHttpServletRequestWrapper=new XssHttpServletRequestWrapper(
                httpServletRequest);
        chain.doFilter(xssHttpServletRequestWrapper, response);
    }

    @Override
    public void destroy() {

    }

}