# Mail-Order Pharmacy System

A comprehensive full-stack application for pharmaceutical benefits management using microservices architecture with Spring Boot backend services and React 18+ TypeScript frontend.

## ✅ Implementation Status

### 🎯 FULLY IMPLEMENTED & TESTED**

- All backend microservices are fully functional with JWT security
- React 18+ TypeScript frontend builds successfully
- Complete Docker containerization setup
- All compilation issues resolved and tested

## 🏗️ System Architecture

``` txt
┌─────────────────┐    ┌─────────────────────────────────────────┐
│   React 18+     │    │         Backend Microservices           │
│   TypeScript    │    │                                         │
│   Member Portal │◄───┤  Auth    Drugs    Subscription  Refill  │
│                 │    │  :8084   :8081    :8082        :8083   │
│   Port: 3000    │    │                                         │
└─────────────────┘    │         Swagger Aggregator              │
                       │              :8085                      │
                       └─────────────────────────────────────────┘
```

## 🚀 Features

### Backend Microservices

- **Authentication Service (Port 8084)**: JWT-based authentication with 15-minute token expiration
- **Drugs Service (Port 8081)**: Drug inventory management and availability checking
- **Subscription Service (Port 8082)**: Mail-order pharmacy subscription management
- **Refill Service (Port 8083)**: Prescription refill handling and payment tracking
- **Swagger Aggregator (Port 8085)**: Centralized API documentation

### Frontend Application

- **Member Portal**: React 18+ TypeScript application with Material-UI
- **Authentication**: JWT token management with protected routes
- **Drug Management**: Search and browse drug inventory
- **Subscription Management**: Create and manage mail-order subscriptions
- **Refill Management**: Track refills and request adhoc refills

## 📋 Prerequisites

- **Java 17** or higher
- **Node.js 18+** and npm
- **Docker & Docker Compose** (optional for containerized deployment)
- **Maven 3.8+** (Maven wrapper included in each microservice)

## 🔧 Quick Start

### Option 1: Docker Compose (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd Mail-Order-Pharmacy

# Build and start all services
docker-compose up --build

# Or run in background
docker-compose up -d --build

# Stop all services
docker-compose down

# Rebuild specific service
docker-compose up --build auth-microservice
```

### Option 2: Manual Setup

```bash
# 1. Setup databases
./scripts/setup/setup-project.sh

# 2. Start backend services (in separate terminals)
# Note: Use Maven wrapper (./mvnw) if Maven is not installed globally
cd backend/auth-microservice && ./mvnw spring-boot:run
cd backend/drugs-microservice && ./mvnw spring-boot:run
cd backend/subscription-microservice && ./mvnw spring-boot:run
cd backend/refill-microservice && ./mvnw spring-boot:run
cd backend/swagger-aggregator && ./mvnw spring-boot:run

# 3. Start frontend
cd frontend/member-portal
npm install
npm run dev  # For development mode
# OR
npm run build && npm run preview  # For production build and preview
```

## 🔨 Build Commands

### Backend Services

```bash
# Build all microservices
./scripts/build/build-all-services.sh

# Build individual microservice
cd backend/auth-microservice
./mvnw clean compile

# Build with tests
./mvnw clean test

# Package JAR files
./mvnw clean package

# Skip tests during packaging
./mvnw clean package -DskipTests

# Build with specific profile
./mvnw clean package -Dspring.profiles.active=prod
```

### Frontend Setup

```bash
cd frontend/member-portal

# Install dependencies
npm install

# Development build
npm run dev

# Production build
npm run build

# Preview production build
npm run preview

# Build with type checking
npm run build && npm run type-check

# Clean build artifacts
rm -rf dist node_modules
npm install
npm run build
```

## 🚀 Run Commands

### Development Mode

```bash
# Start all services in development mode
./scripts/deploy/start-dev.sh

# Start backend services only
./scripts/deploy/start-backend.sh

# Start frontend only
cd frontend/member-portal && npm run dev

# Start with hot reload (backend)
cd backend/auth-microservice
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"
```

### Production Mode

```bash
# Build and start production deployment
./scripts/deploy/deploy-prod.sh

# Start services with production profiles
cd backend/auth-microservice
./mvnw spring-boot:run -Dspring.profiles.active=prod

# Start frontend in production mode
cd frontend/member-portal
npm run build
npm run preview
```

### Service-Specific Commands

```bash
# Start individual microservices
cd backend/auth-microservice && ./mvnw spring-boot:run
cd backend/drugs-microservice && ./mvnw spring-boot:run
cd backend/subscription-microservice && ./mvnw spring-boot:run
cd backend/refill-microservice && ./mvnw spring-boot:run
cd backend/swagger-aggregator && ./mvnw spring-boot:run

# Start with custom ports
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8090"

# Start with debug mode
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

## 📦 Deploy Commands

### Docker Deployment

```bash
# Build and deploy all services
docker-compose up --build -d

# Deploy specific services
docker-compose up auth-microservice drugs-microservice -d

# Scale services
docker-compose up --scale auth-microservice=2 --scale drugs-microservice=3

# Update running services
docker-compose up --force-recreate auth-microservice

# View logs
docker-compose logs -f auth-microservice
docker-compose logs --tail=100 drugs-microservice

# Stop and remove containers
docker-compose down --volumes --remove-orphans
```

