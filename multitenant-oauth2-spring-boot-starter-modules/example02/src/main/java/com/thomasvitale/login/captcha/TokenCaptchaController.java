package com.thomasvitale.login.captcha;
import java.io.IOException;

import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
 
import org.springframework.http.MediaType; 
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RestController;

import com.thomasvitale.login.config.BusinessProperties;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation; 

@Tag(name ="captcha api",description = "產生 captcha與確認captcha")
@RequiredArgsConstructor 
@RestController
public class TokenCaptchaController {
	
	private final  TokenCaptchaService tokenCaptchaService;
	 
    private final  BusinessProperties  properties;

	@Operation(description ="確認 captcha")
	@PostMapping(value="/captcha/verify" ,produces = MediaType.APPLICATION_JSON_VALUE) 
	public TokenCaptchaObj verifyCaptcha(@RequestBody TokenCaptchaObj tokenCaptchaObj)throws ServletException, IOException {
		return tokenCaptchaService.captchaVerify(tokenCaptchaObj);
	}
 
	@Operation(description ="產生 captcha")
	@PostMapping(value="/captcha" ,produces = MediaType.APPLICATION_JSON_VALUE) 
	public TokenCaptchaObj getCaptcha(@RequestBody TokenCaptchaObj tokenCaptchaObj) throws ServletException, IOException {

		// 預設對token進行效期測試，但即便過效期還是要生一個新的token回去
		// 3/27 新增，開發階段使用，回傳captcha附上正確的碼
		if(!tokenCaptchaService.verifyToken(tokenCaptchaObj.getToken())){
			tokenCaptchaObj.setToken(tokenCaptchaService.getToken(tokenCaptchaObj.getToken()).getToken());
		}
		TokenCaptchaObj tempTokenCaptchaObj = tokenCaptchaService.getCaptchaImgString(tokenCaptchaObj.getToken());
		tokenCaptchaObj.setCaptchaImg(tempTokenCaptchaObj.getCaptchaImg());
		if(properties.isShowCaptchaAndCode()) {
			tokenCaptchaObj.setCaptcha(tempTokenCaptchaObj.getCaptcha());
		}
		tokenCaptchaObj.setCaptchaStatus(CaptchaStatus.FAIL.toString());
		return tokenCaptchaObj;
	}


 

}