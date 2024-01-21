package com.qkwl.admin.layui.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import com.alibaba.dubbo.rpc.RpcContext;
import com.qkwl.common.util.I18NUtils;


@Configuration
@WebFilter(filterName = "myFilter", urlPatterns = "/*")
public class MultiLangFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(MultiLangFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      
      //设置服务上下文
      String lang = null;
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        lang = httpServletRequest.getHeader(I18NUtils.LANG_PARAM);
        if (StringUtils.isBlank(lang)) {
          lang = httpServletRequest.getParameter(I18NUtils.LANG_PARAM);
        }

        if (StringUtils.isBlank(lang)) {
          lang = I18NUtils.DEFAULT_LOCALE.toString();
        }
      
        RpcContext.getContext().setAttachment(I18NUtils.LANG_PARAM,lang);      
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}