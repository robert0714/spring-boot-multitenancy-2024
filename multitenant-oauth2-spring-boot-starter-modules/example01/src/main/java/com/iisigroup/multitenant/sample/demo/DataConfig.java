package com.iisigroup.multitenant.sample.demo;

import java.time.OffsetDateTime; 
import java.util.List;
 

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import com.thomasvitale.multitenant.context.TenantContextHolder;
import com.iisigroup.multitenant.sample.instrumentservice.instrument.Instrument;
import com.iisigroup.multitenant.sample.instrumentservice.instrument.InstrumentRepository;

@Configuration 
public class DataConfig {

	private final InstrumentRepository instrumentRepository;

	public DataConfig(InstrumentRepository instrumentRepository) {
		this.instrumentRepository = instrumentRepository;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void loadTestData() {
		TenantContextHolder.setTenantIdentifier("dukes");
		if (instrumentRepository.count() == 0) {			
			var piano = Instrument.builder()
		                    .name("Steinway")
		                    .type("piano")
		                    .createdAt(OffsetDateTime.now())
		                    .updatedAt(OffsetDateTime.now())
		                    .createdBy("default")
					        .updatedBy("default")
					      .build();
			var cello = Instrument.builder()
				            .name("Cello")
                            .type("string")
		                    .createdAt(OffsetDateTime.now())
		                    .updatedAt(OffsetDateTime.now())
		                    .createdBy("default")
					        .updatedBy("default")
					      .build();  
			var guitar = Instrument.builder()
		                    .name("Gibson Firebird")
                            .type("guitar")
                            .createdAt(OffsetDateTime.now())
                            .updatedAt(OffsetDateTime.now())
                            .createdBy("default")
			                .updatedBy("default")
			              .build();  
			instrumentRepository.saveAll(List.of(piano, cello, guitar));
		}
		TenantContextHolder.clear();

		TenantContextHolder.setTenantIdentifier("beans");
		if (instrumentRepository.count() == 0) { 			  
			var organ = Instrument.builder()
                    .name("Hammond B3")
                    .type("organ")
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .createdBy("default")
			        .updatedBy("default")
			      .build();
	        var viola = Instrument.builder()
		            .name("Viola")
                    .type("string")
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .createdBy("default")
			        .updatedBy("default")
			      .build();  
	        var guitarFake = Instrument.builder()
                    .name("Gibson Firebird (Fake)")
                    .type("guitar")
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .createdBy("default")
	                .updatedBy("default")
	              .build(); 
			instrumentRepository.saveAll(List.of(organ, viola, guitarFake));
		}
		TenantContextHolder.clear();
	}

}
