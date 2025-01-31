package com.thomasvital.login.oauth;
 
 
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle; 
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod; 
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity; 
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.thomasvital.login.aggregate.LoginInputDto;
import com.thomasvital.login.config.OAuthProperties;
import com.thomasvital.login.multitenancy.context.TenantContextHolder;
import com.thomasvital.login.multitenancy.tenantdetails.PropertiesTenantDetailsService;
import com.thomasvital.login.multitenancy.tenantdetails.TenantDetails;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; 
 
/**
 * @author <a href="mailto:robert.lee@iisigroup.com">Robert Lee</a>
 *  <p>
 *  <code>https://github.com/resilience4j/resilience4j-spring-boot2-demo </code>
 *  <code>https://resilience4j.readme.io/v1.6.0/docs/getting-started-3  </code>
 *  <code>https://www.oauth.com/oauth2-servers/device-flow/token-request </code>
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class OauthClient {
	 
	private final RestTemplate webClient;
	protected final String TOKEN_JSON_PATH = "$['access_token']";
	 
	private final OAuthProperties oAuthProperties ; 
	
	
	private final PropertiesTenantDetailsService tenantDetailsService ;
	/**
	 * Post construct.
	 * @throws SSLException 
	 */
	@PostConstruct
	protected void postConstruct(){
		 
	}

	/**
	 * Get the token.
	 *
	 * @param oAuthProperties
	 * @return
	 */
	public String getToken( final LoginInputDto dto) {
	    String tenantId =	TenantContextHolder.getTenantIdentifier() ;
	    String tenantTokenUrl = null;
	    TenantDetails info  = tenantDetailsService.loadTenantByIdentifier(tenantId);
		if (info!=null) {
			log.info("tenantId: {}", tenantId);
			 
			String tokenUrlSuffix = oAuthProperties.getTokenUrlSuffix() ;
			
			tenantTokenUrl = String.format("%s%s", info.issuer(), tokenUrlSuffix);
			
			log.info("tenantTokenUrl: {}", tenantTokenUrl);
		} 		
		
		final String clientId = oAuthProperties.getClientId();
		final String clientSecret = oAuthProperties.getClientSecret();		
		
		final String tokenUrl ;
		
		if(StringUtils.isNotBlank(tenantTokenUrl)) {
			tokenUrl = tenantTokenUrl;
		}else {
			tokenUrl = oAuthProperties.getTokenUrl();
		}
		
		final int timeoutSec = oAuthProperties.getTimeoutSec();
		
		final HttpHeaders headers = createHeaders(clientId, clientSecret);
		headers.set("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		headers.set("Accept", "*/*");
		headers.setConnection("keep-alive");
		;

		final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", "password");
		map.add("username", dto.getUserName());
		map.add("password", dto.getPasswd());
		map.add("client_id", clientId);
		map.add("client_secret", clientSecret);
		map.add("scope", "openid");

		log.info("tokenUrl: {}" , tokenUrl);
		log.info("headers: {}" , ToStringBuilder.reflectionToString(headers, ToStringStyle.JSON_STYLE));
		log.info("bdoy: {}" , ToStringBuilder.reflectionToString(headers, ToStringStyle.JSON_STYLE));
		final HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers); 

		ResponseEntity<String> response = this.webClient.exchange(tokenUrl, HttpMethod.POST, entity, String.class);
		log.info(response.getBody());
 

		String responseBody = response.getBody();
		log.info(responseBody);

//		DocumentContext context = JsonPath.parse(responseBody);
//		String jwt = context.read(TOKEN_JSON_PATH).toString();
		
 
		return responseBody;
	}
	HttpHeaders createHeaders(String username, String password){
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(username, password, StandardCharsets.UTF_8); 
		return headers ; 
  	}

}
