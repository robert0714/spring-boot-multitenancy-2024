package com.thomasvitale.login.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.thomasvitale.login.config.CorsFilter;
 

class CorsFilterTest {
	CorsFilter filter ;
	@BeforeEach
	protected void setUp() throws Exception {
		filter =new CorsFilter();
	}

	@AfterEach
	protected void tearDown() throws Exception {
	}

	@Test
	public void testMatchesOrigins() {
		Set<String> rules =  Set.of("*.iisigroup", "*.esg", "*.ocarbon.net",
				"http://192.168.62.37", "http://localhost:8080", "http://localhost:3000", "http://localhost:4000");
		boolean test001 = filter.matchesOrigins("iisi01.esg", rules);
		assertTrue(test001);
		boolean test002 = filter.matchesOrigins("iisi01.esg1", rules);
		assertFalse(test002);
	}

}
