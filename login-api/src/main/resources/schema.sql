drop table if exists user_record_verifications; 
CREATE TABLE user_record_verifications (
  ID int not null AUTO_INCREMENT,
  session_id varchar(191) not null,
  phone_or_email varchar(191) not null, 
  captcha  varchar(191)  , 
  VERIFY_CODE  varchar(191)  , 
  created_at DATE,
  updated_at DATE,
  COUNT  int NOT NULL ,
  EXPIRED  int NOT NULL ,
  PRIMARY KEY (ID)
); 