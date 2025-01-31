package com.thomasvitale.login.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * -Dfile.encoding=UTF-8
 * */
@Component
@ConfigurationProperties(prefix = "oauth2.server"  , ignoreInvalidFields = true)
@Data
public class OAuthProperties {

	/**
	 * token URL
	 */
	private String tokenUrl;

	/**
	 * client ID
	 */
	private String clientId;
	
	/**
	 * client secret
	 */
	private String clientSecret;

	/**
	 * time out second
	 */
	private int timeoutSec;
	
	/**
	 *  token URL suffix
	 */
	private String tokenUrlSuffix;
	
 
}
