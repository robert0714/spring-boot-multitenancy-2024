package com.thomasvitale.multitenant.app.advanced;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thomasvitale.multitenant.app.instrumentservice.Instrument;
import com.thomasvitale.multitenant.app.instrumentservice.InstrumentRepository;
import com.thomasvitale.multitenant.security.JWTInfoHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

public class InstrumentAbstractServiceApplication {
	@Tag(name = "InstrumentController", description = "instruments api endpoints")
	@RestController
	@RequestMapping("/instruments")
	public static class InstrumentController {

		private static final Logger log = LoggerFactory.getLogger(InstrumentController.class);
		private final InstrumentRepository instrumentRepository;
		private final JWTInfoHelper helper;

		public InstrumentController(InstrumentRepository instrumentRepository, JWTInfoHelper helper) {
			this.instrumentRepository = instrumentRepository;
			this.helper = helper;
		}

		@GetMapping
		@Operation(description = "List all instruments")
		List<Instrument> getInstruments() {
			log.info("Returning all instruments");
			log.info("Realm: {} ,User: {}", this.helper.showRealmName(), this.helper.showUserName());
			return instrumentRepository.findAll();
		}

		@Operation(description = "List all instruments by specific type")
		@GetMapping("{type}")
		List<Instrument> getInstrumentByType(@PathVariable String type) {
			log.info("Returning instrument of type: {}", type);
			log.info("Realm: {} ,User: {}", this.helper.showRealmName(), this.helper.showUserName());
			return instrumentRepository.findByType(type);
		}

		@Operation(description = "Create/Update instrument")
		@ApiResponses(value = {
				@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Instrument.class))) })
		@PostMapping
		Instrument addInstrument(@RequestBody
//				@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Instrument.class)))
		Instrument instrument) {
			log.info("Adding instrument: {}", instrument.getName());
			log.info("Realm: {} ,User: {}", this.helper.showRealmName(), this.helper.showUserName());
			return instrumentRepository.save(instrument);
		}

	}
}
