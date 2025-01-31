package com.thomasvital.login.multitenancy.web;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.thomasvital.login.multitenancy.context.TenantContextHolder;
import com.thomasvital.login.multitenancy.context.resolvers.HttpHeaderTenantResolver;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter; 
/**
 * Establish a tenant context from an HTTP request, if tenant information is available.
 */
@Component
public class TenantContextFilter extends OncePerRequestFilter {

	private final HttpHeaderTenantResolver httpRequestTenantResolver; 

	public TenantContextFilter(HttpHeaderTenantResolver httpHeaderTenantResolver ) {
		this.httpRequestTenantResolver = httpHeaderTenantResolver; 
    }

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tenantIdentifier = httpRequestTenantResolver.resolveTenantIdentifier(request);

        if (StringUtils.hasText(tenantIdentifier)  ) {
        	TenantContextHolder.setTenantIdentifier(tenantIdentifier);
        }  

        try {
            filterChain.doFilter(request, response);
        } finally {
        	clear() ;  
        }
	}

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/actuator");
    }	 
    private void clear() {
		MDC.remove("tenantId");
		TenantContextHolder.clear();
	}
}
