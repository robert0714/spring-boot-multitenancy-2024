package com.thomasvitale.login.captcha; 
 

import jakarta.servlet.ServletException;
 

import java.io.IOException;
 
public interface TokenCaptchaService {

    public TokenCaptchaObj captchaVerify(TokenCaptchaObj tokenCaptchaObj) throws ServletException, IOException;

    public TokenCaptchaObj getCaptchaImgString(String token) throws ServletException, IOException;

    public boolean verifyToken(String token);

    public TokenCaptchaObj getToken(String token);

    public boolean isTokenExistInTokenCaptcha(String token);
    
    public void deleteBefore1Month();
    
     

}
