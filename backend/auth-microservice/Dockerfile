# auth-microservice: multi-stage build. The build context is this folder:
#   docker build -t auth-microservice backend/auth-microservice
# (on a hosting platform: Dockerfile path backend/auth-microservice/Dockerfile, build context backend/auth-microservice)

# ---- Build stage: compile with the official Maven image ----
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

# Resolve dependencies first so they are cached between source changes
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src src
RUN mvn -B clean package -DskipTests

# ---- Runtime stage: slim JRE, non-root user ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
COPY --from=builder --chown=appuser:appgroup /build/target/auth-microservice-*.jar app.jar
USER appuser

# Most hosting platforms inject PORT; fall back to the service's default port.
ENV PORT=8084
EXPOSE 8084

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -fs http://localhost:${PORT}/actuator/health || exit 1

CMD ["sh", "-c", "exec java -Dserver.port=$PORT -jar /app/app.jar"]
