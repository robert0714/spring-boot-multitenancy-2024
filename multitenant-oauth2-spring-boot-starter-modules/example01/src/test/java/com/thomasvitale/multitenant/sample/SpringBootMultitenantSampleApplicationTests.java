package com.thomasvitale.multitenant.sample;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest; 
import org.springframework.context.annotation.Import;
 
 

@SpringBootTest
@Import(TestInstrumentServiceApplication.class) 
class SpringBootMultitenantSampleApplicationTests {

	@Test
	void contextLoads() {
	}

}
