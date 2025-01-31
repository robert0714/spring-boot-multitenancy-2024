package com.thomasvital.login.aggregate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@io.swagger.v3.oas.annotations.media.Schema(description ="登入物件")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginInputDto {
	@io.swagger.v3.oas.annotations.media.Schema(description ="公司統一編號")
	private String snNumber;
	
	@io.swagger.v3.oas.annotations.media.Schema(description ="使用者帳號")
	private String userName;
	
	@io.swagger.v3.oas.annotations.media.Schema(description ="使用者密碼")
	private String passwd;
	
	@io.swagger.v3.oas.annotations.media.Schema(description ="catchpa圖形驗證輸入值")
	private String catchpa;
	
	@io.swagger.v3.oas.annotations.media.Schema(description ="catchpa圖形驗證代表token")
	private String token;
}
