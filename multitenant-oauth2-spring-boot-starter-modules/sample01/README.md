# Spring Boot starter library for multi-tenant OAuth2 resource servers

This is a starter library for multi-tenant OAuth2 resource servers implemented with Spring. 
The code in this project is based on the samples from the official
[Spring Security documentation](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/multitenancy.html).

## Installation

Add the dependency to your pom.xml file:

```xml
<dependency>
    <groupId>com.iisigroup</groupId>
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

| Key                         | Allowed values                                                                                                                                                  | Default value |
|-----------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------| 
| `header.header-name`        | Any string                                                                                                                                                      | X-Tenant-Id   |




## Usage

You can use Docker Compose to run the necessary backing services for observability, authentication, and AI.

From the project root folder, run Docker Compose.

```bash
docker-compose up -d
docker compose up -d multitenant-db keycloak
```

The Instrument Service application can be run as follows to rely on Testcontainers to spin up a PostgreSQL database:

```bash
mvn clean package

java -jar target/instrument-service.jar \
          --spring.datasource.url=jdbc:postgresql://localhost:5432/multitenant  \
          --spring.datasource.username=user  \
          --spring.datasource.password=pw

mvn spring-boot:build-image

docker run --net=host \
       -it -d    \
       --env-file dockerenv-host \
       -p  8181:8181 \
        --name instrument \
        instrument-service
docker rm -f instrument

docker run   \
       -it -d    \
       --network my-lab-network \
       --env-file dockerenv-bridge \
        --name instrument \
        instrument-service   
```

 
 
Two tenants are configured: `dukes` and `beans`. Ensure you add the following configuration to your `hosts` file to resolve tenants from DNS names.

```bash
127.0.0.1       dukes.rock
127.0.0.1       beans.rock
```

Now open the browser window and navigate to `https://dukes.rock:1443/instruments/`. You'll be redirected to the Keycloak authentication page. Log in with `isabelle/password`. The result will be the list of instruments from the Dukes rock band.

Now open another browser window and navigate to `https://beans.rock:1443/instruments/`. You'll be redirected to the Keycloak authentication page. Log in with `bjorn/password`. The result will be the list of instruments from the Beans rock band.


# How to reach localhost on host from docker container?
* occasion: Using prometheus to scape the metrics of development application
* reference: 
  * https://forums.docker.com/t/how-to-reach-localhost-on-host-from-docker-container/113321
  * https://www.cnblogs.com/forlive/p/15989409.html
  * https://medium.com/@edemircan/a-deep-dive-into-dockerized-monitoring-and-alerting-for-spring-boot-with-prometheus-and-grafana-144fcd209822