### Manual Deployment

```bash
# Create deployment directory
mkdir -p deploy/mail-order-pharmacy

# Build JAR files
./scripts/build/build-all-services.sh

# Copy JAR files to deployment directory
cp backend/*/target/*.jar deploy/mail-order-pharmacy/

# Copy frontend build
cp -r frontend/member-portal/dist deploy/mail-order-pharmacy/frontend

# Start services in background
cd deploy/mail-order-pharmacy
nohup java -jar auth-microservice-1.0.0.jar --server.port=8084 > auth.log 2>&1 &
nohup java -jar drugs-microservice-1.0.0.jar --server.port=8081 > drugs.log 2>&1 &
nohup java -jar subscription-microservice-1.0.0.jar --server.port=8082 > subscription.log 2>&1 &
nohup java -jar refill-microservice-1.0.0.jar --server.port=8083 > refill.log 2>&1 &
nohup java -jar swagger-aggregator-1.0.0.jar --server.port=8085 > swagger.log 2>&1 &

# Serve frontend (using Python for example)
cd frontend && python3 -m http.server 3000
```

### Environment-Specific Deployment

```bash
# Development deployment
export SPRING_PROFILES_ACTIVE=dev
./scripts/deploy/deploy-dev.sh

# Staging deployment
export SPRING_PROFILES_ACTIVE=staging
./scripts/deploy/deploy-staging.sh

# Production deployment
export SPRING_PROFILES_ACTIVE=prod
./scripts/deploy/deploy-prod.sh

# Cloud deployment
export SPRING_PROFILES_ACTIVE=cloud
docker-compose -f docker-compose.cloud.yml up -d
```

## 🌐 Service URLs

| Service | URL | Description |
|---------|-----|-------------|
| Member Portal | <http://localhost:3000> | React frontend application |
| Auth Service | <http://localhost:8084> | Authentication & authorization |
| Drugs Service | <http://localhost:8081> | Drug inventory management |
| Subscription Service | <http://localhost:8082> | Subscription management |
| Refill Service | <http://localhost:8083> | Refill processing |
| Swagger UI | <http://localhost:8085> | Unified API documentation |

## 👤 Demo Credentials

``` txt
Username: member1 | Password: password
Username: member2 | Password: password  
Username: member3 | Password: password
```

## 📚 API Documentation

Access comprehensive API documentation at:

- **Unified Swagger UI**: <http://localhost:8085/swagger-ui.html>
- **Individual Service Docs**: <http://localhost:[port]/swagger-ui.html>

### Key API Endpoints

#### Authentication Service (8084)

``` txt
POST /api/auth/signin - User login
POST /api/auth/validate - Token validation
```

#### Drugs Service (8081)

``` txt
GET  /api/drugs/searchDrugsByID?drugId={id} - Search drug by ID
GET  /api/drugs/searchDrugsByName?drugName={name} - Search drugs by name
POST /api/drugs/getDispatchableDrugStock - Get available stock
```

#### Subscription Service (8082)

``` txt
POST /api/subscriptions/subscribe - Create subscription
POST /api/subscriptions/unsubscribe - Cancel subscription
GET  /api/subscriptions/my-subscriptions - Get member subscriptions
```

#### Refill Service (8083)

``` txt
GET  /api/refill/viewRefillStatus - Get refill status
GET  /api/refill/getRefillDuesAsOfDate - Get due refills
POST /api/refill/requestAdhocRefill - Request adhoc refill
```

## 🧪 Testing

### Backend Testing

```bash
# Run tests for all microservices
cd backend/auth-microservice && ./mvnw clean test
cd backend/drugs-microservice && ./mvnw clean test
cd backend/subscription-microservice && ./mvnw clean test
cd backend/refill-microservice && ./mvnw clean test

# Run tests with coverage
./mvnw clean test jacoco:report

# View coverage reports
open target/site/jacoco/index.html
```

### Frontend Testing

```bash
cd frontend/member-portal

# Install dependencies (if not already done)
npm install

# Run tests
npm test

# Run tests with coverage
npm test -- --coverage

# Build for production
npm run build
```

## 🔧 Recent Fixes & Updates

### Backend Improvements

- ✅ Fixed AuthController compilation error with proper UserDetails casting
- ✅ Added missing Spring Security dependencies to drugs and subscription services
- ✅ Created complete security configuration for subscription microservice
- ✅ All microservices now compile and run successfully

### Frontend Improvements  

- ✅ Fixed TypeScript configuration for React 18+ compatibility
- ✅ Updated tsconfig.json with proper module resolution
- ✅ Added missing React page components (DrugsPage, SubscriptionsPage, RefillsPage)
- ✅ Fixed Vite build configuration and entry points
- ✅ Resolved all compilation errors - frontend builds successfully

## 🐳 Docker Configuration

### Development Profile

```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/pharmacy
```

