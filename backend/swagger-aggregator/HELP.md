# Swagger Aggregator Service

Centralized API documentation aggregator for all Mail-Order Pharmacy microservices.

## Features

- **Unified Documentation**: Single entry point for all microservice APIs
- **OpenAPI Integration**: Comprehensive Swagger UI with interactive testing
- **Service Discovery**: Automatic aggregation of API specifications
- **Cross-service Testing**: Test all microservice endpoints from one interface
- **Real-time Updates**: Dynamic documentation updates as services change

## Aggregated Services

This service aggregates API documentation from:

- **Auth Microservice** (Port 8084): Authentication and user management APIs
- **Drugs Microservice** (Port 8081): Drug inventory and stock management APIs
- **Subscription Microservice** (Port 8082): Subscription and prescription APIs
- **Refill Microservice** (Port 8083): Refill orders and processing APIs

## Configuration

- **Port**: 8085
- **Main URL**: http://localhost:8085/swagger-ui.html
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

## Features

- Interactive API testing environment
- Request/response examples
- Schema validation
- Error code documentation
- Authentication flow testing
- Cross-service API exploration

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
