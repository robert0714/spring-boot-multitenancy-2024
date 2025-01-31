package com.thomasvitale.multitenant.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.common.KeyValue;

import org.slf4j.MDC; 
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.filter.ServerHttpObservationFilter;

import com.thomasvitale.multitenant.context.TenantContextHolder;
import com.thomasvitale.multitenant.context.resolvers.HttpHeaderTenantResolver;
import com.thomasvitale.multitenant.security.JWTInfoHelper;
import com.thomasvitale.multitenant.tenantdetails.TenantDetailsService; 
/**
 * Establish a tenant context from an HTTP request, if tenant information is available.
 */
@RequiredArgsConstructor
@Slf4j 
public class TenantContextFilter extends OncePerRequestFilter {

	
	private final HttpHeaderTenantResolver httpRequestTenantResolver;
    private final TenantDetailsService tenantDetailsService;
    private final JWTInfoHelper jwtInfoHelper ;
	 

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tenantIdentifier = httpRequestTenantResolver.resolveTenantIdentifier(request);

        if (StringUtils.hasText(tenantIdentifier) && isTenantValid(tenantIdentifier)) {
            TenantContextHolder.setTenantIdentifier(tenantIdentifier);
            configureLogs(tenantIdentifier);
            configureTraces(tenantIdentifier, request);
        } else {
        	String realmName = jwtInfoHelper.showRealmName();
        	String userName = jwtInfoHelper.showUserName();
        	log.debug("jwt realm: %s".formatted(realmName));      
        	log.debug("jwt userinfo: %s".formatted(userName));      
        	log.debug("A valid tenant must be specified for requests to %s".formatted(request.getRequestURI()));        	
           // throw new TenantResolutionException("A valid tenant must be specified for requests to %s".formatted(request.getRequestURI()));
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            clear();
        }
	}

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        boolean result = request.getRequestURI().contains("/actuator")
				|| request.getRequestURI().contains("/swagger-ui");
		return result;
    }

    private boolean isTenantValid(String tenantIdentifier) {
        var tenantDetails = tenantDetailsService.loadTenantByIdentifier(tenantIdentifier);
        if(tenantDetails == null) {
        	log.error("'%s' is invalid tenantId,it does not exist in our configuration".formatted(tenantIdentifier));  
        }
        return tenantDetails != null && tenantDetails.enabled();
    }

	private void configureLogs(String tenantId) {
		MDC.put("tenantId", tenantId);
	}

	private void configureTraces(String tenantId, HttpServletRequest request) {
		ServerHttpObservationFilter.findObservationContext(request).ifPresent(context ->
				context.addHighCardinalityKeyValue(KeyValue.of("tenant.id", tenantId)));
	}

	private void clear() {
		MDC.remove("tenantId");
		TenantContextHolder.clear();
	}

}
