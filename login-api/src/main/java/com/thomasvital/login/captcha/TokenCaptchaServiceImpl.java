package com.thomasvital.login.captcha;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List; 
import java.util.UUID;
 
import javax.imageio.ImageIO;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thomasvital.login.config.BusinessProperties;
 

/***
 * 將表格token_captcha廢除的過度版本
 * 
 * **/
@Component
@RequiredArgsConstructor 
public class TokenCaptchaServiceImpl implements TokenCaptchaService {
 
     
    private final  UserRecordVerificationsEntityRepo userRecordVerificationsEntityRepo;
     
    private final   BusinessProperties  properties;
         
    private RandomDataGenerator random = new RandomDataGenerator();
    


    public void deleteBefore1Month() { 
    	
    };
    protected int getTimeoutParams() {
    	return -(properties.getTimeout());
    }
    /**captcha 驗證  */
    public TokenCaptchaObj captchaVerify(final TokenCaptchaObj tokenCaptchaObj) throws ServletException, IOException {
    	
    	final String token =  tokenCaptchaObj.getToken() ;

        /** 取得使用者輸入之captcha碼  */
        final String inputCaptchaCode = tokenCaptchaObj.getInputCaptcha();

        // 找尋DB內 token相符 且 未超過時間的 token，與captcha 組合
        /**先驗證token是否效期內  */
        if(verifyToken(token)){
            /**token 效期內，找出其對應的captcha  */
            List<String> tokenCaptchaEntityList = userRecordVerificationsEntityRepo.findCaptchaAndNonExpiredTokenTop1(token,getTimeoutParams());
            
			if (tokenCaptchaEntityList.size() > 0
					&& tokenCaptchaEntityList.get(0).equals(inputCaptchaCode)) {
				
                /**與既存的captcha code符合  */
                tokenCaptchaObj.setValidate("true");
                tokenCaptchaObj.setCaptchaStatus(CaptchaStatus.PASS.toString());
            }else{
                /**與既存的captcha code 不符合  */
                tokenCaptchaObj.setValidate("false");
                tokenCaptchaObj.setCaptchaStatus(CaptchaStatus.FAIL.toString());
            }

        /**token本身已過期，重新生一組token回傳，本次captcha驗證失敗  */
        }else{
        	final String newToken = getToken(token).getToken();
            tokenCaptchaObj.setToken(newToken);
            tokenCaptchaObj.setValidate("false");
            tokenCaptchaObj.setCaptchaStatus(CaptchaStatus.FAIL.toString());
        }

        /** new 一個暫時obj作為中介轉運新生成captcha img 與 captcha code  */
        TokenCaptchaObj tempTokenCaptchaObj = new TokenCaptchaObj();
        tempTokenCaptchaObj = getCaptchaImgString(token);

        /**不管怎樣使用這個endpoint必定回傳一個新的captcha圖像  */
        tokenCaptchaObj.setCaptchaImg(tempTokenCaptchaObj.getCaptchaImg());
        tokenCaptchaObj.setCaptcha(tempTokenCaptchaObj.getCaptcha());

        return  tokenCaptchaObj;

    }

    /** 換一組全新 captcha 圖像與captcha碼, 但token於前一步已驗證過，故這裡不進行效期驗證   */
        public TokenCaptchaObj getCaptchaImgString(String verified_token) throws ServletException, IOException {
        	final TokenCaptchaObj outputTokenCaptchaObj = new TokenCaptchaObj();
        	
            int iTotalChars = 6; 	// captcha 數字長度

            // 產生新的 captcha code
            final String captchaCode = generateCaptchaCode(iTotalChars);

            //用提供的token 進行生產作業，因為前一步已做過token驗證，這邊不做任何token效期驗證
            
            List<UserRecordVerificationsEntity> list = userRecordVerificationsEntityRepo.findBySessionId(verified_token);
            for(UserRecordVerificationsEntity unit : list) {
            	unit.setCaptcha(captchaCode);
            	unit.setUpdatedAt(new Date());            	
            	userRecordVerificationsEntityRepo.save(unit);
            }
            

            int iHeight = 40; 		// 圖像高度(像素)
            int iWidth = 150;		// 圖像寬度(像素)
            Font fntStyle1 = new Font("Arial", Font.BOLD, 30);	// 圖像字體

            BufferedImage bufferedImage = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2DImage = (Graphics2D) bufferedImage.getGraphics();

            int iCircle = 15;
            for (int i = 0; i < iCircle; i++) {
                graphics2DImage.setColor(new Color(random.nextSecureInt(0,255), random.nextSecureInt(0,255), random.nextSecureInt(0,255)));
            }
            graphics2DImage.setFont(fntStyle1);
            for (int i = 0; i < iTotalChars; i++) {
                graphics2DImage.setColor(new Color(random.nextSecureInt(0,255), random.nextSecureInt(0,255), random.nextSecureInt(0,255)));
                if (i % 2 == 0) {
                    graphics2DImage.drawString(captchaCode.substring(i, i + 1), 25 * i, 24);
                } else {
                    graphics2DImage.drawString(captchaCode.substring(i, i + 1), 25 * i, 35);
                }
            }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpeg", out);
        byte[] bytes = out.toByteArray();
        String src = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        graphics2DImage.dispose();

            outputTokenCaptchaObj.setCaptchaImg(src);
            outputTokenCaptchaObj.setCaptcha(captchaCode);
        return outputTokenCaptchaObj;
    }

