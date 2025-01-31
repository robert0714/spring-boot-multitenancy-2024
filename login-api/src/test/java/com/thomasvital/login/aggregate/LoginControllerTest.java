package com.thomasvital.login.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.thomasvital.login.aggregate.LoginInputDto;
import com.thomasvital.login.captcha.TokenCaptchaObj;

import lombok.extern.slf4j.Slf4j;
@Disabled
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT ,properties = {"login.page.api.showCaptchaAndCode=true"}) 
public class LoginControllerTest {

	protected final String TOKEN_JSON_PATH = "$['access_token']";
	@Autowired
	private TestRestTemplate testRestTemplate; 
	private HttpEntity<Object> httpObjectEntity;
	private HttpHeaders headers;


	@BeforeEach
	public void setUp() throws Exception {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-Requested-With", "XMLHttpRequest");
//		objectMapper = new ObjectMapper();
	} 
 

	@Test 
	public void testVerifyCaptcha() throws Exception {
		String uri = "/auth/verify";
		final TokenCaptchaObj estimateTestObj = getCaptcha() .getBody();
		String catchValue = estimateTestObj.getCaptcha();
		String token = estimateTestObj.getToken();
		assertNotNull( catchValue );
		assertNotNull( token );
		
		LoginInputDto body = LoginInputDto.builder().passwd("unittest").userName("unittest").catchpa(catchValue).token(token).build();
		
		httpObjectEntity = new HttpEntity<Object>(body, headers);
		ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(uri, httpObjectEntity, String.class);
		assertEquals(200, responseEntity.getStatusCodeValue());
		
		final String responseBody = responseEntity.getBody() ;
		
		assertNotNull( responseBody );
		
		log.info("body: {}", responseEntity.getBody());
		log.info(responseEntity.getHeaders().getAccessControlAllowOrigin());
		log.info(responseEntity.getHeaders().toString());
		
		DocumentContext context = JsonPath.parse(responseBody);
		String jwt = context.read(TOKEN_JSON_PATH).toString();
		assertNotNull( jwt ); 
		log.info(jwt);
	}
	@Test 
	public void testVerifyCaptchaInMultiTenancy() throws Exception {
		String uri = "/auth/verify";
		final TokenCaptchaObj estimateTestObj = getCaptcha() .getBody();
		String catchValue = estimateTestObj.getCaptcha();
		String token = estimateTestObj.getToken();
		assertNotNull( catchValue );
		assertNotNull( token );
		
		LoginInputDto body = LoginInputDto.builder().passwd("unittest").userName("unittest").catchpa(catchValue).token(token).build();
		
		HttpHeaders headersMultiTenancy = SerializationUtils.clone(headers);
		headersMultiTenancy.set("X-Tenant-Id", "dukes");
		httpObjectEntity = new HttpEntity<Object>(body, headersMultiTenancy);
		ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(uri, httpObjectEntity, String.class);
		assertEquals(200, responseEntity.getStatusCodeValue());
		
		final String responseBody = responseEntity.getBody() ;
		
		assertNotNull( responseBody );
		
		log.info("body: {}", responseEntity.getBody());
		log.info(responseEntity.getHeaders().getAccessControlAllowOrigin());
		log.info(responseEntity.getHeaders().toString());
		
		DocumentContext context = JsonPath.parse(responseBody);
		String jwt = context.read(TOKEN_JSON_PATH).toString();
		assertNotNull( jwt ); 
		log.info(jwt);
	}
	@Test
	public void testVerifyCaptcha401() throws Exception {
		String uri = "/auth/verify";
		final TokenCaptchaObj estimateTestObj = getCaptcha() .getBody();
		String catchValue = null;
		String token = estimateTestObj.getToken();
		 
		
		LoginInputDto body = LoginInputDto.builder().passwd("unittest").userName("unittest").catchpa(catchValue).token(token).build();
		
		httpObjectEntity = new HttpEntity<Object>(body, headers);
		
		assertThrows(org.springframework.web.client.ResourceAccessException.class, () -> {
			ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(uri, httpObjectEntity, String.class);
			assertEquals(401, responseEntity.getStatusCodeValue());
			
			final String responseBody = responseEntity.getBody() ;
			
			assertNotNull( responseBody );
			
			log.info("body: {}", responseEntity.getBody());
			 
		}); 
		
	}
	protected ResponseEntity<TokenCaptchaObj> getCaptcha(){
		String uri = "/captcha";
		final TokenCaptchaObj estimateTestObj = createNewTokenCaptchaObjV1() ; 
		
		httpObjectEntity = new HttpEntity<Object>(estimateTestObj, headers);

		ResponseEntity<TokenCaptchaObj> responseEntity = testRestTemplate.postForEntity(uri, httpObjectEntity, TokenCaptchaObj.class);
		
		return responseEntity ; 

	}
	protected  TokenCaptchaObj createNewTokenCaptchaObjV1() {
		final TokenCaptchaObj result =new TokenCaptchaObj();
		result.setToken(null);
		return result;
	}

}
