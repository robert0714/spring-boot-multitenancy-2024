package com.thomasvitale.multitenant.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.HttpServletRequest;
 

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtBearerTokenAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import com.thomasvitale.multitenant.context.TenantContextHolder;
import com.thomasvitale.multitenant.tenantdetails.TenantDetails;
import com.thomasvitale.multitenant.tenantdetails.TenantDetailsService;


public class TenantAuthenticationManagerResolver implements AuthenticationManagerResolver<HttpServletRequest> {

	private static final Map<String,AuthenticationManager> authenticationManagers = new ConcurrentHashMap<>();
	private final TenantDetailsService tenantDetailsService;

	public TenantAuthenticationManagerResolver(TenantDetailsService tenantDetailsService) {
        this.tenantDetailsService = tenantDetailsService;
	}

	@Override
	public AuthenticationManager resolve(HttpServletRequest request) {
		//var tenantId = TenantContextHolder.getRequiredTenantIdentifier();
		String tenantId = TenantContextHolder.getTenantIdentifier();
		if (tenantId == null) {
			tenantId = "default";//為了地端版本相容性
		}
		
//		return authenticationManagers.computeIfAbsent(tenantId, this::buildAuthenticationManager);
//		return authenticationManagers.computeIfAbsent(tenantId, this::buildJwtAuthenticationManager);
		return authenticationManagers.computeIfAbsent(tenantId, this::buildOpaqueAuthenticationManager);
	}

	private AuthenticationManager buildAuthenticationManager(String tenantId) {
		var issuerUri = tenantDetailsService.loadTenantByIdentifier(tenantId).issuer();
		var jwtAuthenticationprovider = new JwtAuthenticationProvider(JwtDecoders.fromIssuerLocation(issuerUri));
		return jwtAuthenticationprovider::authenticate;
	}
	
	// Mimic the default configuration for JWT validation.	
	private AuthenticationManager  buildJwtAuthenticationManager(String tenantId) {
	
	    // this is the keys endpoint for okta
	    var issuerUri = tenantDetailsService.loadTenantByIdentifier(tenantId).issuer();
	    
	    // see http://localhost:8080/realms/dukes/.well-known/openid-configuration
	    String jwkSetUri = issuerUri + "/protocol/openid-connect/certs";
	    
	    // This is basically the default jwt logic	
	    JwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
	    
	    JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
	 
	    authenticationProvider.setJwtAuthenticationConverter(new JwtBearerTokenAuthenticationConverter());
	 
	    return authenticationProvider::authenticate;	 
	}
	    
	// Mimic the default configuration for opaque token validation
	private AuthenticationManager buildOpaqueAuthenticationManager(String tenantId) {
		TenantDetails tenant = tenantDetailsService.loadTenantByIdentifier(tenantId);
		var issuerUri = tenant.issuer();

		// see http://localhost:8080/realms/dukes/.well-known/openid-configuration
		String introspectionUri = issuerUri + "/protocol/openid-connect/token/introspect";
		String clientId = tenant.clientId() ;
		String clientSecret = tenant.clientSecret();

		// The default opaque token logic
		OpaqueTokenIntrospector introspectionClient =new SpringOpaqueTokenIntrospector(introspectionUri, clientId,
				clientSecret);
		return new OpaqueTokenAuthenticationProvider(introspectionClient)::authenticate;
	}

}
