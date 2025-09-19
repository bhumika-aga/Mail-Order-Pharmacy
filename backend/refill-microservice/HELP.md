# MediFlow Refill Microservice - Development Guide 🚀

## Quick Start

Get the MediFlow Refill Microservice running in minutes:

```bash
# Clone and build
git clone <repository-url>
cd backend/refill-microservice
./mvnw clean spring-boot:run

# Access services
open http://localhost:8083/swagger-ui.html  # API Documentation
open http://localhost:8083/h2-console      # Database Console
open http://localhost:8083/actuator/health # Health Check
```

## Reference Documentation

Comprehensive documentation for MediFlow development:

* [MediFlow Complete Guide](../../MEDIFLOW_COMPLETE_GUIDE.md) - Full system documentation
* [Quick Deploy Guide](../../QUICK_DEPLOY.md) - Render.com deployment instructions
* [Docker Setup](../../DOCKER_README.md) - Container development guide
* [Official Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
* [Render.com Deployment Guide](https://render.com/docs)

## Production Deployment

This microservice is optimized for Render.com free tier deployment:

* **Docker Ready**: Optimized Dockerfile.refill for production
* **Environment Configuration**: Production-ready application-prod.yml
* **Health Monitoring**: Built-in actuator endpoints
* **JWT Security**: Enterprise-grade authentication
* **PostgreSQL Support**: Ready for production database

## Development Features

* **Hot Reload**: Spring DevTools for rapid development
* **Interactive Testing**: Swagger UI for API exploration
* **H2 Console**: In-memory database for development
* **Comprehensive Logging**: Detailed logging for debugging
* **Maven Wrapper**: No local Maven installation required
