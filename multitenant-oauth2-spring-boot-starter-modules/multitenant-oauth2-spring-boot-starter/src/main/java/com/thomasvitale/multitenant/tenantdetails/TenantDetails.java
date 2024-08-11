package com.thomasvitale.multitenant.tenantdetails;
 
  
/**
 * Provides core tenant information.
 */
public record TenantDetails(
        String identifier,
        boolean enabled,
        String schema,
        String issuer,
        String clientId ,
        String clientSecret
) {}
