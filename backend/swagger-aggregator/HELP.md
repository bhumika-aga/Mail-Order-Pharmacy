# MediFlow Swagger Aggregator 📚

Centralized API documentation hub and interactive testing platform for all MediFlow microservices, providing unified access to comprehensive API documentation with real-time testing capabilities.

## Features

- **Unified API Hub**: Single entry point for all MediFlow microservice APIs
- **Interactive Testing**: Comprehensive Swagger UI with real-time API testing capabilities
- **Service Discovery**: Automatic aggregation and monitoring of API specifications
- **Cross-Service Integration**: Test complete workflows across all microservices
- **Live Documentation**: Dynamic updates reflecting real-time service changes
- **Production Ready**: Optimized for Render.com deployment with health monitoring
- **Developer Experience**: Enhanced API exploration with examples and schema validation
- **Authentication Testing**: Built-in JWT token management for secure endpoint testing

## Aggregated Services

This service aggregates comprehensive API documentation from:

- **Auth Microservice** 🔐 (Port 8084): Advanced JWT authentication and user management
- **Drugs Microservice** 💊 (Port 8081): Intelligent drug inventory and multi-location stock management
- **Subscription Microservice** 📋 (Port 8082): Smart subscription management and prescription handling
- **Refill Microservice** 🔄 (Port 8083): Automated refill processing and order tracking

## Configuration

- **Port**: 8085
- **Main URL**: <http://localhost:8085/swagger-ui.html>
- **Service URLs**: Configurable via environment variables
- **OpenAPI Version**: 3.0.3

## Quick Start

```bash
# Build and run
./mvnw clean spring-boot:run

# Access Unified Swagger UI
open http://localhost:8085/swagger-ui.html

# View OpenAPI Specification
open http://localhost:8085/v3/api-docs
```

## Environment Configuration

```bash
# Service URLs (configurable)
AUTH_SERVICE_URL=http://localhost:8084
DRUGS_SERVICE_URL=http://localhost:8081
SUBSCRIPTION_SERVICE_URL=http://localhost:8082
REFILL_SERVICE_URL=http://localhost:8083
```

## API Testing

Use the Swagger UI to:

1. **Authenticate**: Use the Auth service signin endpoint to get JWT token
2. **Authorize**: Click "Authorize" button and enter "Bearer {token}"
3. **Test APIs**: Execute any endpoint across all microservices
4. **View Responses**: See real-time API responses and status codes

## Service Health

Monitor all microservices health from the aggregator:

```bash
# Check if all services are accessible
curl http://localhost:8085/actuator/health
```

## Enterprise Features

- **Interactive Testing Environment**: Complete API testing suite with real-time validation
- **Comprehensive Examples**: Request/response examples for all endpoints
- **Schema Validation**: Real-time validation with detailed error reporting
- **Security Testing**: JWT authentication flow testing and token management
- **Cross-Service Workflows**: End-to-end testing across all microservices
- **Performance Monitoring**: API response time tracking and health metrics
- **Developer Tools**: Export capabilities for Postman, Insomnia, and other tools
- **Production URLs**: Ready for deployment with environment-specific configurations

## Development

When adding new microservices:

1. Add service URL to application.yml
2. Update SwaggerConfig.java
3. Configure CORS settings
4. Test API aggregation

## Troubleshooting

### Common Issues

**Configuration Error - "Property 'spring.profiles.active' invalid":**

```yaml
# ❌ WRONG: Don't include spring.profiles.active in application-prod.yml
spring:
  profiles:
    active: prod  # This causes startup failure

# ✅ CORRECT: Remove the property from profile-specific files
# Use environment variable or command line instead:
# -Dspring.profiles.active=prod
```

**Service Communication Issues:**

- Ensure all microservices are running on correct ports
- Check environment variables for service URLs
- Verify JWT token authentication is working

**Swagger UI Not Loading:**

```bash
# Check if service is running
curl http://localhost:8085/actuator/health

# Access Swagger UI
open http://localhost:8085/swagger-ui.html
```

## Security

- CORS configuration for cross-origin requests
- JWT token testing capabilities
- Secure service-to-service communication
- Protected endpoint documentation
