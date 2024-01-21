package com.qkwl.web.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.qkwl.web.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.ArrayList;
import java.util.List;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 添加类型转换器和格式化器
     */
    /*
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatterForFieldType(LocalDate.class, new USLocalDateFormatter());
    }*/

    //由于在拦截器中注解无效，需要提前注入bean
    /*@Bean
    public HandlerInterceptor getReqInterceptor(){
        return new ReqInterceptor();
    }

    @Bean
    public HandlerInterceptor getAPISecurityInterceptor(){
        return new APISecurityInterceptor();
    }

    @Bean
    public HandlerInterceptor getAPISignSecurityInterceptor(){
        return new APISignSecurityInterceptor();
    }

    @Bean
    public HandlerInterceptor getResetTokenInterceptor(){
        return new RestRedisTokenInterceptor();
    }
    */
    /*
    @Bean
    public HandlerInterceptor getAuthenticationInterceptor(){
        return new AuthenticationInterceptor();
    }
    */

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Bean
    public ErrorPageFilter errorPageFilter() {
        return new ErrorPageFilter();
    }
    @Bean
    public FilterRegistrationBean disableSpringBootErrorFilter(ErrorPageFilter filter) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }


    /**
     * 跨域支持
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") 
                .allowedOrigins("*") 
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT")  //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                .maxAge(3600 * 24);
    }

    /**
     * 添加静态资源--过滤swagger-api (开源的在线API文档)
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //过滤swagger
        registry.addResourceHandler("swagger-ui.html") 
                .addResourceLocations("classpath:/META-INF/resources/"); 
        registry.addResourceHandler("/**") 
                .addResourceLocations("classpath:/static/resources/"); 

        registry.addResourceHandler("/webjars/**") 
                .addResourceLocations("classpath:/META-INF/resources/webjars/"); 

        registry.addResourceHandler("/swagger-resources/**") 
                .addResourceLocations("classpath:/META-INF/resources/swagger-resources/"); 

        registry.addResourceHandler("/swagger/**") 
                .addResourceLocations("classpath:/META-INF/resources/swagger*"); 

        registry.addResourceHandler("/v2/api-docs/**") 
                .addResourceLocations("classpath:/META-INF/resources/v2/api-docs/"); 

    }

    /*
    @Bean FastJsonHttpMessageConverter getFastJsonHttpMessageConverter(){
        //1.需要定义一个convert转换消息的对象;
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        //2.添加fastJson的配置信息，比如：是否要格式化返回的json数据;
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteDateUseDateFormat);
        //3处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        //fastMediaTypes.add(MediaType.TEXT_HTML);
        //4.在convert中添加配置信息.
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        return fastJsonHttpMessageConverter;
    }
    */


    /**
     * 配置消息转换器--这里我用的是alibaba 开源的 fastjson
     * @param converters
     */

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
     //1.需要定义一个convert转换消息的对象;
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        //2.添加fastJson的配置信息，比如：是否要格式化返回的json数据;
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNullListAsEmpty);
                //SerializerFeature.WriteDateUseDateFormat);
        //fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        //3处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        //fastMediaTypes.add(MediaType.ALL); // 全部格式
        fastMediaTypes.add(MediaType.parseMediaType("text/html;charset=utf-8")); 
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
       // fastMediaTypes.add(MediaType.TEXT_HTML);
        //4.在convert中添加配置信息.
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        //5.将convert添加到converters当中.
        converters.add(new StringHttpMessageConverter());
        converters.add(fastJsonHttpMessageConverter);
      // converters.add(new MappingJackson2HttpMessageConverter());
    }


/*
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
       // converters.clear();
        converters.add(getFastJsonHttpMessageConverter());
    }
    */

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      //  registry.addInterceptor(getReqInterceptor()).addPathPatterns("/**");
      //  registry.addInterceptor(getAPISecurityInterceptor()).addPathPatterns("/**");
      //  registry.addInterceptor(getAPISignSecurityInterceptor()).addPathPatterns("/**");
       // registry.addInterceptor(getResetTokenInterceptor()).addPathPatterns("/**");
       // registry.addInterceptor(getAuthenticationInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(authenticationInterceptor).addPathPatterns("/**") 
               .excludePathPatterns("/**.ico"); 
       /* registry.addInterceptor(privilegeInteceptor).addPathPatterns("/**")
                 .excludePathPatterns("/**.html", "/**.ico");*/


    }

  @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(true)	//设置是否是后缀模式匹配,即:/test.*
                .setUseTrailingSlashMatch(true);	//设置是否自动后缀路径模式匹配,即：/test/
    }

    /*
    @Bean
    public ServletRegistrationBean servletRegistrationBean(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean servletServletRegistrationBean = new ServletRegistrationBean(dispatcherServlet);
        servletServletRegistrationBean.addUrlMappings("*.html");//指定.do后缀，可替换其他后缀
        return servletServletRegistrationBean;
    }*/


    /*@Bean
    public ErrorPageFilter errorPageFilter() {
        return new ErrorPageFilter();
    }
    @Bean
    public FilterRegistrationBean disableSpringBootErrorFilter(ErrorPageFilter filter) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }*/
}
