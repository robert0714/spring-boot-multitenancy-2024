package com.thomasvital.login.multitenancy.tenantdetails;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "multitenancy" , ignoreInvalidFields = true)
@Component
@Data
public class TenantDetailsProperties  { 
	private List<TenantDetails> tenants =new ArrayList<TenantDetails>();
}