    /** captcha 數字長度, 預設6
     *  6/4  密碼改為6位數字
     *   */
    private String generateCaptchaCode(int iTotalChars) {
    
    	return String.format("%06d", random.nextSecureInt(0,999999));
//        return (Long.toString(Math.abs(randomChars.nextLong()), 36)).substring(0, iTotalChars);
    	
    }


    @Override
    public boolean verifyToken(String token) {
        Integer nonExpiredTokenCaptchaEntityList = userRecordVerificationsEntityRepo.findNonExpiredToken(token,getTimeoutParams());
        if(nonExpiredTokenCaptchaEntityList ==0){
            // token效期超過，不延長token 時間，不發給新的token。
            return false;
        }else{
            // token未過效期，驗證後(4/6改)發回不延長30分
//            updateTokenCaptchaEntity(nonExpiredTokenCaptchaEntityList.get(0));
            return true;
        }
    }

    /**請求一組token, 並輸入已有(或空字串)token  */
    @Override
    public TokenCaptchaObj getToken(String inputToken) {        
    	final  TokenCaptchaObj result = generateNewTokenCaptchaObj();
    	final String token = result.getToken() ;
        int params = getTimeoutParams();
        
        /** 當token輸入為null 或 '', 直接回傳一組全新token，
         * 但仍會驗證產生的token是否已產生過  */
        if(inputToken==null || inputToken.isEmpty()){
        	//需要存入DB
        	
        	 saveToken(token);
            /*產生一組新token同時進行重複與否的檢查*/            
            return result;

         /**請求附帶token時  **/
        }else {
            /**若為最高權限token**/
 

            /** 未過期token: query含有此token 且仍在時間範圍內未過期之資料筆數，輸入時間單位為分鐘，-為過去**/
            Integer nonExpiredTokenCaptchaEntityList = userRecordVerificationsEntityRepo.findNonExpiredToken(inputToken,params);
            
            /** 全部token: 找出所有含有此token之資料筆數**/
            Integer allCountWhereTokenIs = userRecordVerificationsEntityRepo.countDistinctSessionIdBySessionId(inputToken);


            /** 若 "未過期token" ==0 且 "全部token" ==0, 代表此輸入的token並不在DB內，回傳全新token給他  */
            if(nonExpiredTokenCaptchaEntityList ==0 && allCountWhereTokenIs ==0){
            	//需要存入DB 
            	saveToken(token);
                return result;

            }
            /** 若 "未過期token" > 0 代表這token在DB內且未過期，直接回傳 傳入之既有token, (4/6修正)不延長token時效  */
            if (nonExpiredTokenCaptchaEntityList > 0){
            
               final  String sessionId = 	userRecordVerificationsEntityRepo.findsessionIdNonExpiredTokenTop1(inputToken,params).get((0));
               
                /*單筆token 過期就過期，過期重驗就是重給token, 不需要對既有的延長時效*/  
                result.setToken(sessionId);
                result.setTokenStatus(TokenStatus.HEALTHY.toString());
                return result;

            }

            /** "未過期token" ==0 且 "全部token" > 0, 代表這token在DB內但是已過期，回傳全新一筆token給他  */
            if(allCountWhereTokenIs > 0){
            	//需要存入DB 
            	saveToken(token);
                return  result;
            }
            
        }
        return result;
    }
 
    /**
     * 驗證輸入token是否存在於 Token_Captcha table
     * **/
    @Override
    public boolean isTokenExistInTokenCaptcha(String token){
        int tokenCount =  userRecordVerificationsEntityRepo.countDistinctSessionIdBySessionId(token);
        if (tokenCount ==0 ){
            return false;
        }
        return true;
    }

    
 
    /**
     * 產生一組token, 並確認是否以產生過
     * 確認無重複即存入table**/
    protected TokenCaptchaObj generateNewTokenCaptchaObj(){
    	 final TokenCaptchaObj outputTokenCaptchaObj = new TokenCaptchaObj();
    	 
        outputTokenCaptchaObj.setToken( UUID.randomUUID().toString()); 
        outputTokenCaptchaObj.setTokenStatus(TokenStatus.HEALTHY.toString());
        return outputTokenCaptchaObj;
    } 
    public void saveToken(final String token) {
    	UserRecordVerificationsEntity entity = new UserRecordVerificationsEntity();
		entity.setPhoneOrEmail("");			 
		entity.setCreatedAt(new Date());
		entity.setCount(0);
		entity.setUpdatedAt(new Date());
		entity.setVerifyCode(null);
		entity.setSessionId(token);
		entity.setExpired(0);
		userRecordVerificationsEntityRepo.save(entity);
    }

}
