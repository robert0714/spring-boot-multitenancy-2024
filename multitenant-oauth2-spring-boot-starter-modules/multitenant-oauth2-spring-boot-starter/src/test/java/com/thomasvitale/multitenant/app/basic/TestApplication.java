package com.thomasvitale.multitenant.app.basic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ComponentScan.Filter;

import com.thomasvitale.multitenant.app.advanced.InstrumentServiceApplication;
import com.thomasvitale.multitenant.config.InstrumentTestConfig;
import com.thomasvitale.multitenant.config.InstrumentTestDataConfig;
 

@SpringBootApplication
@ImportAutoConfiguration(exclude = {
	    DataSourceAutoConfiguration.class, 
	    HibernateJpaAutoConfiguration.class
	}) 
@ComponentScan(excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {InstrumentServiceApplication.InstrumentController.class,
		InstrumentServiceApplication.class, InstrumentTestDataConfig.class, InstrumentTestConfig.class,InstrumentTestConfig.class ,
		FlywayAutoConfiguration.class }))
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
