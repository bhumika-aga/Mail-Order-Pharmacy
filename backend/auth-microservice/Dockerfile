# Multi-stage build for auth-microservice
FROM maven:3.9.2-openjdk-17-slim AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Production stage
FROM openjdk:17-jre-slim

# Add labels for better container management
LABEL maintainer="pharmacy-team"
LABEL version="1.0.0"
LABEL description="Authentication microservice for Mail Order Pharmacy"

# Create app user for security
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Set working directory
WORKDIR /app

# Create data directory for H2 database
RUN mkdir -p /app/data && chown -R appuser:appgroup /app

# Copy the jar file from builder stage
COPY --from=builder /app/target/auth-microservice-*.jar /app/auth-microservice.jar

# Change ownership to app user
RUN chown appuser:appgroup /app/auth-microservice.jar

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8084

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8084/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/auth-microservice.jar"]