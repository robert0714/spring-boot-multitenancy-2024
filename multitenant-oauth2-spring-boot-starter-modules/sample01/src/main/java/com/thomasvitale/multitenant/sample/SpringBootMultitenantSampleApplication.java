package com.thomasvitale.multitenant.sample;

import org.springframework.boot.SpringApplication; 
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.thomasvitale.multitenant.sample.config.SecurityConfig; 
@SpringBootApplication 
public class SpringBootMultitenantSampleApplication {
	private static ApplicationContext applicationContext;

	public static void main(String[] args) {
		applicationContext = SpringApplication.run(SpringBootMultitenantSampleApplication.class, args);
		checkBeansPresence(
		          "securityFilterChain", "corsConfigurationSource", "CorsFilter", "tenantAuthenticationManagerResolver", "jwtInfoHelper");

	}
	
	private static void checkBeansPresence(String... beans) {
        for (String beanName : beans) {
            System.out.println("Is " + beanName + " in ApplicationContext: " + 
              applicationContext.containsBean(beanName));
        }
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            System.out.println(  beanName ); 
               
        }
        
    }

}
