package com.cngc.admin.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 时间格式化属性.
 *
 * @author duanyl
 */
@Component
@ConfigurationProperties(prefix = "spring.jackson")
@Data
public class DateFormatProperties {
    private String[] dateParse;
}
