# Subscription Microservice

Subscription management and prescription handling service for the Mail-Order Pharmacy system.

## Features

- **Subscription Management**: Create, update, and cancel mail-order subscriptions
- **Prescription Tracking**: Manage member prescriptions and drug details
- **Refill Scheduling**: Track refill due dates and frequencies
- **Insurance Integration**: Handle insurance information and claims
- **JWT Security**: Protected endpoints with token validation
- **Member-specific Access**: Secure data access control per member

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

Includes:

- 5 member prescriptions (PRES001-PRES005)
- 5 member subscriptions (SUB001-SUB005)
- Various frequencies and statuses
- Insurance and doctor information

## Security

- JWT token validation for all endpoints
- Member-specific data access control
- Spring Security configuration
- Protected database console access

## External Dependencies

- **Auth Service**: For JWT token validation
- **Drugs Service**: For drug information validation
