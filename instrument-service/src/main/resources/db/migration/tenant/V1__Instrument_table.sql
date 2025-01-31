CREATE TABLE instrument(
   id      UUID PRIMARY KEY,
   name    VARCHAR(255) NOT NULL,
   type    VARCHAR(255),
   created_at timestamptz  NOT NULL,
   updated_at timestamptz  NOT NULL,
   created_by varchar(255)  NOT NULL,
   updated_by varchar(255)  NOT NULL
);
create table request_records (
	id varchar(36) PRIMARY KEY,
	status integer not null, 
	cost_time bigint not null, 
	request_time timestamp(6) with time zone not null, 	
	api_name varchar(255), 
	error_msg varchar(255), 
	realmname varchar(255) not null, 
	request_method varchar(255) not null, 
	request_param varchar(255), 
	request_url varchar(255) not null, 
	response_body TEXT, 
	user_ip varchar(255) not null, 
	user_name varchar(255) not null )