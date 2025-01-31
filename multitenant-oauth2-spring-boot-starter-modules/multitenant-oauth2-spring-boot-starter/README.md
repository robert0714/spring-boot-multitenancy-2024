# Spring Boot starter library for multi-tenant OAuth2 resource servers

This is a starter library for multi-tenant OAuth2 resource servers implemented with Spring. 
The code in this project is based on the samples from the official
[Spring Security documentation](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/multitenancy.html).

## Installation

Add the dependency to your pom.xml file:

```xml
<dependency>
    <groupId>com.thomasvitale</groupId>
    <artifactId>multitenant-oauth2-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Usage

The auto-configuration for a multi-tenant OAuth2 resource server can be activated by adding the property
`spring.security.oauth2.resourceserver.multitenant.enabled=true` 
to your application properties.

If you use Mysql or Mariadb , default schema need to be adjust by `multitenancy.defaultSchema=test` .
## Configuration

All configuration properties start with the prefix
`spring.security.oauth2.resourceserver.multitenant.*`

| Key                        | Allowed values          | Default value  |
|-----------------------------|-------------------------|---------------| 
| `header.header-name`  | Any string              | x-tenant-id   |

### AutoConfiguration
* `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` need to adjsut
```
com.thomasvitale.multitenant.config.MultiTenantResourceServerAutoConfiguration
```



