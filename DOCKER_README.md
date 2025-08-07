# MediFlow - Docker Setup

This document provides comprehensive information about the Docker setup for the MediFlow microservices application.

## 🏗️ Architecture Overview

The application consists of the following services:

- **Frontend**: React-based MediFlow member portal (port 3000)
- **Auth Microservice**: Authentication and authorization (port 8084)
- **Drugs Microservice**: Drug inventory management (port 8081)
- **Subscription Microservice**: Subscription management (port 8082)
- **Refill Microservice**: Prescription refill processing (port 8083)
- **Swagger Aggregator**: API documentation aggregator (port 8085)

## 📁 Project Structure

``` txt
MediFlow/
├── backend/
│   ├── auth-microservice/
│   │   └── Dockerfile
│   ├── drugs-microservice/
│   │   └── Dockerfile
│   ├── subscription-microservice/
│   │   └── Dockerfile
│   ├── refill-microservice/
│   │   └── Dockerfile
│   └── swagger-aggregator/
│       └── Dockerfile
├── frontend/
│   └── member-portal/
│       └── Dockerfile
├── scripts/
│   ├── build/
│   │   ├── build-all.sh
│   │   ├── build-backend.sh
│   │   ├── build-frontend.sh
│   │   └── clean-build.sh
│   ├── db-init/
│   │   ├── init-databases.sh
│   │   └── cleanup-databases.sh
│   ├── deploy/
│   │   ├── start-services.sh
│   │   ├── stop-services.sh
│   │   └── health-check.sh
│   └── setup/
│       ├── setup-project.sh
│       └── setup-dev-environment.sh
└── docker-compose.yml
```

## 🚀 Quick Start

### Prerequisites

- Docker 20.10+
- Docker Compose 2.0+
- Git

### Initial Setup

1. **Clone the repository** (if not already done)
2. **Run the project setup script**:

   ```bash
   ./scripts/setup/setup-project.sh
   ```

3. **Build all services**:

   ```bash
   ./scripts/build/build-all.sh
   ```

4. **Start all services**:

   ```bash
   ./scripts/deploy/start-services.sh -d
   ```

5. **Check service health**:

   ```bash
   ./scripts/deploy/health-check.sh
   ```

### Service URLs

Once running, access the services at:

- **Frontend**: <http://localhost:3000>
- **Auth Service**: <http://localhost:8084>
- **Drugs Service**: <http://localhost:8081>
- **Subscription Service**: <http://localhost:8082>
- **Refill Service**: <http://localhost:8083>
- **Swagger UI**: <http://localhost:8085>

## 🔧 Docker Configuration

### Backend Services (Spring Boot)

All backend services use a multi-stage build approach:

1. **Builder Stage**: Uses `maven:3.9.2-openjdk-17-slim` to compile Java code
2. **Runtime Stage**: Uses `openjdk:17-jre-slim` for smaller production images

**Key Features**:

- Non-root user execution for security
- Health checks with actuator endpoints
- Optimized layer caching
- Volume mounts for H2 database persistence

**Example Dockerfile Structure**:

```dockerfile
# Multi-stage build
FROM maven:3.9.2-openjdk-17-slim AS builder
# ... build steps ...

FROM openjdk:17-jre-slim
# ... runtime configuration ...
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1
```

### Frontend Service (React)

The frontend uses a multi-stage build with Nginx:

1. **Builder Stage**: Uses `node:18-alpine` to build React application
2. **Runtime Stage**: Uses `nginx:1.25-alpine` to serve static files

**Key Features**:

- Optimized Nginx configuration
- Client-side routing support
- Security headers
- Gzip compression
- Non-root user execution

## 🛠️ Available Scripts

### Build Scripts

| Script | Description |
|--------|-------------|
| `scripts/build/build-all.sh` | Build all services (backend + frontend) |
| `scripts/build/build-backend.sh` | Build only backend microservices |
| `scripts/build/build-frontend.sh` | Build only frontend application |
| `scripts/build/clean-build.sh` | Clean all artifacts and rebuild everything |

**Usage Examples**:

```bash
# Build everything
./scripts/build/build-all.sh

# Build only backend
./scripts/build/build-backend.sh

# Clean build (removes all images and artifacts)
./scripts/build/clean-build.sh
```

### Database Scripts

| Script | Description |
|--------|-------------|
| `scripts/db-init/init-databases.sh` | Initialize database directories |
| `scripts/db-init/cleanup-databases.sh` | Remove all database files |

### Deployment Scripts

