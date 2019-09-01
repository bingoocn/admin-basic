package com.cngc.admin.dictionary.translate.config;

import com.cngc.admin.dictionary.translate.annotation.DictTranslator;
import com.cngc.admin.dictionary.translate.annotation.EnableRequestDictTranslate;
import com.cngc.admin.dictionary.translate.utils.DictTranslateUtils;
import com.cngc.admin.dictionary.translate.utils.DictTranslatorServiceProxy;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

/**
 * 请求参数数据字典转换解释器.
 *
 * @author duanyl
 */
public class TranslatorMethodArgumentResolver implements HandlerMethodArgumentResolver {
    /**
     * 注入requestMappingHandlerAdapter bean
     */
    private RequestMappingHandlerAdapter pb;

    TranslatorMethodArgumentResolver(RequestMappingHandlerAdapter pb) {
        this.pb = pb;
    }

    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        Annotation[] annotations;
        try {
            annotations = getParameterAnnotations(parameter);
        } catch (Exception e) {
            return false;
        }
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(EnableRequestDictTranslate.class)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 将parameter去掉EnableRequestDictTranslate注解,同时更改parameterIndex
     * 从而在resolver composite中,进行resolver选择时,选择到原本对应的参数处理resolver.
     * 参数进行[非]TranslatorMethodArgumentResolver处理后,再进行数据字典的转换,完成处理.
     *
     * @param parameter     方法参数
     * @param mavContainer  mavContainer
     * @param webRequest    请求对象
     * @param binderFactory binderFactgory
     * @return 处理后的参数数据对象
     * @throws Exception e
     */
    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //拷贝方法参数,进行参数内容修改,不影响传入方法参数对象.
        parameter = parameter.clone();
        //先去计算name,如果在改变index后,无法拿到name,数据越界.
        parameter.getParameterName();
        HandlerMethodArgumentResolverComposite argumentResolvers;
        try {
            Field argumentResolversField = RequestMappingHandlerAdapter.class.getDeclaredField("argumentResolvers");
            argumentResolversField.setAccessible(true);
            argumentResolvers = (HandlerMethodArgumentResolverComposite) argumentResolversField.get(pb);
        } catch (Exception e) {
            throw new RuntimeException("无法处理RequestMappingHandlerAdapter.argumentResolvers", e);
        }
        Annotation[] annotations = getParameterAnnotations(parameter);
        Annotation[] annotationsNew = DictTranslateUtils.removeDictTranslateAnnotation(annotations, EnableRequestDictTranslate.class);

        Field paramterAnnotationsField = MethodParameter.class.getDeclaredField("parameterAnnotations");
        paramterAnnotationsField.setAccessible(true);
        paramterAnnotationsField.set(parameter, annotationsNew);

        Field parameterIndexField = MethodParameter.class.getDeclaredField("parameterIndex");
        parameterIndexField.setAccessible(true);
        parameterIndexField.set(parameter, parameter.getExecutable().getParameterTypes().length + parameter.getParameterIndex());

        //resolvercomposite进行处理,返回处理后的参数数据.
        Object result = argumentResolvers.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        // 请求时字典类型属性使用code&name风格时,不进行字典name->code的处理.
        if (DictTranslateUtils.isCodeAndNameStyle(webRequest) || result == null) {
            return result;
        }

        Object initObject = parameter.getParameterType().newInstance();
        //进行字段的数据字典转换处理.
        if (DictTranslateUtils.isStringClass(result)) {
            DictTranslator dictTranslatorAnnotation = parameter.getParameterAnnotation(DictTranslator.class);
            if (dictTranslatorAnnotation != null) {
                result = convertDictData(result.toString(), dictTranslatorAnnotation, null);
            }
        } else {
            //TODO 处理嵌套复杂对象
            Field[] resultFields = result.getClass().getDeclaredFields();
            for (Field field : resultFields) {
                DictTranslator dicTranslatorAnnotation;
                if ((dicTranslatorAnnotation = field.getAnnotation(DictTranslator.class)) != null) {
                    field.setAccessible(true);
                    Object fieldValue = field.get(result);
                    dicTranslatorAnnotation = AnnotationUtils.getAnnotation(dicTranslatorAnnotation, DictTranslator.class);
                    if (fieldValue == null || dicTranslatorAnnotation == null) {
                        continue;
                    }
                    String displayStrValue = fieldValue.toString();
                    field.set(result, convertDictData(displayStrValue, dicTranslatorAnnotation, Optional.ofNullable(field.get(initObject)).map(v -> v.toString()).orElse(null)));
                }
            }
        }
        return result;
    }

    private Annotation[] getParameterAnnotations(MethodParameter parameter) throws NoSuchFieldException, IllegalAccessException {
        Field paramterAnnotationsField = MethodParameter.class.getDeclaredField("parameterAnnotations");
        paramterAnnotationsField.setAccessible(true);
        return (Annotation[]) paramterAnnotationsField.get(parameter);
    }


    private String convertDictData(String value, @NotNull DictTranslator dictTranslatorAnnotation, String initValue) {
        // 如果属性值等于对象初始化时的赋值,则不进行处理.
        if (!StringUtils.isEmpty(initValue) && initValue.equals(value)) {
            return value;
        }
        return convertDictData(dictTranslatorAnnotation.type(), value,
                dictTranslatorAnnotation.isMultiVal(), dictTranslatorAnnotation.split());
    }

    /**
     * 数据字典转换:displayName->code.
     *
     * @param type        数据字典类型
     * @param displayName 显示值
     * @param isMult      是否时多值
     * @param splitSign   多值分割符
     * @return 转换后code
     */
    private String convertDictData(String type, String displayName, boolean isMult, String splitSign) {
        if (StringUtils.isEmpty(displayName)) {
            return displayName;
        }
        String[] displayNameArray;
        if (isMult) {
            displayNameArray = displayName.split(splitSign);
        } else {
            displayNameArray = new String[]{displayName};
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < displayNameArray.length; i++) {
            if (i > 0) {
                sb.append(splitSign);
            }
            sb.append(DictTranslatorServiceProxy.getInstance().translateDisplayNameToCode(type, displayNameArray[i]));
        }
        return sb.toString();
    }

}
