package com.thomasvital.login.multitenancy.tenantdetails;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PropertiesTenantDetailsService implements TenantDetailsService {

    private final TenantDetailsProperties tenantDetailsProperties;

    public PropertiesTenantDetailsService(TenantDetailsProperties tenantDetailsProperties) {
        this.tenantDetailsProperties = tenantDetailsProperties;
    }

    @Override
    public List<TenantDetails> loadAllTenants() {
        return tenantDetailsProperties.getTenants();
    }

    @Override
    public TenantDetails loadTenantByIdentifier(String identifier) {
    	if(identifier==null) {
    		return null;
    	}
    	List<TenantDetails> tenants = tenantDetailsProperties.getTenants() ;
    	
        return tenants.stream()
                .filter(TenantDetails::enabled)
                .filter(tenantDetails -> identifier.equals(tenantDetails.identifier()))
                .findFirst().orElse(null);
    }

}
