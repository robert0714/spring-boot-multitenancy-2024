package com.thomasvitale.multitenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter; 

import java.util.Optional; 
import static org.hamcrest.Matchers.containsString; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;
import com.nimbusds.jwt.proc.JWTProcessor;
import com.thomasvitale.multitenant.app.advanced.InstrumentServiceApplication;
import com.thomasvitale.multitenant.app.basic.TestApplication;
import com.thomasvitale.multitenant.config.InstrumentTestConfig;
import com.thomasvitale.multitenant.config.InstrumentTestDataConfig;
import com.thomasvitale.multitenant.config.MultiTenantHeaderTestConfiguration;
import com.thomasvitale.multitenant.tenantdetails.TenantDetails;
import com.thomasvitale.multitenant.tenantdetails.TenantDetailsService; 
 

@SpringBootTest(classes = TestApplication.class, properties = {
        "spring.security.oauth2.resourceserver.multitenant.enabled=true", 
        "spring.security.oauth2.resourceserver.multitenant.header.header-name="
                + MultiTenantHeaderApplicationTests.HEADER_NAME,
})

@Import(MultiTenantHeaderTestConfiguration.class)
@AutoConfigureMockMvc
public class MultiTenantHeaderApplicationTests { 

    static final String HEADER_NAME = "TEST-TENANT-ID";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext context;

    @MockitoBean
    private TenantDetailsService tenantService;
    
    @Test
    void contextLoads() {
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(JWTClaimsSetAwareJWSKeySelector.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(JWTProcessor.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(OAuth2TokenValidator.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(JwtDecoder.class));
//        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(AuthenticationManagerResolver.class));
        
        assertThrows(NoSuchBeanDefinitionException.class,
                () -> context.getBean("multiTenantJwtFilterChain", SecurityFilterChain.class));
         
        assertThat(context.getBean("multiTenantHeaderFilter", OncePerRequestFilter.class)).isNotNull(); 
    }   
    
    @Test
    void getWithKnownTenant_shouldReturnHelloWorld() throws Exception {
        String tenantId = "test-tenant";
		TenantDetails tenant = new TenantDetails(tenantId, true, "schema01", "http://test.dev/test-tenant",
				"edge-service", "rocking-secret");		                

        Mockito.doReturn(Optional.of(tenant))
                .when(tenantService).getById(tenantId);
        
        Mockito.doReturn(tenant)
             .when(tenantService).loadTenantByIdentifier(tenantId);

        mockMvc.perform(get("/").header(HEADER_NAME, tenantId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello World from " + tenantId)));
    }
    @Test
    void getWithUnknownTenant_doNothing() throws Exception {
        String tenantId = "test-tenant";

        Mockito.doReturn(Optional.empty())
                .when(tenantService).getById(tenantId);
        Mockito.doReturn(null)
                .when(tenantService).loadTenantByIdentifier(tenantId);

        mockMvc.perform(get("/").header(HEADER_NAME, tenantId))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }
    @Test
    void getWithoutTenant_doNothing() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }
    
}

