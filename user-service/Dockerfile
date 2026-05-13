# ──────────────────────────────────────────────────────────────
# user-service Dockerfile — Multi-stage build
# Stage 1: Maven build
# Stage 2: Slim JRE runtime (eclipse-temurin:21-jre-alpine)
# ──────────────────────────────────────────────────────────────

# --- Stage 1: Build -----------------------------------------------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Önce parent pom ve common-lib'i kopyala
COPY pom.xml .
COPY common-lib ./common-lib
COPY user-service ./user-service
COPY inventory-service ./inventory-service
COPY notification-service ./notification-service

# Tüm projeyi derle (sadece user-service ve bağımlılıkları)
RUN mvn clean package -pl user-service -am -DskipTests --no-transfer-progress

# --- Stage 2: Runtime ---------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# curl: healthcheck için gerekli
RUN apk add --no-cache curl

# JAR dosyasını builder stage'den al
COPY --from=builder /build/user-service/target/*.jar app.jar

# Spring Boot aktüatör portu
EXPOSE 8081

# Loglar stdout'a yönlendirilir (Docker log driver)
ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
