FROM ubuntu:latest
LABEL authors="nwank"

ENTRYPOINT ["top", "-b"]

# ============================
#   STEP 1 — Build the JAR
# ============================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom for dependency caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Fix: make mvnw executable
RUN chmod +x mvnw

# Pre-download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the Spring Boot JAR (skip tests for faster build)
RUN ./mvnw package -DskipTests -B

# ============================
#   STEP 2 — Run the JAR
# ============================
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run Spring Boot on port 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=8080"]