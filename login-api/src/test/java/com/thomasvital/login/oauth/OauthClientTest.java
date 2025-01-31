package com.thomasvital.login.oauth;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Disabled;

import com.thomasvital.login.aggregate.LoginInputDto;
import com.thomasvital.login.oauth.OauthClient;
@Disabled
@SpringBootTest
public class OauthClientTest {

	@BeforeEach
	protected void setUp() throws Exception {
	}
	@Autowired
	private OauthClient client;
	@Test
	public void testGetToken() {
		String jwt = client.getToken(LoginInputDto.builder().userName("unittest").passwd("unittest").build());
		assertNotNull(jwt);
	}

}
