package com.thomasvitale.multitenant.security;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken; 
 
public class JWTInfoHelper {
	public String showUserName() {
		AbstractOAuth2TokenAuthenticationToken<?> bta = bta() ;
		if (bta==null) {
			return "unknown from security";
		}
		Map<String, Object> tas = bta.getTokenAttributes(); 
		String preferredUsername = (String) tas.get("preferred_username");
 
		return preferredUsername;
	}
	public String showRealmName() {
		AbstractOAuth2TokenAuthenticationToken<?> bta = bta() ;
		if (bta==null) {
			return "unknown from security";
		}
		Map<String, Object> tas = bta.getTokenAttributes();
		String iss = (String)tas.get("iss");
		String realmName = iss.substring(iss.lastIndexOf("/") + 1, iss.length());
//		String [] tmp= iss.split( "/");
//		String realmName= tmp[4]; 
		return realmName;
	}
	public String showToken() {
		AbstractOAuth2TokenAuthenticationToken<?> bta = bta() ;
		if (bta==null) {
			return "unknown from security";
		}
		return bta.getToken().getTokenValue();
	}
	protected AbstractOAuth2TokenAuthenticationToken<?> bta() {
		SecurityContext sc = SecurityContextHolder.getContext(); 
        Authentication authentication = sc.getAuthentication();
		if (authentication instanceof BearerTokenAuthentication) {
			BearerTokenAuthentication bta = (BearerTokenAuthentication) authentication; 
			return bta;
		}else if (authentication instanceof JwtAuthenticationToken) {
			JwtAuthenticationToken bta = (JwtAuthenticationToken) authentication; 
			return bta;
		}
		return null;
	}
}
