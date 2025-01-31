package com.thomasvitale.multitenant.app.advanced;
 
 
import org.springframework.boot.SpringApplication; 
//import org.springframework.boot.devtools.restart.RestartScope; 
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean; 
import org.testcontainers.containers.PostgreSQLContainer;
  

@TestConfiguration(proxyBeanMethods = false)
public class TestPostgresApplication {

    

    public static void main(String[] args) {
		SpringApplication.from(InstrumentServiceApplication::main)
	  		.with(TestPostgresApplication.class)
	  		.run(args);
	}
    @Bean
//    @RestartScope
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
    	 return new PostgreSQLContainer<>("postgres:15.8")
    			 .withEnv("TZ", "Asia/Taipei")
//  			 .withPassword("") 
  			 ;
    } 
	
		 
}