### Cloud Profile

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:pharmacy
```

## 📁 Project Structure

``` txt
mail-order-pharmacy/
├── backend/
│   ├── auth-microservice/          # JWT authentication service
│   ├── drugs-microservice/         # Drug inventory service  
│   ├── subscription-microservice/  # Subscription management
│   ├── refill-microservice/       # Refill processing
│   ├── swagger-aggregator/        # API documentation
│   └── common-libs/               # Shared libraries
├── frontend/
│   └── member-portal/             # React TypeScript app
├── scripts/
│   ├── build/                     # Build automation
│   ├── deploy/                    # Deployment scripts
│   ├── db-init/                   # Database initialization
│   └── setup/                     # Project setup
├── docker-compose.yml             # Multi-service orchestration
└── README.md                      # This file
```

## 🔒 Security Features

- **JWT Authentication**: 15-minute token expiration with refresh capability
- **CORS Configuration**: Secure cross-origin resource sharing across all services
- **Input Validation**: Comprehensive request validation with Spring Boot Validation
- **SQL Injection Protection**: JPA/Hibernate ORM usage with parameterized queries
- **XSS Protection**: React built-in protections and Content Security Policy
- **Service-to-Service Security**: JWT token validation across all microservices

## 🏥 Database Schema

### Key Entities

#### Users (Auth Service)

- id, username, email, password, memberId, timestamps

#### Drugs (Drugs Service)  

- drugId, drugName, manufacturer, dates, pricing, composition

#### DrugLocations (Drugs Service)

- drugId, location, quantityAvailable

#### MemberPrescriptions (Subscription Service)

- prescriptionId, memberId, insurance info, drug details

#### MemberSubscriptions (Subscription Service)

- subscriptionId, memberId, prescription details, frequency, status

#### RefillOrders (Refill Service)

- refillOrderId, subscriptionId, quantities, payment status

## 🚦 Health Checks

Monitor service health:

```bash
# Check all services
./scripts/deploy/health-check.sh

# Individual service health
curl http://localhost:8084/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

## 🛠️ Development

### Adding New Features

1. Create feature branch
2. Implement backend service changes using Maven wrapper (./mvnw)
3. Update frontend components with proper TypeScript types
4. Add comprehensive tests for both backend and frontend
5. Update API documentation (Swagger automatically updates)
6. Update this README.md with any configuration changes
7. Submit pull request

### Running in IDE

- **Backend**: Import as Maven projects in IntelliJ/Eclipse (each microservice is independent)
- **Frontend**: Open in VS Code with TypeScript and React extensions
- **Database**: H2 Console available at `/h2-console` for each service
- **API Testing**: Use Swagger UI at <http://localhost:8085> for unified API testing

### Development Notes

- Each microservice uses Maven wrapper (`./mvnw`) so Maven installation is optional
- Frontend uses Vite for fast development builds
- All services support hot reload during development
- JWT tokens are required for all protected endpoints

## 📈 Performance Considerations

- **Parallel Processing**: Drug stock requests processed concurrently
- **Connection Pooling**: Database connection optimization
- **Caching**: Service-level caching for frequent queries
- **Pagination**: Large result set management
- **Lazy Loading**: Frontend code splitting and lazy loading

## 🔧 Configuration

### Environment Variables

```bash
# Backend services
JWT_SECRET=pharmacySecretKeyForJWTTokenGeneration
JWT_EXPIRATION=900000

# Frontend
REACT_APP_API_BASE_URL=http://localhost
```

### Database Configuration

```yaml
# Development
spring:
  datasource:
    url: jdbc:h2:file:./data/pharmacy
    
# Production  
spring:
  datasource:
    url: jdbc:h2:mem:pharmacy
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Ensure all backend services compile: `./mvnw clean compile` in each service directory
5. Ensure frontend builds successfully: `npm run build` in frontend/member-portal
6. Add tests for new functionality
7. Update this README.md if configuration changes are made
8. Submit a pull request

## 🚨 Troubleshooting

### Common Issues

**Backend Compilation Errors:**

- Ensure Java 17+ is installed
- Use Maven wrapper (`./mvnw`) instead of `mvn` if Maven not installed globally
- Check that Spring Security dependencies are included for JWT services

**Frontend Build Errors:**

- Ensure Node.js 18+ is installed
- Delete `node_modules` and run `npm install` if dependency issues occur
- Use `npm install --legacy-peer-deps` if peer dependency conflicts arise
- Ensure TypeScript files use `.tsx` extension for JSX content

**Docker Issues:**

- Use `docker compose` instead of `docker-compose` for newer Docker versions
- Ensure Docker Desktop is running before executing docker commands

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For questions and support:

- Create an issue on GitHub
- Check API documentation at <http://localhost:8085>
- Review service logs for debugging

## 🔄 Version History

- **v1.0.0**: Initial release with full microservices architecture ✅
  - JWT authentication with Spring Security
  - Drug inventory management
  - Subscription services with proper integrations
  - Refill processing with payment tracking
  - React 18+ TypeScript frontend with Vite
  - Docker containerization with health checks
  - All compilation issues resolved
  - Complete security implementation across services
  - Swagger API documentation aggregation
