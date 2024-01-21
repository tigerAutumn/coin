package com.qkwl.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import com.qkwl.common.auth.SessionContextUtils;

/**
 * Created by muji on 2017/5/24.
 */
@Configuration
public class BeanConfig {

    @Bean
    public SessionContextUtils sessionContextUtils() {
        return new SessionContextUtils();
    }

  

    @Bean
    public InternalResourceViewResolver resourceViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setContentType("text/html;charset=utf-8");
        resolver.setPrefix("/WEB-INF/pages/");
        resolver.setSuffix(".jsp");
        resolver.setOrder(1);
        return resolver;
    }


}
