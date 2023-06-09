package com.acltabontabon.isalin.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "isalin")
public class IsalinProperties {

    private boolean enabled = true;

    private String projectId;

}
