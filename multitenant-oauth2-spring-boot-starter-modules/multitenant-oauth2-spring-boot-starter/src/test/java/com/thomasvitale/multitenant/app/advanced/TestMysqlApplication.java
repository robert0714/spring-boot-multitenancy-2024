package com.thomasvitale.multitenant.app.advanced;
 
 
import org.springframework.boot.SpringApplication;  
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean; 
import org.testcontainers.containers.MySQLContainer; 
  

@TestConfiguration
public class TestMysqlApplication {

    

    public static void main(String[] args) {
		SpringApplication.from(InstrumentServiceApplication::main)
	  		.with(TestMysqlApplication.class)
	  		.run(args);
	}
    @Bean
//  @RestartScope
  @ServiceConnection
  public MySQLContainer<?> mySQLContainer() {
  	 return new MySQLContainer<>("mysql:8.0.37")
  			 .withUsername("root")
  			 .withEnv("EXTRA_OPTS", "\"--lower_case_table_names=1\"")
  			 .withEnv("TZ", "Asia/Taipei")
//  			 .withPassword("") 
  			 ;
  } 
		 
}
