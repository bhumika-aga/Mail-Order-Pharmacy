# MediFlow Subscription Microservice 📋

Advanced subscription management and intelligent prescription handling service for the MediFlow platform, featuring automated refill scheduling and comprehensive insurance integration.

## Features

- **Intelligent Subscription Management**: Create, update, and cancel subscriptions with automated scheduling
- **Advanced Prescription Tracking**: Complete prescription lifecycle management with doctor integration
- **Smart Refill Scheduling**: Automated due date tracking with weekly, monthly, and quarterly frequencies
- **Comprehensive Insurance Integration**: Real-time insurance verification and claims processing
- **Enterprise Security**: JWT token validation with member-specific access control
- **Multi-Location Support**: Location-based delivery optimization
- **Production Ready**: Optimized for Render.com deployment with PostgreSQL
- **Health Monitoring**: Real-time service monitoring with actuator endpoints

## API Endpoints

### Subscription Operations

- `POST /api/subscriptions/subscribe` - Create new subscription
  - Body: SubscriptionRequest with prescription and frequency details
  - Returns: SubscriptionResponse with confirmation

- `POST /api/subscriptions/unsubscribe` - Cancel subscription
  - Body: UnsubscribeRequest with subscription ID
  - Returns: Confirmation message

- `GET /api/subscriptions/my-subscriptions` - Get member's subscriptions
  - Returns: List of SubscriptionResponse for authenticated member

### Refill Management

- `GET /api/subscriptions/refillDues?dueDate={date}` - Get refill dues as of date
  - Query Parameter: dueDate (YYYY-MM-DD format)
  - Returns: List of RefillDueResponse with subscription details

## Configuration

- **Port**: 8082
- **Database**: H2 (jdbc:h2:file:./data/subscription-pharmacy)
- **Authentication**: JWT token validation
- **External Services**: Integration with Auth and Drugs services

## Quick Start

```bash
# Build and run
./mvnw clean spring-boot:run

# Access H2 Console
open http://localhost:8082/h2-console

# View API Documentation
open http://localhost:8082/swagger-ui.html
```

## Data Model

### MemberPrescription

- prescriptionId (Primary Key)
- memberId
- drugCode
- drugName
- doctorName
- insuranceDetails
- prescriptionDate
- expiryDate

### MemberSubscription

- subscriptionId (Primary Key)
- memberId
- prescriptionId (Foreign Key)
- subscriptionFrequency (WEEKLY, MONTHLY, QUARTERLY)
- subscriptionStatus (ACTIVE, PAUSED, CANCELLED)
- nextRefillDate
- memberLocation

## Sample Data

Professionally curated test data includes:

- **5 Member Prescriptions** (PRES001-PRES005): Complete prescription details with realistic doctor information
- **5 Active Subscriptions** (SUB001-SUB005): Various frequencies (WEEKLY, MONTHLY, QUARTERLY)
- **Insurance Integration**: Real insurance provider data with policy numbers
- **Location Mapping**: Prescriptions mapped to New York, Los Angeles, and Chicago
- **Status Tracking**: ACTIVE, PAUSED, and CANCELLED subscription states

## Advanced Features

- **Automated Refill Alerts**: Intelligent notification system for upcoming refills
- **Insurance Verification**: Real-time coverage validation before prescription processing
- **Doctor Integration**: Secure prescription management with healthcare provider details
- **Compliance Tracking**: Full audit trail for regulatory compliance

## Security

- JWT token validation for all endpoints
- Member-specific data access control
- Spring Security configuration
- Protected database console access

## External Dependencies

- **Auth Service**: For JWT token validation
- **Drugs Service**: For drug information validation
