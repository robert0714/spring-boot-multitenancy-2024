package com.thomasvitale.multitenant.app.advanced;
 
import org.springframework.boot.SpringApplication; 
import org.springframework.boot.autoconfigure.SpringBootApplication;   

@SpringBootApplication 
public class InstrumentServiceApplication extends InstrumentAbstractServiceApplication{

	public static void main(String[] args) {
		SpringApplication.run(InstrumentServiceApplication.class, args);
	}

	
}
