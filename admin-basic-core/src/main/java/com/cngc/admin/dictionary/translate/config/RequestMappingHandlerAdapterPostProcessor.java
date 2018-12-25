package com.cngc.admin.dictionary.translate.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * RequestMappingHandlerAdapter bean处理类,支持controller层数据字典转换.
 *
 * @author duanyl
 */
@Component
public class RequestMappingHandlerAdapterPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, String beanName) throws BeansException {

        if (bean instanceof RequestMappingHandlerAdapter) {
            RequestMappingHandlerAdapter pb = (RequestMappingHandlerAdapter) bean;
            List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

            resolvers.add(new TranslatorMethodArgumentResolver(pb));
            Optional.ofNullable(pb.getArgumentResolvers())
                    .ifPresent(resolvers::addAll);
            pb.setArgumentResolvers(resolvers);

            List<HandlerMethodReturnValueHandler> returnHandlers = new ArrayList<>();
            returnHandlers.add(new TranslatorMethodReturnValueHandler(pb));
            Optional.ofNullable(pb.getReturnValueHandlers())
                    .ifPresent(returnHandlers::addAll);
            pb.setReturnValueHandlers(returnHandlers);
        }
        return bean;
    }


}