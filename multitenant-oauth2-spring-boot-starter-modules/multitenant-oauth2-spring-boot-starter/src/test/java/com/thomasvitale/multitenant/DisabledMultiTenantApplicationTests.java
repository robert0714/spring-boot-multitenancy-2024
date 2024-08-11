package com.thomasvitale.multitenant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;
import com.nimbusds.jwt.proc.JWTProcessor;
import com.thomasvitale.multitenant.app.basic.TestApplication;
 

@SpringBootTest(classes = TestApplication.class, properties = {
        "spring.security.oauth2.resourceserver.multitenant.enabled=false",
})
@AutoConfigureMockMvc
public class DisabledMultiTenantApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(JWTClaimsSetAwareJWSKeySelector.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(JWTProcessor.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(OAuth2TokenValidator.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(JwtDecoder.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(AuthenticationManagerResolver.class));
        assertThrows(NoSuchBeanDefinitionException.class,
                () -> context.getBean("multiTenantJwtFilterChain", SecurityFilterChain.class));
        assertThrows(NoSuchBeanDefinitionException.class,
                () -> context.getBean("multiTenantJwtInterceptor", HandlerInterceptor.class));
        assertThrows(NoSuchBeanDefinitionException.class,
                () -> context.getBean("multiTenantHeaderInterceptor", HandlerInterceptor.class));
        assertThrows(NoSuchBeanDefinitionException.class,
                () -> context.getBean("multiTenantWebMvcConfigurer", WebMvcConfigurer.class));
    }

    @Test
    void shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

}
