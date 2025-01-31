package com.thomasvitale.multitenant.context.resolvers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.thomasvitale.multitenant.config.MultiTenantResourceServerProperties;
import com.thomasvitale.multitenant.context.resolvers.HttpHeaderTenantResolver;

class HttpHeaderTenantResolverTest {
	private HttpHeaderTenantResolver aHttpHeaderTenantResolver;
	@BeforeEach
	protected void setUp() throws Exception {
		MultiTenantResourceServerProperties props = mock(MultiTenantResourceServerProperties.class);
		MultiTenantResourceServerProperties.Header header = mock(MultiTenantResourceServerProperties.Header.class);
		when(props.getHeader()).thenReturn(header);
		when(header.getHeaderName()).thenReturn("x-tenant-id");
		 
		aHttpHeaderTenantResolver = new HttpHeaderTenantResolver(props);
	}

	@AfterEach
	protected void tearDown() throws Exception {
	}

	@Test
	public void testValidateIPaddress() {
		boolean test01 = aHttpHeaderTenantResolver.validateIPaddress("192.168.62.37");
		assertTrue(test01);
		boolean test02 = aHttpHeaderTenantResolver.validateIPaddress("iisi01.esg");
		assertFalse(test02);
	}

	@Test
	public void testGetTenantFromSubdomain() {
		String  test01 = aHttpHeaderTenantResolver.getTenantFromSubdomain("192.168.62.37");
		assertNull(test01);
		String test02 = aHttpHeaderTenantResolver.getTenantFromSubdomain("iisi01.esg");
		assertEquals("iisi01" , test02);
	}

}
