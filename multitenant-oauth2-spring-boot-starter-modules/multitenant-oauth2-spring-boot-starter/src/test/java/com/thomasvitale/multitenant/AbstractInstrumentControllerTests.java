package com.thomasvitale.multitenant;
 
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils; 
import org.junit.jupiter.api.BeforeEach; 
import org.mockito.Mockito; 
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomasvitale.multitenant.security.TenantAuthenticationManagerResolver;

import jakarta.servlet.http.HttpServletRequest;
import net.datafaker.Faker; 

 
public class AbstractInstrumentControllerTests {
	protected static final String JWT_TENANT_1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2lkcC5leGFtcGxlLm9yZy90ZW5hbnQtMSIsImlhdCI6MTY2NDU4MjQwMCwiZXhwIjoyNTI0NjA4MDAwLCJzdWIiOiJlNDg5ZmZlNy0yMmQyLTQwZWYtYmRlMS1mZDA2OWFjOGFmMGIiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsidXNlciJdfSwibmFtZSI6ImFsaWNlIn0.byGgJ1neIoYYE0gID2gWOo-PQDfHy3wasJ3NoD7iyz4";
	protected static final String JWT_TENANT_2 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2lkcC5leGFtcGxlLm9yZy90ZW5hbnQtMiIsImlhdCI6MTY2NDU4MjQwMCwiZXhwIjoyNTI0NjA4MDAwLCJzdWIiOiI0YWViYzRkMS0yNDFjLTQ5MzQtYjBiNi1lNjFmMGI1NmRjNzciLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsidXNlciJdfSwibmFtZSI6ImJvYiJ9.wHknw6r-vFO6pzz8DrHhLnxc0X428qGXpq3vxl1ygus";
	protected static final String JWT_UNKNOWN_TENANT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2lkcC5leGFtcGxlLm9yZy91bmtub3duLXRlbmFudCIsImlhdCI6MTY2NDU4MjQwMCwiZXhwIjoyNTI0NjA4MDAwLCJzdWIiOiIxNjU4MzE3Yy0wZWFhLTQwZTYtYWY3YS0wNGJiN2NlMmU5ZDkiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsidXNlciJdfSwibmFtZSI6Imdob3N0In0.e_MudNxU98pc2a8cboGjbvebZBwMux7P7wKP1rTkr7M";

    @MockBean
    private  TenantAuthenticationManagerResolver tamr;

    
    protected Faker faker = new Faker();		  
    
    @MockBean
    private  JwtDecoder jwtDecoder; 
    
    @BeforeEach
	public   void setUpBeforeClass() throws Exception {
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
	protected  JwtAuthenticationToken jwtAuth01() {
		JwtAuthenticationToken jat =  mock(JwtAuthenticationToken.class);
		when(jat.isAuthenticated()).thenReturn(true);
		Map<String, Object> dukeJWTmap = Map.of("realm_access", Map.of("roles", List.of("user")),
                "iss", "http://dukes.rocks/auth/realms/dukes",
                "preferred_username", "isabelle",
                "sub", "fad63ede-0c75-4249-96bc-49d662fbfd8c");
		when(jat.getTokenAttributes()).thenReturn(dukeJWTmap);
		return jat;
	}
	protected  JwtAuthenticationToken jwtAuth02() {
		JwtAuthenticationToken jat =  mock(JwtAuthenticationToken.class);
		when(jat.isAuthenticated()).thenReturn(true);
		Map<String, Object> dukeJWTmap = Map.of("realm_access", Map.of("roles", List.of("user")),
                "iss", "http://beans.rocks/auth/realms/beans",
                "preferred_username", "bjorn",
                "sub", "fad63ede-0c75-4249-96bc-49d662fbfd8c");
		when(jat.getTokenAttributes()).thenReturn(dukeJWTmap);
		return jat;
	}
	protected static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}  
}
