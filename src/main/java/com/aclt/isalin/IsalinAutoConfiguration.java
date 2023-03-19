package com.aclt.isalin;

import com.aclt.isalin.properties.IsalinProperties;
import com.aclt.isalin.aspect.TranslateAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IsalinProperties.class)
public class IsalinAutoConfiguration {

    @Autowired
    private IsalinProperties isalinProperties;

    @Bean
    @ConditionalOnMissingBean
    public TranslateAspect translateAspect() {
        return new TranslateAspect(isalinProperties);
    }

}
