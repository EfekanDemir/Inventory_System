# ──────────────────────────────────────────────────────────────
# notification-service Dockerfile — Multi-stage build
# Stage 1: Maven build
# Stage 2: Slim JRE runtime (eclipse-temurin:21-jre-alpine)
# ──────────────────────────────────────────────────────────────

# --- Stage 1: Build -----------------------------------------------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /build

COPY pom.xml .
COPY common-lib ./common-lib
COPY user-service ./user-service
COPY inventory-service ./inventory-service
COPY notification-service ./notification-service

RUN mvn clean package -pl notification-service -am -DskipTests -q --no-transfer-progress

# --- Stage 2: Runtime ---------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

RUN apk add --no-cache curl

COPY --from=builder /build/notification-service/target/*.jar app.jar

EXPOSE 8083

ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
