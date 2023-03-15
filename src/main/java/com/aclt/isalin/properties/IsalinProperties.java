package com.aclt.isalin.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "aclt.isalin")
public class IsalinProperties {

    private String serviceUrl = "https://translation.googleapis.com/language/translate/v2";

    private String serviceKey;

}
