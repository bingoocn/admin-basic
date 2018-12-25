package com.cngc.admin.dictionary.translate.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 根据数据字典进行数据的转换,注解于domain对象的成员变量.
 *
 * @author duanyl
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface DictTranslator {

    /**
     * 对应的数据字典的类型.
     *
     * @return 数据字典类型
     */
    @AliasFor("type")
    String value() default "";

    @AliasFor("value")
    String type() default "";

    /**
     * 此数据是否时多值.
     *
     * @return boolean
     */
    boolean isMultiVal() default false;

    /**
     * 多值的分隔符.
     *
     * @return 分隔符
     */
    String split() default ",";

}
