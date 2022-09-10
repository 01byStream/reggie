package com.bys.reggie.config;

import com.bys.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description: web配置类
 * @date 2022/8/31 16:14
 */
@Slf4j
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    //设置静态资源路径映射
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始映射静态资源...");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    //扩展MVC框架的消息转换器
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展MVC的消息转换器");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置自定义的对象转换器
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //添加到mvc框架的转换器集合中，排序在最前
        converters.add(0, messageConverter);
    }
}
