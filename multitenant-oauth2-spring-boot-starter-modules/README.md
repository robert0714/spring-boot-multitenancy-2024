## Requirements
### Podman 
#### Instructions for Windows
```
wsl --install --no-distribution
wsl --update
dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
```
See https://github.com/containers/podman/blob/main/docs/tutorials/podman-for-windows.md
* [Installation](https://community.chocolatey.org/packages/podman-cli)
```bash
choco install -y podman-cli
```
* Start up
```bash
podman machine init
podman machine start

```
* Stop
```bash
podman machine stop
podman machine rm 
```
# multitenancy in Lambda ?
https://youtu.be/-PgFoRGaa6s?t=1961

# Issues about multitenancy 
* ThreadLocal has limits 。
  * Scenario
    * Asyncho code..
      * **CompletableFuture** 
    * In Spring Framwork, Using``@Async`` , ``TaskDecorator``, ``ThreadPoolTaskExecutor`` to process：
      * Article: https://www.bytefish.de/blog/spring_boot_multitenancy.html
      * github: 
        * [``TaskDecorator``](https://github.com/bytefish/SpringBootMultiTenancy/blob/master/SpringBootMultiTenancy/src/main/java/de/bytefish/multitenancy/async/TenantAwareTaskDecorator.java)
        * [``ThreadPoolTaskExecutor``](https://github.com/bytefish/SpringBootMultiTenancy/blob/b0c8e0c52b5c64b9864af5e1910666e057478a2a/SpringBootMultiTenancy/src/main/java/de/bytefish/multitenancy/async/AsyncConfig.java#L17-L26)
        * [``@Async``](https://github.com/bytefish/SpringBootMultiTenancy/blob/b0c8e0c52b5c64b9864af5e1910666e057478a2a/SpringBootMultiTenancy/src/main/java/de/bytefish/multitenancy/repositories/ICustomerRepository.java#L16)
          * InheritableThreadLocal的對策：https://yoavbenishai.wordpress.com/2016/06/20/spring-async-annotationa-and-threadlocal/
  * Using Elastic APM to tracing ,Using Transaction observe metadata:
    * Elastic APM Official Document
      * https://www.elastic.co/guide/en/apm/guide/7.17/apm-data-security.html#filters-http-header
* oauth resource url path
  * [Configuration](https://github.com/ThomasVitale/spring-boot-multitenancy/blob/1f9caec0d6ec655f60f002676a4503bb962743e5/instrument-service/src/main/resources/application.yml#L48L-L61)
  * [Code](https://github.com/ThomasVitale/spring-boot-multitenancy/blob/1f9caec0d6ec655f60f002676a4503bb962743e5/instrument-service/src/main/java/com/thomasvitale/instrumentservice/multitenancy/security/TenantAuthenticationManagerResolver.java#L27-L37)
    * To Configure
     ```java
	// Mimic the default configuration for JWT validation.	
	private AuthenticationManager  buildJwtAuthenticationManager(String tenantId) {
	
	    // this is the keys endpoint for okta
	    var issuerUri = tenantDetailsService.loadTenantByIdentifier(tenantId).issuer();
	    
	    // see http://localhost:8080/realms/dukes/.well-known/openid-configuration
	    String jwkSetUri = issuerUri + "/protocol/openid-connect/certs";
	    
	    // This is basically the default jwt logic	
	    JwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
	    
	    JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
	 
	    authenticationProvider.setJwtAuthenticationConverter(new JwtBearerTokenAuthenticationConverter());
	 
	    return authenticationProvider::authenticate;	 
	}
	    
	// Mimic the default configuration for opaque token validation
	private AuthenticationManager buildOpaqueAuthenticationManager(String tenantId) {
		TenantDetails tenant = tenantDetailsService.loadTenantByIdentifier(tenantId);
		var issuerUri = tenant.issuer();

		// see http://localhost:8080/realms/dukes/.well-known/openid-configuration
		String introspectionUri = issuerUri + "/protocol/openid-connect/token/introspect";
		String clientId = tenant.clientId() ;
		String clientSecret = tenant.clientSecret();

		// The default opaque token logic
		OpaqueTokenIntrospector introspectionClient =new SpringOpaqueTokenIntrospector(introspectionUri, clientId,
				clientSecret);
		return new OpaqueTokenAuthenticationProvider(introspectionClient)::authenticate;
	}
     ```
* Configure swagger
  * mutil-tenant , configure the tenantId in header?
# Video
* https://www.youtube.com/watch?v=pG-NinTx4O4
* time[10:17](https://youtu.be/pG-NinTx4O4?t=617) 
# Slides
* url:   
   https://speakerdeck.com/thomasvitale/multitenant-mystery-only-rockers-in-the-building
* other slides:
  * How to integrate Hibernates Multitenant feature with Spring Data JPA in a Spring Boot application
    * https://spring.io/blog/2022/07/31/how-to-integrate-hibernates-multitenant-feature-with-spring-data-jpa-in-a-spring-boot-application
  * Multitenancy in Hibernate
    * https://docs.jboss.org/hibernate/orm/6.2/userguide/html_single/Hibernate_User_Guide.html#multitenacy
  * Multitenancy OAuth2 with Spring Security
     * https://www.youtube.com/watch?v=ke13w8nab-k
  * Context Propagation with Project Reactor 3
     * https://spring.io/blog/2023/03/28/context-propagation-with-project-reactor-1-the-basics
  * Creating a custom Spring Cloud Gateway Filter 
     * https://spring.io/blog/2022/08/26/creating-a-custom-spring-cloud-gateway-filter
  * Multitenancy with Spring Data JDBC
     * https://spring.io/blog/2022/03/23/spring-tips-multitenant-jdbc
 # GitHub repo
 * concept: https://github.com/ThomasVitale/multitenant-spring-boot-demo
 * full： https://github.com/ThomasVitale/spring-boot-multitenancy
 ## Database
 * flyway
   * https://www.baeldung.com/database-migrations-with-flyway
   * https://www.baeldung.com/spring-boot-flyway-repair
   * https://documentation.red-gate.com/fd/quickstart-command-line-184127576.html
## parameters 
  * https://docs.spring.io/spring-boot/appendix/application-properties/index.html
  * 2024.0619 test env               
    ```bash
       java -jar instrument-service-0.0.1-SNAPSHOT.jar 
                 --spring.datasource.url=jdbc:postgresql://172.18.204.152:5432/grafana 
                 --spring.datasource.username=user 
                 --spring.datasource.password=password  
    ```
              
* Now open the browser window and navigate to `http://dukes.rock/instruments/`. You'll be redirected to the Keycloak authentication page. Log in with `isabelle/password`. The result will be the list of instruments from the Dukes rock band.
* Now open another browser window and navigate to `http://beans.rock/instruments/`. You'll be redirected to the Keycloak authentication page. Log in with `bjorn/password`. The result will be the list of instruments from the Beans rock band.
* test path
  ```bash
                 http://localhost:8181/instruments
                 http://dukes.rock/actuator
                 http://beans.rock/actuator 
                 http://dukes.rock/instruments
                 http://beans.rock/instruments
                 http://localhost:12345/
                 http://localhost:3000/dashboards
                 http://localhost:4318/
  ```
# keywords:
  * TenantContext 
    * Refactoring: ``com.thomasvitale.instrumentservice.multitenancy.context.TenantContextHolder``
  * [MVC configuration](https://youtu.be/pG-NinTx4O4?t=582):
    * First,  where's the resolver I need to mark this as a component . so spring will find it and then I need to provide the web configuration so I can implement the web MVC configurer that provides a method to add [interceptors to the application so I can say intercept registry my tenant interceptor](https://github.com/ThomasVitale/multitenant-spring-boot-demo/blob/main/instrument-service/src/main/java/com/thomasvitale/instrumentservice/multitenancy/web/WebConfig.java#L18).
    * All right ,so at this point every request if I provide that HTTP header will know what is the current tenant .so I can just to test it out .I can define a rest controller to return the current tenant .
    * And in this method now and anywhere else as part of the processing with the request I can say tenant context dot get standard all right let's verify that now everything is working correctly before moving on the application is running it's running on Port 8181 
      ```java
          package com.thomasvitale.instrumentservice.instrument.web;

          import com.thomasvitale.instrumentservice.multitenancy.context.TenantContext;
          import org.springframework.web.bind.annotation.GetMapping;
          import org.springframework.web.bind.annotation.RequestMapping;
          import org.springframework.web.bind.annotation.RestController;

          @RestController
          @RequestMapping("tenant")
          public class TenantController {
              @GetMapping
              String getTenant(){
                  return TenantContext.getTenantId();
              }
          }
      ```
 
      * [getConnection](https://github.com/ThomasVitale/multitenant-spring-boot-demo/blob/main/instrument-service/src/main/java/com/thomasvitale/instrumentservice/multitenancy/data/hibernate/ConnectionProvider.java#L36-L39)
      * [releaseConnection](https://github.com/ThomasVitale/multitenant-spring-boot-demo/blob/main/instrument-service/src/main/java/com/thomasvitale/instrumentservice/multitenancy/data/hibernate/ConnectionProvider.java#L43-L46)
      * [TenantInterceptor](https://github.com/ThomasVitale/multitenant-spring-boot-demo/blob/main/instrument-service/src/main/java/com/thomasvitale/instrumentservice/multitenancy/web/TenantInterceptor.java#L22-L26)
    * **Arconia framework**:  
      * Description: Arconia is a framework to build SaaS, multitenant applications using Java and Spring Boot.
      * Github: https://github.com/arconia-io/arconia
# Other References
* https://www.thomasvitale.com/activity/