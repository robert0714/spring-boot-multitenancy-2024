package com.thomasvital.login.captcha;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException; 

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.thomasvital.login.captcha.CaptchaStatus;
import com.thomasvital.login.captcha.TokenCaptchaObj;
import com.thomasvital.login.captcha.TokenCaptchaServiceImpl;

import jakarta.servlet.ServletException;



@SpringBootTest
public class TokenCaptchaServiceImplTest {

	@BeforeEach
	protected void setUp() throws Exception {
	}

	@Autowired
	private TokenCaptchaServiceImpl  tokenCaptchaServiceImpl;
 

	@Test
	public void testGenerateNewTokenCaptchaObj() throws Exception {
		TokenCaptchaObj tokenObject = tokenCaptchaServiceImpl.generateNewTokenCaptchaObj();
		assertNotNull(tokenObject);
	}

	@Test
	public void verifyToken1_illegalToken() {
		// 不合法token
		String illegalToken = "1234";
		assertFalse(tokenCaptchaServiceImpl.verifyToken(illegalToken));
	}

	@Test
	public void verifyToken2_invalidToken() {
		// 合法但過期token
		String legalButInvalidToken = "6fc99568-b222-4457-834c-7b95d6c13786";
		assertFalse(tokenCaptchaServiceImpl.verifyToken(legalButInvalidToken));
	}

	@Test
	public void verifyToken3_validToken() throws ServletException, IOException {
		// 合法 且 未過期token 
		TokenCaptchaObj tempTokenCaptchaObj = tokenCaptchaServiceImpl.getToken(null);
		String token  = tempTokenCaptchaObj.getToken();
		assertTrue(tokenCaptchaServiceImpl.verifyToken(token));
	}

	@Test
	public void generateToken() {
		String originToken = "c39deca0-039c-49be-b1d4-d58324f2f6fb";
		String token = tokenCaptchaServiceImpl.getToken(originToken).getToken();
		assertNotNull(token);
		assertNotEquals(token, originToken);
	}

	@Test
	public void testGetTimeoutParams() throws Exception {
		int timeoutParams = tokenCaptchaServiceImpl.getTimeoutParams();
		assertEquals(-30, timeoutParams);
	}

	@Test
	public void testDeleteBefore1Month() throws Exception {
		tokenCaptchaServiceImpl.deleteBefore1Month();
		;
	}
	@Test
	public void verifyToken3() throws Exception {
		final TokenCaptchaObj tokenCaptchaObj = createNewTokenCaptchaObjV1();
		if (!tokenCaptchaServiceImpl.verifyToken(tokenCaptchaObj.getToken())) {
			tokenCaptchaObj.setToken(tokenCaptchaServiceImpl.getToken(tokenCaptchaObj.getToken()).getToken());
		}
		TokenCaptchaObj tempTokenCaptchaObj = tokenCaptchaServiceImpl.getCaptchaImgString(tokenCaptchaObj.getToken());
		tokenCaptchaObj.setCaptchaImg(tempTokenCaptchaObj.getCaptchaImg());
		tokenCaptchaObj.setCaptcha(tempTokenCaptchaObj.getCaptcha());
		tokenCaptchaObj.setCaptchaStatus(CaptchaStatus.FAIL.toString());
	 
		tokenCaptchaObj.setInputCaptcha(tokenCaptchaObj.getCaptcha());
		TokenCaptchaObj returnObj = tokenCaptchaServiceImpl.captchaVerify(tokenCaptchaObj);
		assertEquals("true", returnObj.getValidate()); 
		assertEquals("PASS", returnObj.getCaptchaStatus());
		assertNotNull(  returnObj.getCaptcha());
	}
	protected  TokenCaptchaObj createNewTokenCaptchaObjV1() {
		final TokenCaptchaObj result =new TokenCaptchaObj();
		result.setToken("fdf4f301-f2ec-48c2-82b7-54c6d922428f");
		return result;
	}

}
