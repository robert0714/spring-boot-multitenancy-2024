package com.thomasvitale.multitenant.config;
 
import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import com.thomasvitale.multitenant.app.instrumentservice.Instrument;
import com.thomasvitale.multitenant.app.instrumentservice.InstrumentRepository;
import com.thomasvitale.multitenant.context.TenantContextHolder;
  
@Configuration(proxyBeanMethods = false)
public class InstrumentTestDataConfig {

	private final InstrumentRepository instrumentRepository;

	public InstrumentTestDataConfig(InstrumentRepository instrumentRepository) {
		this.instrumentRepository = instrumentRepository;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void loadTestData() {
		TenantContextHolder.setTenantIdentifier("dukes");
		if (instrumentRepository.count() == 0) {			
			var piano = Instrument.builder()
		                    .name("Steinway")
		                    .type("piano") 
					      .build();
			var cello = Instrument.builder()
				            .name("Cello")
                            .type("string") 
					      .build();  
			var guitar = Instrument.builder()
		                    .name("Gibson Firebird")
                            .type("guitar") 
			              .build();  
			instrumentRepository.saveAll(List.of(piano, cello, guitar));
		}
		TenantContextHolder.clear();

		TenantContextHolder.setTenantIdentifier("beans");
		if (instrumentRepository.count() == 0) { 			  
			var organ = Instrument.builder()
                    .name("Hammond B3")
                    .type("organ") 
			      .build();
	        var viola = Instrument.builder()
		            .name("Viola")
                    .type("string") 
			      .build();  
	        var guitarFake = Instrument.builder()
                    .name("Gibson Firebird (Fake)")
                    .type("guitar") 
	              .build(); 
			instrumentRepository.saveAll(List.of(organ, viola, guitarFake));
		}
		TenantContextHolder.clear();
	}

}