package com.thomasvitale.instrumentservice.multitenancy.web;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.thomasvitale.instrumentservice.multitenancy.context.TenantContextHolder;
import com.thomasvitale.instrumentservice.multitenancy.context.resolvers.HttpHeaderTenantResolver;
//import com.thomasvitale.instrumentservice.multitenancy.exceptions.TenantResolutionException;
import com.thomasvitale.instrumentservice.multitenancy.security.JWTInfoHelper;
import com.thomasvitale.instrumentservice.multitenancy.tenantdetails.TenantDetailsService;

import io.micrometer.common.KeyValue;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.filter.ServerHttpObservationFilter;

/**
 * Establish a tenant context from an HTTP request, if tenant information is available.
 */
@Component
@Slf4j
@RequiredArgsConstructor
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
        	log.info("jwt realm: %s".formatted(realmName));      
        	log.info("jwt userinfo: %s".formatted(userName));      
        	log.info("A valid tenant must be specified for requests to %s".formatted(request.getRequestURI()));        	
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
        return request.getRequestURI().startsWith("/actuator");
    }

    private boolean isTenantValid(String tenantIdentifier) {
        var tenantDetails = tenantDetailsService.loadTenantByIdentifier(tenantIdentifier);
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
