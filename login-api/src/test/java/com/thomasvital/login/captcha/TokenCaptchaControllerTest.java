package com.thomasvital.login.captcha;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity; 
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomasvital.login.captcha.TokenCaptchaObj;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test; 

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT ,properties = {"login.page.api.showCaptchaAndCode=true"} )
public class TokenCaptchaControllerTest {
 

	@Autowired
	private TestRestTemplate testRestTemplate;
//	private HttpEntity<String> httpStringEntity;
	private HttpEntity<Object> httpObjectEntity;
	private HttpHeaders headers;
//	private ObjectMapper objectMapper;
 

	@BeforeEach
	public void setUp() throws Exception {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-Requested-With", "XMLHttpRequest");
//		objectMapper = new ObjectMapper();
	} 
 

	@Test
//	@Disabled
	public void testVerifyCaptcha() throws Exception {
		String uri = "/captcha/verify";
		final TokenCaptchaObj estimateTestObj = getCaptcha() .getBody();
		estimateTestObj.setInputCaptcha(estimateTestObj.getCaptcha());
		httpObjectEntity = new HttpEntity<Object>(estimateTestObj, headers);
		ResponseEntity<TokenCaptchaObj> responseEntity = testRestTemplate.postForEntity(uri, httpObjectEntity, TokenCaptchaObj.class);
		assertEquals(200, responseEntity.getStatusCodeValue());
		
		assertEquals("true", responseEntity.getBody().getValidate());
		assertEquals("PASS", responseEntity.getBody().getCaptchaStatus());
		assertNotNull(  responseEntity.getBody().getCaptcha());
		log.info("body: {}", responseEntity.getBody());
		log.info(responseEntity.getHeaders().getAccessControlAllowOrigin());
		log.info(responseEntity.getHeaders().toString());
	}
	protected  TokenCaptchaObj createNewTokenCaptchaObjV1() {
		final TokenCaptchaObj result =new TokenCaptchaObj();
		result.setToken("fdf4f301-f2ec-48c2-82b7-54c6d922428f");
		return result;
	}

	@Test
	public void testGetCaptcha() throws Exception { 

		ResponseEntity<TokenCaptchaObj> responseEntity = getCaptcha() ;

		assertEquals(200, responseEntity.getStatusCodeValue());
		assertNotNull(  responseEntity.getBody().getCaptcha());
		log.info("body: {}", responseEntity.getBody());
		log.info(responseEntity.getHeaders().getAccessControlAllowOrigin());
		log.info(responseEntity.getHeaders().toString());  
	} 
	protected ResponseEntity<TokenCaptchaObj> getCaptcha(){
		String uri = "/captcha";
		final TokenCaptchaObj estimateTestObj = createNewTokenCaptchaObjV1() ; 
		
		httpObjectEntity = new HttpEntity<Object>(estimateTestObj, headers);

		ResponseEntity<TokenCaptchaObj> responseEntity = testRestTemplate.postForEntity(uri, httpObjectEntity, TokenCaptchaObj.class);
		
		return responseEntity ; 

	}
}
