package com.cngc.admin.dictionary.translate.utils;

import com.cngc.admin.dictionary.translate.annotation.DictTranslator;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 数据字典转换功能工具类.
 *
 * @author duanyl
 */
public class DictTranslateUtils {

    private static boolean isSimpleClass(Object data) {

        return data instanceof Byte || data instanceof Character ||
                data instanceof Short || data instanceof Integer || data instanceof Long ||
                data instanceof Float || data instanceof Double || data instanceof Boolean;
    }

    public static boolean isStringClass(Object data) {
        return data instanceof String;
    }

    private static boolean isCollectionClass(Object data) {
        return data instanceof Collection;
    }

    private static DictTranslator extractDictTranslatorAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(DictTranslator.class)) {
                return (DictTranslator) annotation;
            }
        }
        return null;
    }

    public static Object processData(Object data, Annotation[] annotations) throws Exception {

        //以下类型数据不进行处理
        if (data == null || isSimpleClass(data) || data instanceof Map) {
            return data;
        }

        if (isStringClass(data)) {
            return Optional.ofNullable(annotations)
                    .map(DictTranslateUtils::extractDictTranslatorAnnotation)
                    .map(dictTranslatorAnnotation -> {
                        dictTranslatorAnnotation = AnnotationUtils.getAnnotation(dictTranslatorAnnotation, DictTranslator.class);
                        return Optional.ofNullable(dictTranslatorAnnotation)
                                .map(d -> DictTranslatorServiceProxy.getInstance().translateCodeToDisplayName(d.type(), (String) data))
                                .orElse((String) data);
                    }).orElse((String) data);

        } else if (isCollectionClass(data)) {
            List<Object> newCollection = new ArrayList<>();
            Collection<Object> collection = (Collection<Object>) data;
            Iterator<Object> it = collection.iterator();
            while (it.hasNext()) {
                Object object = it.next();
                newCollection.add(processData(object, annotations));
                it.remove();
            }
            collection.addAll(newCollection);
        } else {
            Field[] dataFields = data.getClass().getDeclaredFields();
            for (Field field : dataFields) {
                field.setAccessible(true);
                Object processResult = processData(field.get(data), field.getDeclaredAnnotations());
                // 1.当field不为final,才有可能重新设置值
                // 2.只有string类型才有可能重新赋值成员变量
                // 3.当有DictTranslator注解才会重新赋值成员变量
                if (!Modifier.isFinal(field.getModifiers()) && isStringClass(processResult) &&
                        extractDictTranslatorAnnotation(field.getDeclaredAnnotations()) != null) {
                    field.set(data, processResult);
                }
            }
        }
        return data;
    }

    public static Annotation[] removeDictTranslateAnnotation(Annotation[] annotations, Class<? extends Annotation> removeAnnotationClass) {
        Annotation[] annotationsNew;
        if (annotations.length > 1) {
            annotationsNew = new Annotation[annotations.length - 1];
            int index = 0;
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(removeAnnotationClass)) {
                    continue;
                }
                annotationsNew[index++] = annotation;
            }
        } else {
            annotationsNew = new Annotation[0];
        }
        return annotationsNew;
    }
}
