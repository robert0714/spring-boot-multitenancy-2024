package com.thomasvitale.multitenant.config;
 
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
 

/**
 * {@link AutoConfiguration Auto-configuration} for multi-tenant resource server support.
 */ 
@AutoConfigureBefore({
       WebMvcAutoConfiguration.class,
       SecurityAutoConfiguration.class,
       UserDetailsServiceAutoConfiguration.class
})
@AutoConfiguration
@EnableConfigurationProperties(MultiTenantResourceServerProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class MultiTenantResourceServerAutoConfiguration {
	
	@Configuration
	@Conditional(HeaderCondition.class)
    @Import(MultiTenantResourceServerWebMvcConfiguration.class) 
    static class WebMvcConfiguration {
    }
	
	 
	
}
