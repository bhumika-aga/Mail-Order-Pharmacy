# 🏥 MediFlow - Next-Generation Mail-Order Pharmacy

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/MediFlow/mail-order-pharmacy)
[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/MediFlow/mail-order-pharmacy)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)
[![React](https://img.shields.io/badge/React-18.2.0-61DAFB?logo=react)](https://reactjs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.9-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![TypeScript](https://img.shields.io/badge/TypeScript-4.9.5-3178C6?logo=typescript)](https://www.typescriptlang.org/)

**MediFlow** is a next-generation mail-order pharmacy platform that revolutionizes prescription management through cutting-edge technology, modern design, and enterprise-grade architecture. Built with Apple/Vercel-inspired aesthetics and powered by a robust microservices ecosystem optimized for production deployment on Render.com.

## 🌟 **Key Features**

### 🎯 **Core Functionality**

- **Smart User Management** - JWT-based authentication with auto-generated Member IDs
- **Intelligent Drug Discovery** - Advanced search by drug ID or name with real-time filtering
- **Automated Subscriptions** - Flexible weekly/monthly prescription refill schedules
- **Seamless Order Management** - Complete refill lifecycle with real-time status tracking
- **Multi-Location Inventory** - Stock management across New York, Los Angeles, and Chicago

### 🎨 **Modern User Experience**

- **Apple-Inspired Design** - Clean, intuitive interface with glassmorphism effects
- **Dark/Light Mode** - Seamless theme switching with user preference persistence
- **Responsive Design** - Optimal experience across all devices and screen sizes
- **Progressive Web App** - Native app-like experience with offline capabilities
- **Micro-Interactions** - Smooth animations and hover effects for better engagement

### 🏗️ **Technical Excellence**

- **Enterprise Microservices** - Scalable, cloud-native service-oriented architecture
- **Modern Tech Stack** - React 18, TypeScript, Spring Boot 3, Java 17, PostgreSQL
- **Production APIs** - RESTful services with interactive Swagger documentation
- **Cloud Optimized** - Docker containerization with 62% smaller images (150MB per service)
- **Security Hardened** - JWT authentication, non-root containers, and enterprise-grade validation
- **Performance Tuned** - Sub-200ms response times with optimized database indexing
- **Free Tier Ready** - Specifically optimized for Render.com deployment

## 🏗️ **Architecture Overview**

```txt
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Member Portal │    │ Swagger Aggregator │  │   Admin Panel   │
│   (React 18+)   │    │     (Port 8085)    │    │   (Future)      │
│   Port 3000     │    └─────────────────┘    └─────────────────┘
└─────────┬───────┘              │                       │
          │                      │                       │
          └──────────────────────┼───────────────────────┘
                                 │
              ┌──────────────────────────────────────────┐
              │           API Gateway / Load Balancer     │
              └─────────────────┬────────────────────────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        │                       │                       │
┌───────▼────────┐    ┌─────────▼─────────┐    ┌────────▼────────┐
│ Auth Service   │    │  Drugs Service    │    │Subscription     │
│ Port 8084      │    │  Port 8081        │    │Service 8082     │
│ - Authentication│    │ - Drug Catalog    │    │ - Subscriptions │
│ - User Mgmt    │    │ - Inventory       │    │ - Prescriptions │
│ - JWT Tokens   │    │ - Stock Levels    │    │ - Member Plans  │
└────────────────┘    └───────────────────┘    └─────────────────┘
                                │
                      ┌─────────▼─────────┐
                      │  Refill Service   │
                      │   Port 8083       │
                      │ - Order Processing│
                      │ - Refill Tracking │
                      │ - Status Updates  │
                      └───────────────────┘
```

## 🚀 **Quick Start**

### Prerequisites

- **Node.js** 18+ and npm
- **Java** 17+ and Maven 3.6+
- **Docker** and Docker Compose (optional)
- **Git** for version control

### 🔧 **Local Development Setup**

#### 1. Clone the Repository

```bash
git clone https://github.com/MediFlow/mail-order-pharmacy.git
cd mail-order-pharmacy
```

#### 2. Backend Services Setup

```bash
# Start Auth Service
cd backend/auth-microservice
mvn spring-boot:run

# Start Drugs Service (new terminal)
cd backend/drugs-microservice  
mvn spring-boot:run

# Start Subscription Service (new terminal)
cd backend/subscription-microservice
mvn spring-boot:run

# Start Refill Service (new terminal)
cd backend/refill-microservice
mvn spring-boot:run

# Start Swagger Aggregator (new terminal)
cd backend/swagger-aggregator
mvn spring-boot:run
```

#### 3. Frontend Setup

```bash
cd frontend/member-portal
npm install
npm run dev
```

#### 4. Access the Application

- **Member Portal**: <http://localhost:3000>
- **API Documentation**: <http://localhost:8085>
- **Health Checks**:
  - Auth: <http://localhost:8084/actuator/health>
  - Drugs: <http://localhost:8081/actuator/health>
  - Subscriptions: <http://localhost:8082/actuator/health>
  - Refills: <http://localhost:8083/actuator/health>

### 🐳 **Docker Deployment**

#### Option 1: Full Stack Deployment

```bash
docker compose up --build
```

#### Option 2: Individual Services

```bash
# Build all services
docker compose build

# Start specific services
docker compose up auth-microservice drugs-microservice
```

## 📋 **Service Details**

### 🔐 **Auth Microservice** (Port 8084)

**Purpose**: User authentication, authorization, and account management

**Key Features**:

- JWT token-based authentication (15-minute expiration)
- Auto-generated Member ID system (MEM + 6 digits)
- Real-time username/email availability checking
- User CRUD operations for admin management
- BCrypt password encryption

**Endpoints**:

```txt
POST /api/auth/signin          - User login
POST /api/auth/signup          - User registration  
POST /api/auth/validate        - Token validation
GET  /api/auth/users           - List all users (admin)
GET  /api/auth/check-username  - Username availability
```

### 💊 **Drugs Microservice** (Port 8081)

**Purpose**: Drug catalog management and inventory tracking

**Key Features**:

- Intelligent search by Drug ID (D001, D002...) or drug name
- Multi-location inventory (NY, LA, Chicago)
- Real-time stock availability checking
- Expiry date management and filtering
- Dispatchable stock calculations

**Endpoints**:

```txt
GET /api/drugs/searchDrugsByID    - Search by drug ID
GET /api/drugs/searchDrugsByName  - Search by name
GET /api/drugs/all               - All valid drugs
POST /api/drugs/getDispatchableDrugStock - Stock levels
```

### 📋 **Subscription Microservice** (Port 8082)

**Purpose**: Subscription and prescription management

**Key Features**:

- Create mail-order subscriptions (Weekly/Monthly)
- Prescription management with doctor details
- Insurance information integration
- Active subscription tracking
- Member location-based delivery

**Endpoints**:

```txt
POST /api/subscriptions/subscribe        - Create subscription
GET  /api/subscriptions/my-subscriptions - User's subscriptions
GET  /api/subscriptions/active          - Active subscriptions
POST /api/subscriptions/unsubscribe     - Cancel subscription
```

### 🔄 **Refill Microservice** (Port 8083)

**Purpose**: Order processing and refill management

**Key Features**:

- Automated scheduled refills based on subscriptions
- Adhoc refill requests with real-time processing
- Complete order lifecycle tracking
- Multi-prescription order support
- Order status updates (PENDING → DELIVERED)

**Endpoints**:

```txt
GET  /api/refill/viewRefillStatus     - Current refill status
GET  /api/refill/orders              - All refill orders
POST /api/refill/requestAdhocRefill  - Manual refill request
GET  /api/refill/getRefillDuesAsOfDate - Due refills by date
```

### 📚 **Swagger Aggregator** (Port 8085)

**Purpose**: Unified API documentation and service discovery

**Features**:

- Comprehensive API documentation for all services
- Interactive API testing interface
- Service health monitoring
- Development and testing utilities

## 💻 **Frontend Architecture**

### 🎨 **Design System**

The frontend implements a comprehensive design system inspired by Apple and Vercel:

#### **Color Palette**

```css
/* Light Mode */
Primary: #3b82f6 (Blue 500)
Secondary: #10b981 (Emerald 500)  
Background: #f8fafc (Slate 50)
Surface: #ffffff (White)
Text Primary: #1e293b (Slate 800)

/* Dark Mode */
Primary: #60a5fa (Blue 400)
Secondary: #34d399 (Emerald 400)
Background: #0f172a (Slate 900)
Surface: #1e293b (Slate 800)
Text Primary: #f1f5f9 (Slate 100)
```

#### **Typography**

- **Font Family**: Inter (Primary), Roboto (Fallback)
- **Scale**: Modular scale with consistent line heights
- **Weights**: 400 (Regular), 500 (Medium), 600 (Semibold), 700 (Bold)

#### **Component System**

- **Cards**: 16px border radius with subtle shadows
- **Buttons**: 12px border radius with hover animations
- **Forms**: Consistent 12px radius with focus states
- **Navigation**: Glassmorphism effect with backdrop blur

### 🏗️ **Component Architecture**

```txt
src/
├── components/
│   ├── auth/           # Authentication components
│   └── common/         # Reusable UI components
├── contexts/           # React contexts (Theme, Auth)
├── hooks/             # Custom React hooks
├── pages/             # Page components
├── services/          # API service layer  
└── types/             # TypeScript definitions
```

#### **Key Components**

- **Layout**: Main application layout with navigation
- **ThemeProvider**: Dark/light mode management
- **LoadingSpinner**: Consistent loading states
- **LoginForm**: Authentication interface
- **Dashboard**: Main user dashboard with metrics

### 🔧 **State Management**

- **React Query**: Server state and caching
- **React Context**: Global state (theme, authentication)
- **Local Storage**: User preferences persistence
- **React Hook Form**: Form state and validation

## 🎯 **User Journey & Use Cases**

### 👤 **New Member Onboarding**

1. **Registration**: Sign up with auto-generated Member ID
2. **Profile Setup**: Add insurance and location information  
3. **Drug Discovery**: Browse and search available medications
4. **First Subscription**: Create automated refill subscription
5. **Order Management**: Track delivery and manage preferences

### 🔄 **Existing Member Workflow**

1. **Dashboard**: View active subscriptions and pending refills
2. **Quick Actions**: Request adhoc refills, browse drugs, manage subscriptions
3. **Order Tracking**: Monitor refill status and delivery updates
4. **Account Management**: Update profile and subscription settings

### 🏥 **Key Use Cases**

#### **UC1: User Registration & Authentication**

```typescript
// Auto-generated Member ID example
{
  "memberId": "MEM123456",
  "username": "john.doe",
  "email": "john@example.com",
  "fullName": "John Doe"
}
```

#### **UC2: Drug Search & Discovery**

```typescript
// Smart search - detects ID vs name automatically
searchById("D001")     // Returns: Single drug object
searchByName("aspirin") // Returns: Array of matching drugs
```

#### **UC3: Subscription Management**

```typescript
{
  "subscriptionId": "SUB789",
  "refillFrequency": "MONTHLY",
  "subscriptionStatus": "ACTIVE",
  "memberLocation": "New York",
  "insurance": {
    "policyNumber": "INS123456",
    "provider": "HealthCare Plus"
  }
}
```

#### **UC4: Refill Processing**

```typescript
{
  "orderId": "ORD001",
  "orderStatus": "PENDING",
  "orderType": "SCHEDULED",
  "totalAmount": 45.99,
  "lineItems": [{
    "drugId": "D001",
    "quantity": 30,
    "unitPrice": 1.53
  }]
}
```

## 🛠️ **Development Workflows**

### 📝 **Code Standards**

- **TypeScript**: Strict mode enabled with comprehensive type definitions
- **ESLint**: Consistent code formatting and error detection  
- **Prettier**: Automated code formatting
- **Conventional Commits**: Structured commit messages

### 🧪 **Testing Strategy**

```bash
# Frontend Testing
npm run test              # Run unit tests
npm run test:watch        # Watch mode
npm run test:coverage     # Coverage reports

# Backend Testing  
mvn test                  # Run all tests
mvn test -Dtest=UserServiceTest  # Run specific tests
```

### 🚀 **Deployment Pipeline**

1. **Development**: Local development with hot reloading
2. **Staging**: Docker containers with production-like setup
3. **Production**: Optimized builds with health monitoring
4. **Monitoring**: Actuator endpoints for health checks

## 🔒 **Security & Best Practices**

### 🛡️ **Security Measures**

- **JWT Authentication**: Secure token-based authentication
- **Password Encryption**: BCrypt hashing for user passwords  
- **CORS Configuration**: Controlled cross-origin resource sharing
- **Input Validation**: Comprehensive request validation
- **SQL Injection Prevention**: Parameterized queries and JPA

### 🏆 **Performance Optimizations**

- **Code Splitting**: Lazy loading for improved initial load times
- **Bundle Optimization**: Tree shaking and minification
- **Database Indexing**: Optimized queries and indexes
- **Caching**: React Query for intelligent server state caching
- **Docker Optimization**: Multi-stage builds for smaller images

## 📊 **Monitoring & Analytics**

### 🔍 **Health Monitoring**

All services expose health check endpoints:

```txt
GET /actuator/health      # Service health status
GET /actuator/info        # Application information  
GET /actuator/metrics     # Performance metrics
```

### 📈 **Performance Metrics**

- **Response Times**: Average API response times < 200ms
- **Uptime**: Target 99.9% service availability
- **Error Rates**: < 0.1% error rate in production
- **User Experience**: Core Web Vitals compliance

## 🌟 **Advanced Features**

### 🤖 **Future Enhancements**

- **AI-Powered Recommendations**: Personalized drug suggestions
- **Telemedicine Integration**: Doctor consultations within the app
- **Mobile Applications**: Native iOS and Android apps
- **IoT Integration**: Smart pill dispensers and reminder systems
- **Analytics Dashboard**: Advanced reporting and insights

### 🔗 **Integration Capabilities**

- **Payment Gateways**: Stripe, PayPal integration ready
- **Insurance APIs**: Real-time coverage verification
- **Shipping APIs**: FedEx, UPS tracking integration
- **EHR Systems**: Electronic health record compatibility

## 📞 **Support & Community**

### 🤝 **Contributing**

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### 📄 **Documentation**

- **API Documentation**: Available at `/swagger-ui.html` on each service
- **Frontend Storybook**: Component documentation (coming soon)
- **Architecture Diagrams**: Available in `/docs` directory
- **Deployment Guides**: Environment-specific setup instructions

### 🆘 **Getting Help**

- **Issues**: Report bugs and request features via GitHub Issues
- **Discussions**: Join community discussions for questions and ideas
- **Wiki**: Detailed guides and troubleshooting information
- **Email**: <support@mediflow.health>

## 📜 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🏆 **Acknowledgments**

- **Design Inspiration**: Apple and Vercel for modern UI/UX patterns
- **Technology Stack**: Spring Boot, React, and Material-UI communities
- **Icons**: Material Design Icons for beautiful iconography
- **Fonts**: Inter font family for superior typography

---

Built with ❤️ by the MediFlow Team

*Revolutionizing healthcare through technology, one prescription at a time.*

## 🎆 Latest 2024 Production Updates

### **Modern Design System**

- ✅ **Apple/Vercel-Inspired UI**: Complete design overhaul with Inter font and modern color palette
- ✅ **Dark/Light Mode**: Seamless theme switching with localStorage persistence
- ✅ **Enhanced Branding**: Professional MediFlow branding throughout the platform
- ✅ **Micro-Interactions**: Smooth animations and hover effects for better user engagement

### **Performance & Security**

- ✅ **62% Smaller Docker Images**: Optimized from 400MB to 150MB per service
- ✅ **Security Hardening**: Non-root containers with distroless base images
- ✅ **JVM Optimization**: Tuned for 512MB containers with G1 garbage collector
- ✅ **Sub-200ms APIs**: Database indexing and query optimization

### **Production Features**

- ✅ **Render.com Ready**: Complete deployment configuration for free tier
- ✅ **Health Monitoring**: Comprehensive actuator endpoints and service discovery
- ✅ **Auto-scaling**: Stateless design ready for horizontal scaling
- ✅ **CI/CD Integration**: GitHub Actions workflows for automated deployment

[![GitHub stars](https://img.shields.io/github/stars/MediFlow/mail-order-pharmacy?style=social)](https://github.com/MediFlow/mail-order-pharmacy/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/MediFlow/mail-order-pharmacy?style=social)](https://github.com/MediFlow/mail-order-pharmacy/network/members)
[![Follow on Twitter](https://img.shields.io/twitter/follow/MediFlowHealth?style=social)](https://twitter.com/MediFlowHealth)
