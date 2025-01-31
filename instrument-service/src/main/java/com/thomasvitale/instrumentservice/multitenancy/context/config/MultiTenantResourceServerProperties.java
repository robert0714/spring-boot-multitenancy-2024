package com.thomasvitale.instrumentservice.multitenancy.context.config;
 
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Multi-tenant resource server properties.
 */
@Component
@ConfigurationProperties("spring.security.oauth2.resourceserver.multitenant")
public class MultiTenantResourceServerProperties {

    public static final boolean DEFAULT_ENABLED = false; 
    /**
     * Whether multi-tenant resource server configuration is active.
     */
    private boolean enabled = DEFAULT_ENABLED;
 
 
    /**
     * Options for resolving the tenant by header.
     */
    private Header header = new Header();
 
    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
 

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

     

    public static class Header {

        public static final String DEFAULT_HEADER_NAME = "X-Tenant-Id";

        /**
         * Name of the HTTP header which is used for resolving the tenant.
         * Note that this is not suggested for production-grade applications.
         * For production-grade applications, rather resolve the tenant by JWT or another method of your choice.
         */
        private String headerName = DEFAULT_HEADER_NAME;

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }
    }

}