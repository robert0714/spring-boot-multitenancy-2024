package com.thomasvitale.multitenant.config;
 

import javax.sql.DataSource;

import org.flywaydb.core.Flyway; 
import org.springframework.boot.autoconfigure.domain.EntityScan;  
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories; 
import org.testcontainers.junit.jupiter.Testcontainers;
 
import com.thomasvitale.multitenant.app.instrumentservice.flyway.TenantFlywayMigrationInitializer;
import com.thomasvitale.multitenant.tenantdetails.TenantDetailsService; 
 

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackages = "com.thomasvitale.multitenant.app.instrumentservice" ,enableDefaultTransactions = true)
@EntityScan(basePackages = "com.thomasvitale.multitenant.app.instrumentservice")
@Testcontainers
public class InstrumentTestConfig {

//	@Bean
//	public PropertiesTenantDetailsService ptds(TenantDetailsProperties tenantDetailsProperties) {
//		return new PropertiesTenantDetailsService(tenantDetailsProperties);
//	}
//	@Bean
//	public TenantDetailsProperties tenantDetailsProperties( ) {
//		return new TenantDetailsProperties( );
//	}  
 
	@Bean
	public TenantFlywayMigrationInitializer tenantFlywayMigration(final DataSource dataSource,
			final Flyway defaultFlyway,final TenantDetailsService tenantDetailsService) {
		
		return new TenantFlywayMigrationInitializer(dataSource , defaultFlyway , tenantDetailsService);
	}

	
	
}