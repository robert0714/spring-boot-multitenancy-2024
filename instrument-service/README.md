## Option 1: Building Executable JAR
To create an ``executable jar``, simply run:  

```bash
 mvn clean package -DskipTests
``` 

## Option 2: Building image with Spring boot
To create a image, Run the following command

```bash
mvn clean spring-boot:build-image -DskipTests
```
or 
```bash
docker build -f prod.maven.Dockerfile -t instrument-service .
```

## Option 3: Building native image with GraalVM (Spring native)
To create a native image, Run the following command

```bash
mvn clean spring-boot:build-image -Pnative  -DskipTests
```
or 
```bash
docker build -f prod.maven.native.Dockerfile -t instrument-service:native .
```


# Tests
## Using Compose
```bash
docker compose up -d instrument
docker logs -f instrument
```
