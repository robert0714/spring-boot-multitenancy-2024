package com.thomasvitale.multitenant.config;
 

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass; 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.thomasvitale.multitenant.context.resolvers.HttpHeaderTenantResolver;
import com.thomasvitale.multitenant.data.hibernate.ConnectionProvider;
import com.thomasvitale.multitenant.data.hibernate.TenantIdentifierResolver;
import com.thomasvitale.multitenant.filter.TenantContextFilter;
import com.thomasvitale.multitenant.security.JWTInfoHelper;
import com.thomasvitale.multitenant.security.TenantAuthenticationManagerResolver;
import com.thomasvitale.multitenant.tenantdetails.PropertiesTenantDetailsService;
import com.thomasvitale.multitenant.tenantdetails.TenantDetailsProperties;
import com.thomasvitale.multitenant.tenantdetails.TenantDetailsService;
@Configuration
public class MultiTenantResourceServerWebMvcConfiguration {
	@Bean({ "multiTenantHeaderFilter", "multiTenantFilter" })
	@ConditionalOnMissingClass
	public TenantContextFilter multiTenantHeaderFilter(HttpHeaderTenantResolver httpRequestTenantResolver,
			TenantDetailsService tenantDetailsService, JWTInfoHelper jwtInfoHelper) {
		return new TenantContextFilter(httpRequestTenantResolver, tenantDetailsService, jwtInfoHelper);
	}

	@Bean
	@ConditionalOnMissingClass
	public HttpHeaderTenantResolver httpRequestTenantResolve(MultiTenantResourceServerProperties props) {
		return new HttpHeaderTenantResolver(props);
	}

	@Bean({ "jwtHelperr" })
	@ConditionalOnMissingClass
	public JWTInfoHelper jwtInfoHelper() {
		return new JWTInfoHelper();
	}
	@Bean 
	@Conditional(HeaderCondition.class)
	@ConditionalOnMissingClass
	public TenantAuthenticationManagerResolver TenantAuthenticationManagerResolver(TenantDetailsService tenantDetailsService) {
		return new TenantAuthenticationManagerResolver(tenantDetailsService);
	}
	
	@Bean 
	@ConditionalOnClass({DataSource.class ,TenantDetailsService.class })
	@ConditionalOnMissingClass
	public ConnectionProvider aConnectionProvider(@Autowired(required=false) DataSource dataSource,
			@Autowired(required=false)   TenantDetailsService tenantDetailsService) {
		return new ConnectionProvider(dataSource, tenantDetailsService);
	} 

	@Bean
	@ConditionalOnClass({DataSource.class ,TenantDetailsService.class })
	@ConditionalOnMissingClass
	public TenantIdentifierResolver aTenantIdentifierResolver( ) {
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
