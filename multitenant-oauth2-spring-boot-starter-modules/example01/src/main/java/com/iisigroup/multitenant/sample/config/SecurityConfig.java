package com.iisigroup.multitenant.sample.config;

import jakarta.servlet.http.HttpServletRequest;
 

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManagerResolver; 
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;



import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;  
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; 

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;



import com.thomasvitale.multitenant.filter.TenantContextFilter;

@Configuration(proxyBeanMethods = false)
public class SecurityConfig {
    @Value("${spring.websecurity.debug:true}")
    boolean webSecurityDebug;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(webSecurityDebug);
    }
	@Bean
	public SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver,
			TenantContextFilter tenantContextFilter
	) throws Exception {
		return http
			.authorizeHttpRequests(request -> request
				.requestMatchers(
						antMatcher("/v3/api-docs/**"),
                        antMatcher("/swagger-ui/**"),
                        antMatcher("/swagger-ui.html"),
						antMatcher("/actuator/**")
						).permitAll()
				.anyRequest().authenticated())
			.oauth2ResourceServer(oauth2 -> oauth2.authenticationManagerResolver(authenticationManagerResolver))
			.addFilterBefore(tenantContextFilter, BearerTokenAuthenticationFilter.class)
			.csrf(AbstractHttpConfigurer::disable)
            .cors(configurationSource -> configurationSource
                    .configurationSource(corsConfigurationSource()))
			
			.build();
	}
    
//	@Bean
//	SecurityFilterChain securityFilterChain(HttpSecurity http  ) throws Exception { 
//		http
//        .sessionManagement(sessionManagement -> sessionManagement
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//        .authorizeHttpRequests(authorize -> authorize
//                .requestMatchers(
//                		antMatcher("/v3/api-docs/**"),
//                        antMatcher("/swagger-ui/**"),
//                        antMatcher("/swagger-ui.html"),
//						antMatcher("/actuator/**")
//                ).permitAll()
//                .anyRequest().authenticated())
//        .oauth2ResourceServer(oauth2 -> oauth2
//                .opaqueToken(Customizer.withDefaults()) 
//           ) 
//        .csrf(AbstractHttpConfigurer::disable)
//        .cors(configurationSource -> configurationSource
//                .configurationSource(corsConfigurationSource()));
//		 
//		return http.build();
//	} 
	
	
	 @Bean
	 public CorsConfigurationSource corsConfigurationSource() {
	        CorsConfiguration configuration = new CorsConfiguration();
	        configuration.setAllowCredentials(false); 
	        configuration.setAllowedOriginPatterns(List.of("*"));
	        configuration.setAllowedOrigins(List.of("*"));
	        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTION"));
	        configuration.setAllowedHeaders(Arrays.asList("X-idp", "Authorization", "Content-Type", "x-requested-with", "authorization", "credential", "X-XSRF-TOKEN"));
	        configuration.setExposedHeaders(Arrays.asList("xsrf-token", "Content-Disposition"));
	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	        source.registerCorsConfiguration("/**", configuration);
	        return source;
	    }
}
