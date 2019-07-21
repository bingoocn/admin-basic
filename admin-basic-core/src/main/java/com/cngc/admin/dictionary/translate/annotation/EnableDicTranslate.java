package com.cngc.admin.dictionary.translate.annotation;

import com.cngc.admin.dictionary.translate.config.RequestMappingHandlerAdapterPostProcessor;
import com.cngc.admin.properties.DateFormatProperties;
import com.cngc.admin.utils.SpringContextUtil;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({RequestMappingHandlerAdapterPostProcessor.class, DateFormatProperties.class, SpringContextUtil.class})
public @interface EnableDicTranslate {
}
