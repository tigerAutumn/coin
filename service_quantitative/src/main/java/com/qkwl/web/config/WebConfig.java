package com.qkwl.web.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.qkwl.web.interceptor.APIAuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private APIAuthenticationInterceptor authenticationInterceptor;

 
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

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(true)	//设置是否是后缀模式匹配,即:/test.*
                .setUseTrailingSlashMatch(true);	//设置是否自动后缀路径模式匹配,即：/test/
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/**.ico","/swagger-resources/**", "/webjars/**", "/swagger-ui.html/**","/v2/api-docs","/csrf");
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }
}
