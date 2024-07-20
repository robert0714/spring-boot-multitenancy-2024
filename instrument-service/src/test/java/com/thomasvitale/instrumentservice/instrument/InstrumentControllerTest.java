package com.thomasvitale.instrumentservice.instrument;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication; 
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext; 
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.thomasvitale.instrumentservice.TestInstrumentServiceApplication;
import com.thomasvitale.instrumentservice.multitenancy.security.TenantAuthenticationManagerResolver;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant; 
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@DirtiesContext
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
		"spring.datasource.url=jdbc:h2:mem:multitenant;DATABASE_TO_LOWER=TRUE",
		"spring.datasource.username=user",
		"spring.datasource.password=pw",
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.show-sql=true",
		"spring.jpa.properties.hibernate.format_sql=true",
		"spring.sql.init.continue-on-error=false"})
//@Import(TestInstrumentServiceApplication.class)
public class InstrumentControllerTest {
	private static final String JWT_TENANT_1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2lkcC5leGFtcGxlLm9yZy90ZW5hbnQtMSIsImlhdCI6MTY2NDU4MjQwMCwiZXhwIjoyNTI0NjA4MDAwLCJzdWIiOiJlNDg5ZmZlNy0yMmQyLTQwZWYtYmRlMS1mZDA2OWFjOGFmMGIiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsidXNlciJdfSwibmFtZSI6ImFsaWNlIn0.byGgJ1neIoYYE0gID2gWOo-PQDfHy3wasJ3NoD7iyz4";
    private static final String JWT_TENANT_2 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2lkcC5leGFtcGxlLm9yZy90ZW5hbnQtMiIsImlhdCI6MTY2NDU4MjQwMCwiZXhwIjoyNTI0NjA4MDAwLCJzdWIiOiI0YWViYzRkMS0yNDFjLTQ5MzQtYjBiNi1lNjFmMGI1NmRjNzciLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsidXNlciJdfSwibmFtZSI6ImJvYiJ9.wHknw6r-vFO6pzz8DrHhLnxc0X428qGXpq3vxl1ygus";
    private static final String JWT_UNKNOWN_TENANT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2lkcC5leGFtcGxlLm9yZy91bmtub3duLXRlbmFudCIsImlhdCI6MTY2NDU4MjQwMCwiZXhwIjoyNTI0NjA4MDAwLCJzdWIiOiIxNjU4MzE3Yy0wZWFhLTQwZTYtYWY3YS0wNGJiN2NlMmU5ZDkiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsidXNlciJdfSwibmFtZSI6Imdob3N0In0.e_MudNxU98pc2a8cboGjbvebZBwMux7P7wKP1rTkr7M";

    private static final String TENANT_NAME_HEADER="X-TenantId"; 
    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private TenantAuthenticationManagerResolver tamr;
    
    @MockBean
    private JwtDecoder jwtDecoder;
    
    
	@BeforeEach
	protected void setUp() throws Exception {
		AuthenticationManager am = mock(AuthenticationManager.class);
		
		when(tamr.resolve(Mockito.any(HttpServletRequest.class))).thenAnswer(invocation->{
			Object args = invocation.getArguments()[0];
			if (args == null) {
				return mock(AuthenticationManager.class);
			} 
			HttpServletRequest res = (HttpServletRequest) args;
			String jwtheader = res.getHeader(HttpHeaders.AUTHORIZATION);
			if (!StringUtils.containsAny(jwtheader, JWT_TENANT_1, JWT_TENANT_2)) {
				return mock(AuthenticationManager.class);
			}
			return am; 
		});
		 
		when(am.authenticate(any(Authentication.class))).thenAnswer(invocation -> {
			Object args = invocation.getArguments()[0];
			if (args == null) {
				return mock(JwtAuthenticationToken.class);
			}
			if (args instanceof BearerTokenAuthenticationToken) {
				BearerTokenAuthenticationToken bta = (BearerTokenAuthenticationToken) args;
				final String jwt = bta.getToken();
				if (!StringUtils.containsAny(jwt, JWT_TENANT_1, JWT_TENANT_2)) {
					return mock(JwtAuthenticationToken.class);
				}
				final JwtAuthenticationToken result = switch (jwt) {
				    case JWT_TENANT_1 -> jwtAuth01();
				    case JWT_TENANT_2 -> jwtAuth02();
				    default -> mock(JwtAuthenticationToken.class);
				};
				return result;
			}
			return mock(JwtAuthenticationToken.class);
		});
		
		
		when(jwtDecoder.decode(JWT_TENANT_1))
        .thenReturn(new Jwt(JWT_TENANT_1, Instant.now(), Instant.now().plusSeconds(60),
                Map.of("alg", "HS256", "typ", "JWT"),
                Map.of("realm_access", Map.of("roles", List.of("user")),
                        "iss", "https://idp.example.org/tenant-1",
                        "sub", "e489ffe7-22d2-40ef-bde1-fd069ac8af0b")));
		
		when(jwtDecoder.decode(JWT_TENANT_2))
        .thenReturn(new Jwt(JWT_TENANT_2, Instant.now(), Instant.now().plusSeconds(60),
                Map.of("alg", "HS256", "typ", "JWT"),
                Map.of("realm_access", Map.of("roles", List.of("user")),
                        "iss", "https://idp.example.org/tenant-2",
                        "sub", "4aebc4d1-241c-4934-b0b6-e61f0b56dc77")));
	}
	protected JwtAuthenticationToken jwtAuth01() {
		JwtAuthenticationToken jat =  mock(JwtAuthenticationToken.class);
		when(jat.isAuthenticated()).thenReturn(true);
		Map<String, Object> dukeJWTmap = Map.of("realm_access", Map.of("roles", List.of("user")),
                "iss", "http://dukes.rocks/auth/realms/dukes",
                "preferred_username", "isabelle",
                "sub", "fad63ede-0c75-4249-96bc-49d662fbfd8c");
		when(jat.getTokenAttributes()).thenReturn(dukeJWTmap);
		return jat;
	}
	protected JwtAuthenticationToken jwtAuth02() {
		JwtAuthenticationToken jat =  mock(JwtAuthenticationToken.class);
		when(jat.isAuthenticated()).thenReturn(true);
		Map<String, Object> dukeJWTmap = Map.of("realm_access", Map.of("roles", List.of("user")),
                "iss", "http://beans.rocks/auth/realms/beans",
                "preferred_username", "bjorn",
                "sub", "fad63ede-0c75-4249-96bc-49d662fbfd8c");
		when(jat.getTokenAttributes()).thenReturn(dukeJWTmap);
		return jat;
	}

	@AfterEach
	protected void tearDown() throws Exception {
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
	@Disabled
	void unknownTenant() throws Exception { 
		sendRequest(JWT_UNKNOWN_TENANT,"").andExpect(status().isUnauthorized());
 
	}
	
	@Test
	@Disabled
	void givenRequestIsAnonymous_whenGetGreet_thenUnauthorized() throws Exception {
		mvc.perform(get("/instruments").with(SecurityMockMvcRequestPostProcessors.anonymous()))
	      .andExpect(status().isUnauthorized());
	}
	

	@Test
	@Disabled
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
				request.addHeader(TENANT_NAME_HEADER, tenantId);
			}			
			return request;
		}

	}

}
