FROM ghcr.io/graalvm/native-image-community:17 AS builder
RUN microdnf install maven -y
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src src
RUN mvn -Pnative native:compile -DskipTests 

FROM oraclelinux:9-slim
WORKDIR /app
COPY --from=builder app/target/instrument-service ./
ENTRYPOINT ["./instrument-service"]