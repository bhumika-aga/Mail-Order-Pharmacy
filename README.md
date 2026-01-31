# 🏥 MediFlow - Mail-Order Pharmacy System

![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)
![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
[![React](https://img.shields.io/badge/React-18.2.0-61DAFB?logo=react)](https://reactjs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![TypeScript](https://img.shields.io/badge/TypeScript-4.9.5-3178C6?logo=typescript)](https://www.typescriptlang.org/)
[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk)](https://openjdk.org/)

A comprehensive mail-order pharmacy management system built with modern microservices architecture. This platform streamlines prescription refills, inventory management, and customer subscriptions through automated workflows and intelligent drug discovery.

## 🌟 Key Features

### Core Functionality

- **🔐 Smart Authentication** - JWT-based auth with auto-generated Member IDs (MEM + 6 digits)
- **💊 Drug Management** - Intelligent search by ID or name, multi-location inventory tracking
- **📋 Subscription System** - Automated weekly/monthly prescription refill schedules
- **🔄 Order Processing** - Complete refill lifecycle from pending to delivered
- **📍 Multi-Location** - Inventory management across New York, Los Angeles, and Chicago
- **📊 Real-time Tracking** - Live order status updates and stock availability

### Technical Highlights

- **Microservices Architecture** - Independent, scalable services
- **Modern Stack** - React 18, TypeScript, Spring Boot 3.5, Java 17
- **RESTful APIs** - Complete Swagger/OpenAPI documentation
- **Security First** - BCrypt password hashing, JWT tokens, CORS protection
- **Docker Ready** - Optimized containers (150MB each, 62% size reduction)
- **H2/PostgreSQL** - H2 for development, PostgreSQL for production

## 🏗️ Architecture

The system consists of 5 microservices communicating via REST APIs:

```txt
┌─────────────────┐
│ Member Portal   │  React TypeScript SPA
│  (Port 5173)    │
└────────┬────────┘
         │
    ┌────┴────────────────────────────────┐
    │                                     │
┌───┴────────┐  ┌──────────┐  ┌─────────▼────┐  ┌──────────┐
│   Auth     │  │  Drugs   │  │Subscription  │  │ Refill   │
│  :8084     │  │  :8081   │  │   :8082      │  │  :8083   │
└────────────┘  └──────────┘  └──────────────┘  └──────────┘
         │             │              │               │
         └─────────────┴──────────────┴───────────────┘
                       │
              ┌────────▼────────┐
              │    Swagger      │
              │  Aggregator     │
              │    :8085        │
              └─────────────────┘
```

## 🚀 Quick Start

### Prerequisites

- **Java 17+** - JDK installation
- **Maven 3.6+** - Build tool
- **Node.js 18+** - with npm
- **Docker** (optional) - For containerized deployment

### Local Development

#### 1. Clone & Setup

```bash
git clone <your-repo-url>
cd Mail-Order-Pharmacy
```

#### 2. Start Backend Services

Open 4 separate terminals and run:

```bash
# Terminal 1: Auth Service
cd backend/auth-microservice
mvn spring-boot:run

# Terminal 2: Drugs Service
cd backend/drugs-microservice
mvn spring-boot:run

# Terminal 3: Subscription Service
cd backend/subscription-microservice
mvn spring-boot:run

# Terminal 4: Refill Service
cd backend/refill-microservice
mvn spring-boot:run
```

All services use H2 in-memory database for development (no setup required).

#### 3. Start Frontend

```bash
cd frontend/member-portal
npm install
npm run dev
```

#### 4. Access the Application

- **Member Portal**: <http://localhost:5173>
- **API Documentation**: <http://localhost:8085>
- **Health Checks**:
  - Auth: <http://localhost:8084/actuator/health>
  - Drugs: <http://localhost:8081/actuator/health>
  - Subscription: <http://localhost:8082/actuator/health>
  - Refill: <http://localhost:8083/actuator/health>

### Docker Deployment

```bash
# Start all services with Docker Compose
docker compose up --build

# Or build individual services
docker build -f Dockerfile.auth -t auth-service .
docker build -f Dockerfile.drugs -t drugs-service .
# ... and so on
```

## 📋 Service Details

### 🔐 Auth Microservice (Port 8084)

**Purpose**: User authentication and account management

**Features**:

- JWT authentication (15-minute token expiration)
- Automatic Member ID generation (MEM123456)
- Username/email uniqueness validation
- BCrypt password encryption
- User CRUD operations

**Key Endpoints**:

```txt
POST /api/auth/signin           - User login
POST /api/auth/signup           - User registration
POST /api/auth/validate         - Token validation
GET  /api/auth/users            - List all users
GET  /api/auth/check-username   - Check availability
```

### 💊 Drugs Microservice (Port 8081)

**Purpose**: Drug catalog and inventory management

**Features**:

- Dual-mode search (by ID like "D001" or by name)
- Multi-location stock tracking
- Expiry date management
- Dispatchable stock calculation

**Key Endpoints**:

```txt
GET  /api/drugs/searchDrugsByID         - Search by drug ID
GET  /api/drugs/searchDrugsByName       - Search by name
GET  /api/drugs/all                     - All valid drugs
POST /api/drugs/getDispatchableDrugStock - Stock by location
```

### 📋 Subscription Microservice (Port 8082)

**Purpose**: Prescription subscription management

**Features**:

- Weekly/Monthly refill frequency
- Prescription details (doctor, insurance)
- Active/Cancelled status tracking
- Next refill date calculation

**Key Endpoints**:

```txt
POST /api/subscriptions/subscribe       - Create subscription
GET  /api/subscriptions/my-subscriptions - User's subscriptions
GET  /api/subscriptions/active          - Active subscriptions
POST /api/subscriptions/unsubscribe     - Cancel subscription
```

### 🔄 Refill Microservice (Port 8083)

**Purpose**: Order processing and fulfillment

**Features**:

- Scheduled refills (automated)
- Adhoc refills (on-demand)
- Order lifecycle management
- Status tracking (PENDING → DELIVERED)

**Key Endpoints**:

```txt
GET  /api/refill/viewRefillStatus      - Current status
GET  /api/refill/orders               - All orders
POST /api/refill/requestAdhocRefill   - Manual refill
GET  /api/refill/getRefillDuesAsOfDate - Due refills
```

### 📚 Swagger Aggregator (Port 8085)

Unified API documentation for all microservices. Access interactive Swagger UI for testing and exploring all endpoints.

## 🗄️ Data Models

### Key Entities

#### User

- Member ID (auto-generated)
- Username, Email (unique)
- Password (BCrypt hashed)

#### Drug

- Drug ID (D001, D002...)
- Name, Manufacturer
- Unit Price, Medical Composition

#### Drug Location

- Location (NY, LA, Chicago)
- Quantity, Expiry Date

#### Subscription

- Subscription ID (SUB-uuid)
- Member ID, Drug ID
- Frequency (WEEKLY/MONTHLY)
- Status (ACTIVE/CANCELLED)

#### Refill Order

- Order ID (ORD-uuid)
- Order Type (SCHEDULED/ADHOC)
- Status (PENDING/PROCESSING/SHIPPED/DELIVERED)
- Line Items with quantities

### Database Strategy

- **Development**: H2 in-memory database (zero config)
- **Production**: PostgreSQL (separate DB per service)
- **Pattern**: Database per microservice (loose coupling)

## 🔒 Security

- **Authentication**: JWT tokens with HS512 signature
- **Password Storage**: BCrypt hashing (industry standard)
- **CORS**: Configured allowed origins
- **SQL Injection**: Prevented via JPA parameterized queries
- **Validation**: Input validation on all endpoints

## 🛠️ Development

### Build Commands

```bash
# Backend (Maven)
mvn clean install           # Build all
mvn spring-boot:run        # Run service
mvn test                   # Run tests

# Frontend (npm)
npm install                # Install dependencies
npm run dev               # Development server
npm run build             # Production build
npm run lint              # Code linting
```

### Project Structure

```txt
Mail-Order-Pharmacy/
├── backend/
│   ├── auth-microservice/
│   ├── drugs-microservice/
│   ├── subscription-microservice/
│   ├── refill-microservice/
│   └── swagger-aggregator/
├── frontend/
│   └── member-portal/
├── Dockerfile.* (5 files)
├── docker-compose.yml
├── render.yaml
└── Documentation files
```

## 📚 Documentation

- **[PROJECT_IMPLEMENTATION.md](PROJECT_IMPLEMENTATION.md)** - Implementation patterns and core concepts
- **[QUICK_DEPLOY.md](QUICK_DEPLOY.md)** - Quick deployment guide
- **[render-manual-deployment.md](render-manual-deployment.md)** - Render.com deployment instructions

## 🎯 Use Cases

### 1. New User Registration

1. User signs up → Auto-generated Member ID
2. JWT token issued for authentication
3. Browse drug catalog
4. Create subscription with prescription details
5. Automated refills scheduled

### 2. Scheduled Refills

- System checks subscriptions daily
- Creates orders for due refills
- Validates stock availability
- Updates next refill date

### 3. Adhoc Refills

- User requests immediate refill
- System validates subscription status
- Checks drug availability
- Creates order immediately

## 🚢 Deployment

### Docker Production

```bash
# Build optimized images
docker compose build

# Deploy with custom network
docker compose up -d

# View logs
docker compose logs -f
```

### Render.com Deployment

Uses `render.yaml` for infrastructure as code. All services deploy automatically with:

- Health checks enabled
- Auto-scaling configured
- Environment variables managed
- PostgreSQL database provisioned

See [render-manual-deployment.md](render-manual-deployment.md) for detailed steps.

## 🧪 Testing

### Backend Tests

```bash
mvn test                    # Run all tests
mvn test -Dtest=ClassName  # Run specific test
```

### Frontend Tests

```bash
npm run test               # Run tests
npm run test:coverage     # Coverage report
```

### Manual Testing

Use Swagger UI at <http://localhost:8085> for interactive API testing.

## 📈 Performance

- **API Response Times**: < 200ms average
- **Docker Image Size**: ~150MB per service (optimized)
- **Build Time**: ~1-1.5s per service
- **Startup Time**: ~1.5-2s per service

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is available under the MIT License.

## 👥 Authors

Built as a comprehensive mail-order pharmacy management system demonstrating modern microservices architecture, security best practices, and scalable design patterns.

---

**Tech Stack**: Spring Boot 3.5 • React 18 • TypeScript • Java 17 • Maven • Vite • H2/PostgreSQL • Docker

**Architecture**: Microservices • REST APIs • JWT Auth • Database per Service

**Deployment**: Docker • Render.com • Docker Compose
