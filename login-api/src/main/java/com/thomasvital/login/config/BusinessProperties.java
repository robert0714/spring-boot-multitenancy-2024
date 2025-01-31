package com.thomasvital.login.config;

import org.springframework.boot.context.properties.ConfigurationProperties; 
import org.springframework.stereotype.Component;
 

import lombok.Data; 
 
/**
 * -Dfile.encoding=UTF-8
 * */
@Component
@ConfigurationProperties(prefix = "login.page.api"  , ignoreInvalidFields = true)
@Data
public class BusinessProperties { 
	
	private int timeout;
	
	private boolean productionMode;	
	private boolean f5Cache;
	private boolean showCaptchaAndCode; 
	
	private boolean disableCatchaVerify;
}
