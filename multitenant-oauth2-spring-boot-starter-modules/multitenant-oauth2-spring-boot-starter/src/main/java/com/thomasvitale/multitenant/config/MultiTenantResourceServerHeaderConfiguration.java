package com.thomasvitale.multitenant.config;
 

import javax.sql.DataSource;
 
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass; 
import org.springframework.context.annotation.Bean; 
import org.springframework.context.annotation.Configuration;

import com.thomasvitale.multitenant.context.resolvers.HttpHeaderTenantResolver;
import com.thomasvitale.multitenant.data.hibernate.TenantIdentifierResolver;
import com.thomasvitale.multitenant.tenantdetails.PropertiesTenantDetailsService;
import com.thomasvitale.multitenant.tenantdetails.TenantDetailsProperties;
import com.thomasvitale.multitenant.tenantdetails.TenantDetailsService;
  
@Configuration
public class MultiTenantResourceServerHeaderConfiguration { 
	@Bean
	@ConditionalOnMissingClass
	public HttpHeaderTenantResolver httpRequestTenantResolve(MultiTenantResourceServerProperties props) {
		return new HttpHeaderTenantResolver(props);
	}  
	@Bean
	@ConditionalOnClass({DataSource.class ,TenantDetailsService.class })
	@ConditionalOnMissingClass
	@ConditionalOnMissingBean
	public TenantIdentifierResolver tenantIdentifierResolver( ) {
		return new TenantIdentifierResolver( );
	}  
	
	@Bean 
	@ConditionalOnMissingClass
	public TenantDetailsService tenantDetailsService(TenantDetailsProperties tenantDetailsProperties ) {
		return new PropertiesTenantDetailsService( tenantDetailsProperties);
	} 
	@Bean 
	@ConditionalOnMissingClass
	public TenantDetailsProperties tenantDetailsProperties(  ) {
		return new TenantDetailsProperties(  );
	} 
}
