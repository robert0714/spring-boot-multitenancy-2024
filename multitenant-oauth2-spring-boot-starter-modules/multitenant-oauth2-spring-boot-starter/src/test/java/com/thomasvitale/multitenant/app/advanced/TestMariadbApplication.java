package com.thomasvitale.multitenant.app.advanced;
 
 
import org.springframework.boot.SpringApplication;  
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MariaDBContainer; 
  

@TestConfiguration
public class TestMariadbApplication {

    

    public static void main(String[] args) {
		SpringApplication.from(InstrumentServiceApplication::main)
	  		.with(TestMariadbApplication.class)
	  		.run(args);
	}

    @Bean
//  @RestartScope
  @ServiceConnection
  public MariaDBContainer<?> mySQLContainer() {
  	 return new MariaDBContainer<>("mariadb:lts-ubi9")
  			 .withUsername("root")
  			 .withEnv("EXTRA_OPTS", "\"--lower_case_table_names=1\"")
  			 .withEnv("TZ", "Asia/Taipei")
//  			 .withPassword("") 
  			 ;
  } 
	
		 
}
