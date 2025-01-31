# BUILD STAGE
# FROM ubuntu:14.04  AS builder

# WORKDIR /app0

# RUN  apt-get update \
#   && apt-get install -y wget \
#   && wget -O elastic-apm-agent-1.43.0.jar https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/1.43.0/elastic-apm-agent-1.43.0.jar -P /app0/ \
#   && wget -O opentelemetry-javaagent.jar https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.33.4/opentelemetry-javaagent.jar -P /app0/ 
 

FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml . 
COPY src src
RUN mvn package -DskipTests

# FINAL STAGE
# FROM registry.access.redhat.com/ubi9/openjdk-17:latest
FROM registry.access.redhat.com/ubi9/openjdk-17-runtime:latest


COPY --from=builder /app/target/edge-service.jar  /deployments/app.jar

# COPY ./target/edge-service.jar /deployments/app.jar
WORKDIR /deployments

# COPY --from=builder /app0 .
 

EXPOSE 8181/tcp 9181/tcp 7600/tcp 

ENV TZ=Asia/Taipei
ENV JAVA_OPTS=""

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar  /deployments/app.jar"]