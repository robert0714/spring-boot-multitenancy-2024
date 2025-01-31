package com.thomasvitale.instrumentservice.config.swagger;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows; 
import io.swagger.v3.oas.annotations.security.SecurityScheme;
 
import io.swagger.v3.oas.annotations.security.SecurityRequirement; 
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 

@OpenAPIDefinition(
        security = {@SecurityRequirement(name = "security_auth"), @SecurityRequirement(name = "jwt_auth")}, // Global security requirement
        info = @Info(
            title = "Instrument service API",
            description = "Demo multi-tenant services",
            version = "v1"))
@SecurityScheme(
        name = "security_auth",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(authorizationCode =
                    @OAuthFlow(authorizationUrl = "${springdoc.oauthFlow.authorizationUrl}",
                               tokenUrl = "${springdoc.oauthFlow.tokenUrl}")))
@SecurityScheme(
        name = "jwt_auth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer")
@Configuration
public class OpenApiConfig {
 

    private static final String[] authPaths = {
    		"/instruments/**" ,
    		"/captcha/**",
            "/auth/**",
            "/verifyEmailAndCheckAccountExistAndThenSendEmail",
            "/emailUserConfirmationOfChange"            
    }; 
 
 

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("auth")
                .pathsToMatch(authPaths)
                .build();
    }
 
 
}
