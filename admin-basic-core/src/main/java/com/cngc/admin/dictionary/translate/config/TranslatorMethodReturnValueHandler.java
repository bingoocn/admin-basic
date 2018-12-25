package com.cngc.admin.dictionary.translate.config;

import com.cngc.admin.dictionary.translate.annotation.EnableReturnValueDictTranslate;
import com.cngc.admin.dictionary.translate.utils.DictTranslateUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * mvc中controller返回值进行数据字典类型数据转换.
 *
 * @author duanlyl
 */
public class TranslatorMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    /**
     * 注入requestMappingHandlerAdapter bean
     */
    private RequestMappingHandlerAdapter pb;

    TranslatorMethodReturnValueHandler(RequestMappingHandlerAdapter pb) {
        this.pb = pb;
    }

    @Override
    public boolean supportsReturnType(@NotNull MethodParameter returnType) {
        for (Annotation annotation : returnType.getMethodAnnotations()) {
            if (annotation.annotationType().equals(EnableReturnValueDictTranslate.class)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void handleReturnValue(Object returnValue,@NotNull MethodParameter returnType,@NotNull ModelAndViewContainer mavContainer,@NotNull NativeWebRequest webRequest) throws Exception {
        returnValue = DictTranslateUtils.processData(returnValue, returnType.getMethodAnnotations());
        /*Executable executable = returnType.getExecutable();
        Field declaredAnnotationsField = Executable.class.getDeclaredField("declaredAnnotations");
        declaredAnnotationsField.setAccessible(true);
        Map<Class<? extends Annotation>, Annotation> declaredAnnotations = (Map<Class<? extends Annotation>, Annotation>) declaredAnnotationsField.get(executable);
        declaredAnnotations.remove(EnableReturnValueDictTranslate.class);*/

        HandlerMethodReturnValueHandlerComposite oriReturnValueHandlers;
        try {
            Field returnValueHandlersField = RequestMappingHandlerAdapter.class.getDeclaredField("returnValueHandlers");
            returnValueHandlersField.setAccessible(true);
            oriReturnValueHandlers = (HandlerMethodReturnValueHandlerComposite) returnValueHandlersField.get(pb);
        } catch (Exception e) {
            throw new RuntimeException("无法处理RequestMappingHandlerAdapter.returnValueHandlers", e);
        }

        HandlerMethodReturnValueHandlerComposite newReturnValueHandlers = new HandlerMethodReturnValueHandlerComposite();
        for(HandlerMethodReturnValueHandler handlerMethodReturnValueHandler : oriReturnValueHandlers.getHandlers()) {
            if(!(handlerMethodReturnValueHandler instanceof TranslatorMethodReturnValueHandler)) {
                newReturnValueHandlers.addHandler(handlerMethodReturnValueHandler);
            }
        }
        newReturnValueHandlers.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }
}
