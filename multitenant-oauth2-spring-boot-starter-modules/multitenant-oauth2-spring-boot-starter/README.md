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

## Configuration

All configuration properties start with the prefix
`spring.security.oauth2.resourceserver.multitenant.*`     
     
| Key                          | Allowed values | Default value |
|------------------------------|----------------|---------------| 
| `header.header-name`   | Any string  | X-Tenant-Id   |



# Consideration
* What are the considerations for assigning the responsibility of the `tenant resolver` to the front-end?
1. First of all, it is specified that Request needs to carry tenant-id information.
   1. In this way, there is a way to handle situations where the K8s internal call service URL format is `<service-name>.<namespace>.svc.cluster.local:<service-port>`. Only by observing the Request record can APM identify which tenant it is.
   
2. There are different considerations for using gateway or SPA as the entrance to Requset to initially convert tenant information tenantId.
   | sub-domain<p>converted to<p>[tenantId](https://learn.microsoft.com/en-us/azure/architecture/guide/multitenant/considerations/map-requests#http-request-properties)<p>location of occurrence | Benefits to us now  | bad for us now |
   |----------------------------|-----------------------------------------|----------------------------|
   | gateway                    | Reduce coupling degree and be flexible  | It is not possible to coexist without tenants at the same time, and the current grasp is insufficient.  |
   | SPA                        | Simultaneous coexistence of no tenant situation  | prone to controversy|
   | backend                    | Tenant association cannot be analyzed from a ZAP perspective | (1)Violates general gateway/proxy setting conventions (Supplement 1)<p>(2)When the front-end is developed on a laptop, the URL used to call the back-end is different from `windows.location` (Supplement 2) |
      
   * Supplement 1: [vue reverse proxy](https://dev.azure.com/R230835/14067/_git/multi-tenant-demo-01?path=/frontend01/vite.config.js):
     ```javascript
           server: {
               host: '0.0.0.0',
               port: '4000' ,
               open: true,
               proxy: {
                 '/auth': 'http://localhost:8080',
                 '/api': {
                   target: 'http://localhost:8181',
                   changeOrigin: true, 
                 }, 
                 '/instruments': {
                   target: `http://${hostname}:8182`,
                   changeOrigin: true,
                 },
               }
             },
       ```
   * Supplement 2: From a front-end developer perspective：
     * Front-End Development Practices: The Most Common Types
       * The backend CORS is open to a maximum range of (`*`).
       * Whether the backend is started locally or remotely at 192.168.x.x will not affect front-end <font color="red">tenantless</font> development.
         * The backend starts on local laptop
           :::mermaid
           flowchart LR       
                subgraph developer's laptop 
                direction LR   
                c1("SPA")-- "call url-> localhost or window.loaction" -->a2("backend") 
           end     
           :::
         * Backend started at 192.168.x.x
           :::mermaid
           flowchart LR       
             subgraph vm 192.168.x.x
                a1("gateway")-- "call url-> localhost" -->a2("backend")
             end    
             subgraph developer's laptop 
               c1("SPA")-- "call url-> 192.168.x.x " -->a1
             end
           :::
       * And let the multi-tenant tenantId be converted to:
         *  Front-end execution: Can the back-end smoothly know who the tenant is?
         *  Gateway execution: HOST always receives `192.168.x.x`, <font color="red">cannot determine the tenant</font>.
         *  Back-end execution: HOST always receives `192.168.x.x`, <font color="red">cannot determine the tenant</font>.
     * Front-end development practices: The second most common type (devserver-proxy)
       * Backend CORS<font color="red"> uses default values</font>.
       * Whether the backend is started locally or remotely at 192.168.62.37 will not affect front-end <font color="red">tenantless</font> development.
       * Use devserver-proxy: https://cli.vuejs.org/config/#devserver-proxy
         * The backend starts on local laptop
           :::mermaid
           flowchart LR    
             subgraph developer's laptop 
               direction LR   
               c1("SPA")-- "call url-> localhost" -->devserver-proxy 
               devserver-proxy-- "call url-> localhost" -->a2("backend")
             end
           :::
         * Backend started at 192.168.x.x
           :::mermaid
           flowchart LR       
             subgraph vm 192.168.x.x
                a1("gateway")-- "call url-> localhost" -->a2("backend")
             end
    
             subgraph developer's laptop 
               c1("SPA")-- "call url-> localhost" -->devserver-proxy
               devserver-proxy-- "call url-> 192.168.x.x" -->a1
             end
           :::
       * And let the multi-tenant tenantId be converted to:
         *  Front-end execution: Can the back-end smoothly know who the tenant is?
         *  Gateway execution: devserver-proxy makes HOST receive always `192.168.x.x` or `localhost`, <font color="red">cannot determine the tenant</font>.
         *  Back-end execution: devserver-proxy allows HOST to always receive `192.168.x.x` or `localhost`, and <font color="red">cannot determine the tenant</font>.