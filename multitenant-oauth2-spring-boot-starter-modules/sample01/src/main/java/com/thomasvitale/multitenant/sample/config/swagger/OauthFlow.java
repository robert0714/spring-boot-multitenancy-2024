package com.thomasvitale.multitenant.sample.config.swagger;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class OauthFlow {
	private String authorizationUrl;
	private String tokenUrl; 
}
