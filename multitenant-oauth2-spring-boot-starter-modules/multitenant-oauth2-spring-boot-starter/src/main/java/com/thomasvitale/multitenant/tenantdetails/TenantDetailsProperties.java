package com.thomasvitale.multitenant.tenantdetails;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "multitenancy")
public class TenantDetailsProperties {
	private List<TenantDetails> tenants;
	private String defaultSchema;
}
