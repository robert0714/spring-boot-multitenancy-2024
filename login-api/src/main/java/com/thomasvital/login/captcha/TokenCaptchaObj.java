package com.thomasvital.login.captcha;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomasvital.login.aggregate.LoginInputDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@io.swagger.v3.oas.annotations.media.Schema(description ="captcha物件")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenCaptchaObj {

	@io.swagger.v3.oas.annotations.media.Schema(description ="使用者看到Captcha所輸入值")
	@JsonProperty("inputCaptcha")
	private String inputCaptcha;
	
	@io.swagger.v3.oas.annotations.media.Schema(description ="Captcha影像base64值")
	@JsonProperty("captchaImg")
	private String captchaImg;
	
	@io.swagger.v3.oas.annotations.media.Schema(description ="Captcha所對應token")
	@JsonProperty("_token")
	private String token;
	
	
	@JsonProperty("validate")
	private String validate;

//	@JsonProperty("verifyCode")
//	private String verifyCode;
	
	
//	@JsonProperty("inputCode")
//	private String inputCode;

	@JsonProperty("captcha")
	private String captcha;

	 /** 
     * token_status
     * <p/> 
     *  */
	@JsonProperty("tokenStatus")
	private String tokenStatus;
	
	 /** 
     * captcha_status
     * <p/> 
     *  */
	@JsonProperty("captchaStatus")
	private String captchaStatus;
	
//	 /** 
//     * sms_mail_reach_query_limit
//     * <p/> 
//     *  */
//	@JsonProperty("smsMailReachQueryLimit") // 查詢次數達到限制 max=6
//	private String smsMailReachQueryLimit;

	@Override
	public String toString() {
		return "{" +
				"\"captcha\":\"" + captcha + "\"" +
				",\"_token\":\"" + token + "\"" +
				"}";
	}
}

