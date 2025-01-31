package com.thomasvitale.multitenant.tenantdetails;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
 
 
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
        return getById(identifier).orElse(null);
    }

	public Optional<? extends TenantDetails> getById(String identifier) {
		
        return tenantDetailsProperties.getTenants().stream()
                .filter(TenantDetails::enabled)
				.filter(tenantDetails -> Objects.equals(tenantDetails.identifier(), identifier))
                .findFirst() ;
    }

	@Override
	public Optional<String> getDefaultSchema() {
		String schemaFromProp = tenantDetailsProperties.getDefaultSchema();
		return Optional.ofNullable(schemaFromProp);
	}
}
