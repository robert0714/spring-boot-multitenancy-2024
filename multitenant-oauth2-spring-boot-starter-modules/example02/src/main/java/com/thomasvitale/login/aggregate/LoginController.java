package com.thomasvitale.login.aggregate;
import java.io.IOException;

import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
 
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.thomasvitale.login.captcha.TokenCaptchaObj;
import com.thomasvitale.login.captcha.TokenCaptchaService;
import com.thomasvitale.login.config.BusinessProperties;
import com.thomasvitale.login.oauth.OauthClient;

@Tag(name ="captcha api",description = "產生 captcha與確認captcha")
@RequiredArgsConstructor 
@RestController
public class LoginController {
	 
	private final  BusinessProperties properties;
 
	private final  TokenCaptchaService tokenCaptchaService;
	
	 
    private final  OauthClient  oauthClient;

	@Operation(description ="檢查使用者與帳號密碼..以及確認 captcha")
	@PostMapping(value="/auth/verify" ,produces = MediaType.APPLICATION_JSON_VALUE) 
	public String verifyCaptcha(@RequestBody LoginInputDto tokenCaptchaObj)throws ServletException, IOException {
		if (properties.isDisableCatchaVerify()) {
			return oauthClient.getToken(tokenCaptchaObj);
		}
		
		String inputCaptcha = tokenCaptchaObj.getCatchpa();
		String token = tokenCaptchaObj.getToken();
		final TokenCaptchaObj estimateTestObj = TokenCaptchaObj.builder().inputCaptcha(inputCaptcha).token(token).build();		
		TokenCaptchaObj result = 	tokenCaptchaService.captchaVerify(estimateTestObj);

		final String captchaStatus = result.getCaptchaStatus();
		switch (captchaStatus) {
			case "PASS": {
				return oauthClient.getToken(tokenCaptchaObj);
			}
			default:  
				throw HttpClientErrorException.create(UNAUTHORIZED, "Your input catcha value: "+inputCaptcha, null, null, null) ;
		}
	} 



 

}