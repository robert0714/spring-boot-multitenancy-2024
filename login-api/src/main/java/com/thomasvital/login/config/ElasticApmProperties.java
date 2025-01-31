package com.thomasvital.login.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "elastic.apm"  , ignoreInvalidFields = true) 
@Data
public class ElasticApmProperties { 
    private String serverUrl; 
    private String serviceName; 
    private String secretToken;
    private String environment;
    private String applicationPackages;
    private String logLevel;
    private boolean enabled ;
}
