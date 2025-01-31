package com.thomasvitale.login.captcha;
 

import jakarta.persistence.*;
import lombok.Data; 

import java.util.Date;
 
 
@Data
@Entity
@Table(name = "user_record_verifications"   )
public class UserRecordVerificationsEntity {
 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
    private Integer id;
    @Basic
	@Column(name = "session_id", nullable = false, length = 191)
    private String sessionId;
    
    @Basic
 	@Column(name = "count" )
     private Integer count;
    
    @Basic
 	@Column(name = "verify_code" )
     private String verifyCode;
    
    @Basic
    @Column(name = "captcha",nullable = true)
    private String captcha;
    
    @Basic
    @Column(name = "expired")
    private Integer expired;
    
    @Basic
	@Column(name = "phone_or_email", nullable = false, length = 191)
    private String phoneOrEmail;
    @Basic
	@Column(name = "created_at")
    private Date createdAt;
    @Basic
	@Column(name = "updated_at")
    private Date updatedAt;
 
}
 