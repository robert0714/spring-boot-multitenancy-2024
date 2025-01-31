FROM ghcr.io/graalvm/native-image-community:17 AS builder
RUN microdnf install maven -y
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src src
RUN mvn native:compile -DskipTests 

FROM oraclelinux:9-slim
WORKDIR /app
COPY --from=builder app/target/login-api-multi-tenant ./
ENTRYPOINT ["./login-api-multi-tenant"]