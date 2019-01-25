package com.cngc.admin.config;

import com.cngc.admin.properties.DateFormatProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.text.DateFormat;


/**
 * 对象的默认处理配置类.
 *
 * @author duanyl
 */
@Configuration
public class DefaultObjectConfig {
    @Autowired
    private DateFormatProperties dateFormatProperties;

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.setDateFormat((DateFormat) enhancer(objectMapper.getDateFormat()).create());
        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString("");
            }
        });
        return objectMapper;
    }

    /**
     * 对springboot默认配置的dateFormat进行增强,改变其parse(String str)的行为,满足多种指定格式的字符串转换为Date.
     *
     * @param dateFormat springBoot默认配置的时间转换
     * @return cglib增强器
     */
    private Enhancer enhancer(DateFormat dateFormat) {
        Class<?> formatClass = dateFormat.getClass();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(formatClass);
        enhancer.setCallback((MethodInterceptor) (o, method, objects, methodProxy) -> {
            // 对clone方法需要进行处理,保证原对象clone时,生成一个新的代理对象.
            if (formatClass.getMethod("clone").equals(method)) {
                return enhancer((DateFormat) dateFormat.clone()).create();
            }

            // 实现多种格式的字符串转换为Date对象.
            if (formatClass.getMethod("parse", String.class).equals(method)) {
                return DateUtils.parseDate((String)objects[0],dateFormatProperties.getDateParse());
            }

            return methodProxy.invoke(dateFormat, objects);
        });
        return enhancer;
    }
}
