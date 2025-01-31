package com.thomasvitale.instrumentservice.multitenancy.context.resolvers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.thomasvitale.instrumentservice.multitenancy.context.config.MultiTenantResourceServerProperties;

/**
 * Strategy used to resolve the current tenant from a header in an HTTP request.
 */
@Component
@RequiredArgsConstructor
public class HttpHeaderTenantResolver implements TenantResolver<HttpServletRequest> {
    
    private final MultiTenantResourceServerProperties props ;

	@Override
    @Nullable
	public String resolveTenantIdentifier(HttpServletRequest request) {
		String tenantHeader = props.getHeader().getHeaderName();
		return request.getHeader(tenantHeader);
	}

}
