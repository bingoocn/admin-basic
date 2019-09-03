package com.cngc.admin.dictionary.translate.config;

import com.cngc.admin.dictionary.translate.annotation.DictTranslator;
import com.cngc.admin.dictionary.translate.utils.DictTranslateUtils;
import com.cngc.admin.dictionary.translate.utils.DictTranslatorServiceProxy;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.IOException;

/**
 * @author maxD
 */
public class DictTranslateSerializer extends JsonSerializer<String> implements ContextualSerializer {

    /**
     * 字典类型.
     */
    private String type;

    DictTranslateSerializer() {
        super();
    }

    DictTranslateSerializer(String type) {
        this.type = type;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if(DictTranslateUtils.getIsCodeAndNameStyle()) {
            Dictionary dictionary = new Dictionary();
            dictionary.setCode(value);
            dictionary.setName(DictTranslatorServiceProxy.getInstance().translateCodeToDisplayName(this.type, value));
            gen.writeObject(dictionary);
        }else {
            gen.writeObject(value);
        }
        DictTranslateUtils.removeIsCodeAndNameStyle();
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        DictTranslator dictTranslator = AnnotationUtils.synthesizeAnnotation(property.getAnnotation(DictTranslator.class), null);
        String type = dictTranslator.type();
        if (StringUtils.isEmpty(type)) {
            throw new JsonMappingException("未设置字典类型!");
        }
        return new DictTranslateSerializer(type);
    }

    @Data
    class Dictionary {
        private String name;
        private String code;
    }
}