| Script | Description |
|--------|-------------|
| `scripts/deploy/start-services.sh` | Start all services with Docker Compose |
| `scripts/deploy/stop-services.sh` | Stop all services |
| `scripts/deploy/health-check.sh` | Check health of all services |

**Usage Examples**:

```bash
# Start services in background
./scripts/deploy/start-services.sh -d

# Start with rebuild
./scripts/deploy/start-services.sh -b

# Stop services and remove volumes
./scripts/deploy/stop-services.sh -v

# Check service health
./scripts/deploy/health-check.sh
```

### Setup Scripts

| Script | Description |
|--------|-------------|
| `scripts/setup/setup-project.sh` | Initial project setup |
| `scripts/setup/setup-dev-environment.sh` | Development environment configuration |

## 🗄️ Database Configuration

The application uses H2 databases with file persistence:

- **Auth Service**: `./data/auth/auth-pharmacy.db`
- **Drugs Service**: `./data/drugs/drugs-pharmacy.db`
- **Subscription Service**: `./data/subscription/subscription-pharmacy.db`
- **Refill Service**: `./data/refill/refill-pharmacy.db`

Database console access:

- URL: `http://localhost:808X/h2-console` (replace X with service port)
- JDBC URL: `jdbc:h2:file:./data/[service]/[service]-pharmacy`
- Username: `sa`
- Password: `password`

## 🔍 Monitoring and Debugging

### Health Checks

All services include health check endpoints:

- Spring Boot services: `/actuator/health`
- Frontend: Root endpoint `/`

### Viewing Logs

```bash
# View all service logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f auth-microservice

# View last 100 lines
docker-compose logs --tail=100 drugs-microservice
```

### Container Management

```bash
# View running containers
docker-compose ps

# Restart a specific service
docker-compose restart auth-microservice

# Scale a service (not applicable for this stateful setup)
docker-compose up -d --scale drugs-microservice=2
```

## 🔐 Security Features

### Container Security

- All services run as non-root users
- Minimal base images (Alpine/Slim)
- Security headers in Nginx configuration
- Health checks for service monitoring

### Network Security

- Services communicate through Docker network
- No direct external access to backend services (except through defined ports)
- Environment-based configuration

## 🚀 Production Considerations

### Performance Optimization

- Multi-stage builds reduce image size
- Layer caching optimizes build times
- Nginx compression enabled for frontend
- Java security entropy configuration

### Resource Management

```yaml
# Add to docker-compose.yml for resource limits
deploy:
  resources:
    limits:
      memory: 512M
      cpus: '0.5'
    reservations:
      memory: 256M
      cpus: '0.25'
```

### External Database Configuration

For production, replace H2 with external databases:

```yaml
environment:
  - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/pharmacy
  - SPRING_DATASOURCE_USERNAME=pharmacy_user
  - SPRING_DATASOURCE_PASSWORD_FILE=/run/secrets/db_password
```

## 🔧 Troubleshooting

### Common Issues

1. **Port conflicts**: Ensure ports 3000, 8081-8085 are available
2. **Docker not running**: Start Docker daemon
3. **Build failures**: Check Docker disk space and memory
4. **Service startup failures**: Check logs and health endpoints

### Diagnostic Commands

```bash
# Check Docker system info
docker system info

# Check disk usage
docker system df

# Prune unused resources
docker system prune -a

# Check service dependencies
docker-compose config --services
```

### Performance Tuning

```bash
# Increase Docker memory (Docker Desktop)
# Settings > Resources > Memory > 4GB+

# Clean unused Docker resources
docker system prune -a --volumes
```

## 📝 Development Workflow

### Local Development

1. Run `./scripts/setup/setup-project.sh`
2. Run `./scripts/setup/setup-dev-environment.sh`
3. Use Docker Compose for services: `./scripts/deploy/start-services.sh -d`
4. Develop with hot reload (frontend) or IDE debugging (backend)

### Testing

```bash
# Run tests in containers
docker-compose exec auth-microservice mvn test
docker-compose exec member-portal npm test
```

### CI/CD Integration

The build scripts are designed for CI/CD integration:

```yaml
# Example GitHub Actions step
- name: Build services
  run: ./scripts/build/build-all.sh

- name: Run health checks
  run: |
    ./scripts/deploy/start-services.sh -d
    sleep 30
    ./scripts/deploy/health-check.sh
```

## 📞 Support

For issues related to Docker setup:

1. Check the troubleshooting section
2. Review service logs: `docker-compose logs [service-name]`
3. Verify all prerequisites are installed
4. Ensure Docker has sufficient resources allocated

---

*This documentation covers the complete Docker setup for the MediFlow application. Keep it updated as the application evolves.*
