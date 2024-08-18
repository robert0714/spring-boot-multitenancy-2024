package com.thomasvitale.multitenant.context.resolvers;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import com.thomasvitale.multitenant.config.MultiTenantResourceServerProperties; 

/**
 * Strategy used to resolve the current tenant from a header in an HTTP request.
 */ 

public class HttpHeaderTenantResolver implements TenantResolver<HttpServletRequest> {
    
	private static final Logger log = LoggerFactory.getLogger(HttpHeaderTenantResolver.class);
	
    private final MultiTenantResourceServerProperties props ;

    public HttpHeaderTenantResolver(MultiTenantResourceServerProperties props) {
    	this.props=props ;
    }
    
	@Override
    @Nullable
	public String resolveTenantIdentifier(HttpServletRequest request) {
		String tenantHeader = props.getHeader().getHeaderName();
		String tenantIdFromHeader = request.getHeader(tenantHeader);
		return Optional.ofNullable( tenantIdFromHeader)
				      .orElse(Optional.ofNullable(tenantIdFromHost())
				    		  .orElse(null));
	}
	protected String tenantIdFromHost(){
		UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentRequest().build();
		String host = uriComponents.getHost();
		log.info("hostname resolved by spring component: {}" ,host);
		String tenantIdFromHost = getTenantFromSubdomain(host);
		return tenantIdFromHost;
	}
	protected boolean validateIPaddress(String host) {
		String regularExpression = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$" ;
		return host.matches(regularExpression) ;
	}
	
	protected String getTenantFromSubdomain(String hostname) {
		if (hostname == null) {
			return null;
		}
		boolean isIp = validateIPaddress(hostname);
		if (isIp ) {
			return null;
		}
		String[] array = hostname.split("\\.") ;
		if (array != null && array.length > 0) {
			String subdomain = array[0];
			return subdomain;
		}else {
			log.error("hostname: {}" ,hostname);
			return null ;
		} 
	}

}
