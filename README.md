# Mail-Order Pharmacy System

A comprehensive full-stack application for pharmaceutical benefits management using microservices architecture with Spring Boot backend services and React 18+ TypeScript frontend.

## 🏗️ System Architecture

```
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
- **Docker & Docker Compose**
- **Maven 3.8+**

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
```

### Option 2: Manual Setup

```bash
# 1. Setup databases
./scripts/setup/setup-project.sh

# 2. Start backend services (in separate terminals)
cd backend/auth-microservice && mvn spring-boot:run
cd backend/drugs-microservice && mvn spring-boot:run
cd backend/subscription-microservice && mvn spring-boot:run
cd backend/refill-microservice && mvn spring-boot:run
cd backend/swagger-aggregator && mvn spring-boot:run

# 3. Start frontend
cd frontend/member-portal
npm install
npm start
```

## 🌐 Service URLs

| Service | URL | Description |
|---------|-----|-------------|
| Member Portal | http://localhost:3000 | React frontend application |
| Auth Service | http://localhost:8084 | Authentication & authorization |
| Drugs Service | http://localhost:8081 | Drug inventory management |
| Subscription Service | http://localhost:8082 | Subscription management |
| Refill Service | http://localhost:8083 | Refill processing |
| Swagger UI | http://localhost:8085 | Unified API documentation |

## 👤 Demo Credentials

```
Username: member1 | Password: password
Username: member2 | Password: password  
Username: member3 | Password: password
```

## 📚 API Documentation

Access comprehensive API documentation at:
- **Unified Swagger UI**: http://localhost:8085/swagger-ui.html
- **Individual Service Docs**: http://localhost:[port]/swagger-ui.html

### Key API Endpoints

#### Authentication Service (8084)
```
POST /api/auth/signin - User login
POST /api/auth/validate - Token validation
```

#### Drugs Service (8081)
```
GET  /api/drugs/searchDrugsByID?drugId={id} - Search drug by ID
GET  /api/drugs/searchDrugsByName?drugName={name} - Search drugs by name
POST /api/drugs/getDispatchableDrugStock - Get available stock
```

#### Subscription Service (8082)
```
POST /api/subscriptions/subscribe - Create subscription
POST /api/subscriptions/unsubscribe - Cancel subscription
GET  /api/subscriptions/my-subscriptions - Get member subscriptions
```

#### Refill Service (8083)
```
GET  /api/refill/viewRefillStatus - Get refill status
GET  /api/refill/getRefillDuesAsOfDate - Get due refills
POST /api/refill/requestAdhocRefill - Request adhoc refill
```

## 🧪 Testing

### Backend Testing
```bash
# Run all tests
mvn clean test

# Run tests with coverage
mvn clean test jacoco:report

# View coverage reports
open target/site/jacoco/index.html
```

### Frontend Testing
```bash
cd frontend/member-portal

# Run tests
npm test

# Run tests with coverage
npm test -- --coverage
```

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

```
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
- **CORS Configuration**: Secure cross-origin resource sharing
- **Input Validation**: Comprehensive request validation
- **SQL Injection Protection**: JPA/Hibernate ORM usage
- **XSS Protection**: React built-in protections

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
2. Implement backend service changes
3. Update frontend components
4. Add comprehensive tests
5. Update API documentation
6. Submit pull request

### Running in IDE
- **Backend**: Import as Maven projects in IntelliJ/Eclipse
- **Frontend**: Open in VS Code with TypeScript support
- **Database**: H2 Console available at `/h2-console`

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
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For questions and support:
- Create an issue on GitHub
- Check API documentation at http://localhost:8085
- Review service logs for debugging

## 🔄 Version History

- **v1.0.0**: Initial release with full microservices architecture
  - JWT authentication
  - Drug inventory management
  - Subscription services
  - Refill processing
  - React frontend
  - Docker containerization