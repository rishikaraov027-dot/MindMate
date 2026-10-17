# ==============================================================================
# MindMate AI - Spring Boot Web Application Dockerfile for IBM Datathon
# Multi-Architecture Support: linux/amd64, linux/s390x (IBM LinuxONE z16)
# ==============================================================================

# Stage 1: Build the Java application
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy Maven POM and download dependencies for layer caching
COPY pom.xml .
RUN mvn dependency:resolve -B || true

# Copy application source code and package
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Minimal, hardened production runtime
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Install curl for container healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

# Create a non-root group and user for IBM LinuxONE security standards
RUN groupadd -r mindmate && useradd -r -g mindmate -d /app -s /sbin/nologin mindmate

# Create data directory for SQLite persistence
RUN mkdir -p /app/data && chown -R mindmate:mindmate /app

# Copy the packaged jar from the builder stage
COPY --from=builder --chown=mindmate:mindmate /build/target/*.jar app.jar

# Run as non-root user
USER mindmate

EXPOSE 8080

ENV SERVER_PORT=8080
ENV SPRING_DATASOURCE_URL=jdbc:h2:file:/app/data/mindmate;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE;MODE=LEGACY;NON_KEYWORDS=USER
ENV SPRING_DATASOURCE_DRIVER=org.h2.Driver
ENV SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.H2Dialect
ENV IBM_AI_SERVICE_URL=http://ai-engine:5000

HEALTHCHECK --interval=20s --timeout=5s --start-period=20s --retries=3 \
  CMD curl -f http://localhost:8080/ || exit 1

ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
