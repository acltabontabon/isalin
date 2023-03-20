package com.aclt.isalin;

import com.aclt.isalin.properties.IsalinProperties;
import com.aclt.isalin.aspect.TranslateAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(IsalinProperties.class)
@ConditionalOnProperty(prefix = "isalin", name = "enabled", havingValue = "true", matchIfMissing = true)
public class IsalinAutoConfiguration {

    @Autowired
    private IsalinProperties isalinProperties;

    @Bean
    @ConditionalOnMissingBean
    public TranslateAspect translateAspect() {
        log.info("Isalin is enabled.");
        return new TranslateAspect(isalinProperties);
    }

}
