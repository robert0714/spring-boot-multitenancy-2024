package com.thomasvital.login.captcha;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
 

@Repository
public interface UserRecordVerificationsEntityRepo extends JpaRepository<UserRecordVerificationsEntity, Integer> {
 	public List<UserRecordVerificationsEntity> findBySessionId(String token);
	
	@Query(value = " SELECT * FROM user_record_verifications WHERE user_record_verifications.session_id = ?1  AND count=0  and phone_or_email='' ", nativeQuery = true)
	public List<UserRecordVerificationsEntity> findBySessionIdAndCountIs0(String token);

	@Query(value = "SELECT  top 10  " + 
			"  *  \r\n" + 
			"  FROM    user_record_verifications  \r\n"			
			+ " where user_record_verifications.phone_or_email like ?1%  \r\n" 
			+ " and updated_at >  ?2  \r\n" 
			+ " and updated_at <  ?3  \r\n" 
			+ "  order by updated_at  \r\n"
			, nativeQuery = true)
	public List<UserRecordVerificationsEntity> findByPhoneOrEmailLike(String data,Date start,Date end);

	@Query( value ="  SELECT * FROM user_record_verifications \r\n" 
				 + "  WHERE user_record_verifications.phone_or_email = ?1   \r\n" 
				 + "  AND DATEPART(month, created_at) = MONTH(CURRENT_TIMESTAMP()) \r\n" 
				 + "  AND DATEPART(year, created_at) = YEAR(CURRENT_TIMESTAMP()) ORDER BY created_at desc", nativeQuery = true)
	public List<UserRecordVerificationsEntity> findByPhoneOrEmail(String data);
	
	
	public Integer countDistinctSessionIdBySessionId(String sessionId);
	
	
	public Integer countDistinctSessionIdBySessionIdAndVerifyCode(final String sessionId,final String verifyCode);
	
	public List<UserRecordVerificationsEntity> findByVerifyCode(final String verifyCode);

	
    /* 找出token發布 30分鐘以內的項目
    *  TODO:3/30 30分鐘限制改用程式帶參數進入，並改成property  */
    @Query(value = " SELECT count(*) FROM user_record_verifications WHERE expired=0 AND session_id= ?1 AND updated_at > DATEADD(MINUTE, ?2 , CURRENT_TIMESTAMP()) ", nativeQuery = true)
    Integer findNonExpiredToken(String token, int timePeriod);
    
    /* 找出token發布 30分鐘以內的項目
    *  TODO:3/30 30分鐘限制改用程式帶參數進入，並改成property  */
    @Query(value = " SELECT top 1 *  FROM user_record_verifications WHERE expired=0 AND session_id= ?1 AND updated_at > DATEADD(MINUTE, ?2 , CURRENT_TIMESTAMP()) ", nativeQuery = true)
    @Deprecated
    List<UserRecordVerificationsEntity> findNonExpiredTokenTop1(String token, int timePeriod);
    
    
    @Query(value = " SELECT top 1 captcha  FROM user_record_verifications WHERE expired=0 AND session_id= ?1 AND updated_at > DATEADD(MINUTE, ?2 , CURRENT_TIMESTAMP()) ", nativeQuery = true)
    List<String> findCaptchaAndNonExpiredTokenTop1(String token, int timePeriod);
    
    @Query(value = " SELECT top 1 session_id  FROM user_record_verifications WHERE expired=0 AND session_id= ?1 AND updated_at > DATEADD(MINUTE, ?2 , CURRENT_TIMESTAMP()) ", nativeQuery = true) 
    List<String>  findsessionIdNonExpiredTokenTop1(String token, int timePeriod);
}
