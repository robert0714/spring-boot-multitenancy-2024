# Getting Started
## Swagger
http://localhost:8083/api/aggregate/auth/v1/swagger-ui/index.html#/

## Option 1: Building Executable JAR
To create an ``executable jar``, simply run:  

```bash
 mvn clean package -DskipTests
``` 
or 
```bash
docker build -f prod.maven.Dockerfile -t login-api-multi-tenant .
```

## Option 2: Building image with Spring boot
To create a image, Run the following command

```bash
mvn clean spring-boot:build-image -DskipTests
```

## Option 3: Building native image with GraalVM (Spring native)
To create a native image, Run the following command

```bash
mvn clean spring-boot:build-image -Pnative  -DskipTests
```
or 
```bash
docker build -f prod.maven.native.Dockerfile -t login-api-multi-tenant:native .
```