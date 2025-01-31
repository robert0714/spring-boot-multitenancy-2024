package com.thomasvitale.multitenant;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach; 
import org.junit.jupiter.api.Test;  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.mock.web.MockHttpServletRequest; 
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;  
import org.springframework.test.context.TestPropertySource; 
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.thomasvitale.multitenant.app.advanced.InstrumentServiceApplication;
import com.thomasvitale.multitenant.app.advanced.TestMysqlApplication;
import com.thomasvitale.multitenant.app.instrumentservice.Instrument;
import com.thomasvitale.multitenant.config.InstrumentTestConfig;
import com.thomasvitale.multitenant.config.InstrumentTestDataConfig;
import com.thomasvitale.multitenant.config.MultiTenantResourceServerAutoConfiguration;
import com.thomasvitale.multitenant.config.MultiTenantResourceServerHeaderConfiguration;
import com.thomasvitale.multitenant.config.MultiTenantResourceServerProperties;
import com.thomasvitale.multitenant.config.MultiTenantResourceServerWebMvcConfiguration;
import com.thomasvitale.multitenant.config.SecurityConfig;
import com.thomasvitale.multitenant.data.hibernate.ConnectionProvider;
import com.thomasvitale.multitenant.data.hibernate.TenantIdentifierResolver;
import com.thomasvitale.multitenant.tenantdetails.TenantDetailsProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
 
@AutoConfigureMockMvc 
@SpringBootTest( classes = InstrumentServiceApplication.class,
          webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, 
          properties = { 
        		  "spring.datasource.hikari.auto-commit=true",
        		  "spring.datasource.type=com.zaxxer.hikari.HikariDataSource",
        		  "hibernate.connection.provider_disables_autocommit=false",
		          "spring.security.oauth2.resourceserver.multitenant.enabled=true",
		          "spring.websecurity.debug=true",
		          "logging.level.org.springframework.security=false",
		          "spring.jpa.show-sql=true",
		          "spring.jpa.properties.hibernate.format_sql=true",
		          "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect",
		          "spring.jpa.properties.hibernate.dialect.storage_engine=innodb",
		          "spring.sql.init.continue-on-error=false"})
@TestPropertySource(locations="classpath:instrumentsTestForMysql.properties")
@Import(value= {
		TestMysqlApplication.class,
		InstrumentTestConfig.class,
		SecurityConfig.class ,
		InstrumentTestDataConfig.class})
public class InstrumentControllerTest03 extends AbstractInstrumentControllerTests{
	 
    @Autowired
    private MockMvc mvc;  
    
	@AfterEach
	protected void tearDown() throws Exception {
	}

	// https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(MultiTenantResourceServerAutoConfiguration.class));

	@Test
	void defaultWebMvc() {
		this.contextRunner.withUserConfiguration(
				MultiTenantResourceServerWebMvcConfiguration.class,
				MultiTenantResourceServerProperties.class)
		.run((context) -> {
					assertThat(context).hasSingleBean(MultiTenantResourceServerWebMvcConfiguration.class);
					assertThat(context).hasSingleBean(ConnectionProvider.class);
					assertThat(context).hasSingleBean(TenantIdentifierResolver.class);
				});
	}

	@Test
	void defaultHeader() {
		this.contextRunner.withUserConfiguration(
				MultiTenantResourceServerHeaderConfiguration.class,
				MultiTenantResourceServerProperties.class,
				TenantIdentifierResolver.class)
		.run((context) -> {
					assertThat(context).hasSingleBean(MultiTenantResourceServerHeaderConfiguration.class);
					assertThat(context).hasSingleBean(TenantIdentifierResolver.class);
					assertThat(context).hasSingleBean(TenantDetailsProperties.class);
				});
	}
	@Test  
	public void testGetInstrumentsWenTenantIsDukes() throws Exception { 		 
		sendRequest(JWT_TENANT_1 ,"dukes")
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Steinway")))
        .andExpect(content().string(containsString("piano")));
	}
	@Test
	public void testGetInstrumentsWenTenantIsBeans() throws Exception {
		sendRequest(JWT_TENANT_2 ,"beans")
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Hammond B3")))
        .andExpect(content().string(containsString("Viola")));
	}

	@Test 
	void unknownTenant() throws Exception { 
		sendRequest(JWT_UNKNOWN_TENANT,"").andExpect(status().isUnauthorized());
 
	}
	
	@Test
	void givenRequestIsAnonymous_whenGetGreet_thenUnauthorized() throws Exception {
		mvc.perform(get("/instruments").with(SecurityMockMvcRequestPostProcessors.anonymous()))
	      .andExpect(status().isUnauthorized());
	}
	

	@Test
	void noTenant() throws Exception {
		sendRequest("","").andExpect(status().isUnauthorized());
	}

	@Test
	void ignoredPath() throws Exception {
		mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
	}

	private ResultActions sendRequest(String token,String tenantId) throws Exception {
		return mvc.perform(get("/instruments").with(processHeaders(token,tenantId)));
	}

	private static BearerTokenRequestPostProcessor processHeaders(String token ,String tenantId) {
		return new BearerTokenRequestPostProcessor(token,tenantId);
	}

	private static class BearerTokenRequestPostProcessor implements RequestPostProcessor {

		private final String token;
		private final String tenantId;

		BearerTokenRequestPostProcessor(String token ,String tenantId) {
			this.token = token;
			this.tenantId = tenantId ; 
		}

		@Override
		@NonNull
		public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
			request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			if(StringUtils.isNotEmpty(tenantId)) {
				request.addHeader("x-tenant-id", tenantId);
			}			
			return request;
		}

	}
	private ResultActions sendRequestAddInstrument(String token,String tenantId,String instrumentJson) throws Exception {
		return mvc.perform( MockMvcRequestBuilders
			      .post("/instruments")
			      .content(instrumentJson)
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .with(processHeaders(token,tenantId))
			     );
	}
	@Test  
	public void testAddInstrument() throws Exception{
         String token = null;	
         String tenantId = null;
         
		 for (int i = 0; i < 10; ++i) {
			 
			 if((i % 2)==1) {
				 token = JWT_TENANT_1;
				 tenantId = "dukes";
			 }else {
				 token = JWT_TENANT_2;
				 tenantId = "beans";
			 }		 
			 
			 String instrumentUnique =  faker.unique().fetchFromYaml("music.instruments"); 
			 String instrumentJson = asJsonString( Instrument.builder()
	                    .name(instrumentUnique)
	                    .type("piano") 
				      .build());
			 
			 ResultActions resultActions = sendRequestAddInstrument(token, tenantId, instrumentJson).andExpect(status().isOk())
		      .andExpect(jsonPath("$.id").exists())  
		      .andExpect(content().string(containsString(instrumentUnique)));
			 
	         resultActions.andDo(print());
		 }         
	}
	
}

