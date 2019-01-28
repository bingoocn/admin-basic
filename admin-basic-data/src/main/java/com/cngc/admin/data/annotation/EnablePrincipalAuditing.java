package com.cngc.admin.data.annotation;

import com.cngc.admin.data.config.JpaAuditorConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(JpaAuditorConfig.class)
public @interface EnablePrincipalAuditing {
}
