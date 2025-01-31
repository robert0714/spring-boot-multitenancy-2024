package com.iisigroup.multitenant.sample.config.swagger;

import static org.springdoc.core.utils.Constants.SPRINGDOC_ENABLED;

import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.utils.Constants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
 
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Lazy(false)
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = Constants.SPRINGDOC_PREFIX)
@ConditionalOnProperty(name = SPRINGDOC_ENABLED, matchIfMissing = true)
@ConditionalOnBean(SpringDocConfiguration.class)
public class SpringDocConfigProperties {
	@NestedConfigurationProperty
	private OauthFlow oauthFlow ; 
}
