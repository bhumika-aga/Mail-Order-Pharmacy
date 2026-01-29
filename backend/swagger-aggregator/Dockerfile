# Multi-stage build for swagger-aggregator
FROM maven:3.9-eclipse-temurin-17 AS builder

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
FROM eclipse-temurin:17-jre-jammy

# Add labels for better container management
LABEL maintainer="pharmacy-team"
LABEL version="1.0.0"
LABEL description="Swagger aggregator for Mail Order Pharmacy microservices"

# Create app user for security
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Set working directory
WORKDIR /app

# Create data directory
RUN mkdir -p /app/data && chown -R appuser:appgroup /app

# Copy the jar file from builder stage
COPY --from=builder /app/target/swagger-aggregator-*.jar /app/swagger-aggregator.jar

# Change ownership to app user
RUN chown appuser:appgroup /app/swagger-aggregator.jar

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8085

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8085/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/swagger-aggregator.jar"]