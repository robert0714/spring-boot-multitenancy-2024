package com.thomasvitale.instrumentservice.config;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing; 
import org.springframework.transaction.annotation.EnableTransactionManagement; 

import java.time.OffsetDateTime; 
import java.util.Optional; 
  

import com.thomasvitale.instrumentservice.multitenancy.security.JWTInfoHelper; 

@EnableTransactionManagement
@Configuration
@EnableJpaAuditing(auditorAwareRef="auditorProvider",dateTimeProviderRef="offsetDateTimeProvider")
public class JpaAuditingConfig {
	@Bean
	public JWTInfoHelper JWTInfoHelper() {
		return new JWTInfoHelper();
	}
	@Bean
    public AuditorAware<String> auditorProvider( JWTInfoHelper helper) {
        return () -> {
        	return Optional.of(helper.showUserName());
        };
    }
	@Bean // Makes OffsetDateTime compatible with auditing fields
    public DateTimeProvider offsetDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }

}
