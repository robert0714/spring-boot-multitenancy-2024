package com.thomasvitale.multitenant;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach; 
import org.junit.jupiter.api.Test; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.web.client.TestRestTemplate; 
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity; 
import org.springframework.test.context.TestPropertySource;

import com.thomasvitale.multitenant.app.advanced.InstrumentServiceApplication;
import com.thomasvitale.multitenant.app.advanced.TestPostgresApplication;
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

import static org.junit.jupiter.api.Assertions.assertEquals; 

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = InstrumentServiceApplication.class,
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
		          "spring.sql.init.continue-on-error=false"})
@TestPropertySource(locations="classpath:instrumentsTest.properties")
@Import(value= {
		TestPostgresApplication.class,
		InstrumentTestConfig.class,
		SecurityConfig.class ,
		InstrumentTestDataConfig.class})
public class InstrumentControllerTest02 extends AbstractInstrumentControllerTests{
	  
    @Autowired
	private TestRestTemplate testRestTemplate;
        
    
    @Autowired
   	private   MultiTenantResourceServerProperties props ;
	 
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

	@AfterEach
	protected void tearDown() throws Exception {
	}

	@Test
	public void testGetInstrumentsWenTenantIsDukes() throws Exception {		 
		ResponseEntity<String> response = sendRequest(JWT_TENANT_1 ,"dukes") ; 
		assertEquals( HttpStatus.OK,response.getStatusCode() );
		assertThat(response.getBody()).contains("Steinway");
		assertThat(response.getBody()).contains("piano");
	}
	@Test
	public void testGetInstrumentsWenTenantIsBeans() throws Exception {		 
		ResponseEntity<String> response = sendRequest(JWT_TENANT_2 ,"beans") ; 
		assertEquals( HttpStatus.OK,response.getStatusCode() );
		assertThat(response.getBody()).contains("Hammond B3");
		assertThat(response.getBody()).contains("Viola");
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
			 ResponseEntity<String> response = sendRequestAddInstrument(token ,tenantId ,instrumentJson) ; 
			 assertThat(response.getBody()).contains(instrumentUnique); 
		 }         
	}
   

	private ResponseEntity<String>  sendRequest(String token,String tenantId) throws Exception {
		String uri = "/instruments";
		HttpHeaders headers =  new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if(StringUtils.isNotEmpty(token)) {
		   headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		}
		if(StringUtils.isNotEmpty(tenantId)) {
		   String headerName = props.getHeader().getHeaderName() ;
		   headers.set(headerName,   tenantId);
		}
		 
		ResponseEntity<String> responseEntity = testRestTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
		return responseEntity;
	} 
	private  ResponseEntity<String>  sendRequestAddInstrument(String token,String tenantId,String instrumentJson) throws Exception {
		String uri = "/instruments";
		HttpHeaders headers =  new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if(StringUtils.isNotEmpty(token)) {
		   headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		}
		if(StringUtils.isNotEmpty(tenantId)) {
		   String headerName = props.getHeader().getHeaderName() ;
		   headers.set(headerName,   tenantId);
		}
		 
		ResponseEntity<String> responseEntity = testRestTemplate.exchange(uri, HttpMethod.POST,
				new HttpEntity<>(instrumentJson, headers), String.class);
		return responseEntity;
	}
	
}

