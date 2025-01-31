package com.thomasvital.login.config;

 
import static org.springdoc.core.utils.Constants.ALL_PATTERN;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.net.ssl.SSLContext;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
 
@Configuration
public class CommonConfig {
 
//	@Value("${trust.store}")
//    private Resource trustStore;
//
//    @Value("${trust.store.password}")
//    private String trustStorePassword;

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
      CertificateException, MalformedURLException, IOException {
      
//        SSLContext sslContext = new SSLContextBuilder()
//          .loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray()).build();
//        SSLConnectionSocketFactory sslConFactory = new SSLConnectionSocketFactory(sslContext);
//
//        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslConFactory).build();
//        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
//        return new RestTemplate(requestFactory);
        return new RestTemplate();
    }
    @Bean
	 CorsConfigurationSource corsConfigurationSource() {
	 	CorsConfiguration configuration = new CorsConfiguration();
	 	configuration.setAllowedOrigins(
	 			Arrays.asList("http://localhost:8082", "http://localhost:8080", "http://localhost:80", "http://localhost:300"));
	 	configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTION"));
	 	configuration.setAllowedHeaders(Arrays.asList("X-idp", "Authorization", "Content-Type"));
	 	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	 	source.registerCorsConfiguration("/**", configuration);
	 	return source;
	 }
//    @Bean
//	@Profile("!prod")
//	public GroupedOpenApi actuatorApi(OpenApiCustomizer actuatorOpenApiCustomiser,
//			OperationCustomizer actuatorCustomizer, WebEndpointProperties endpointProperties,
//			@Value("${springdoc.version}") String appVersion) {
//		return GroupedOpenApi.builder().group("Actuator").pathsToMatch(endpointProperties.getBasePath() + ALL_PATTERN)
//				.addOpenApiCustomizer(actuatorOpenApiCustomiser)
//				.addOpenApiCustomizer(openApi -> openApi.info(new Info().title("Actuator API").version(appVersion)))
//				.addOperationCustomizer(actuatorCustomizer).pathsToExclude("/health/*").build();
//	}

	@Bean
	public GroupedOpenApi usersGroup(@Value("${springdoc.version}") String appVersion) {
		return GroupedOpenApi.builder().group("o-carbon").addOperationCustomizer((operation, handlerMethod) -> {
			operation.addSecurityItem(new SecurityRequirement().addList("basicScheme"));
			return operation;
		}).addOpenApiCustomizer(openApi -> openApi.info(new Info().title("o-carbon login API").version(appVersion)))
				.packagesToScan("com.iisigroup.login.captcha","com.iisigroup.login.aggregate").build();
	}
}
