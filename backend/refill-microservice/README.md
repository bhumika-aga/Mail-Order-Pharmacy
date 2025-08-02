# Refill Microservice

This microservice handles refill orders and subscription management for the Mail-Order Pharmacy system.

## Features

- **Refill Status Management**: Track the status of refill orders
- **Adhoc Refill Orders**: Create on-demand refill orders
- **Subscription Integration**: Get refill dues from subscription service
- **Drug Stock Management**: Integration with drug inventory service
- **JWT Authentication**: Secure API endpoints with JWT tokens
- **Swagger Documentation**: Comprehensive API documentation

## API Endpoints

### Authentication Required

All endpoints require a valid JWT token in the Authorization header: `Bearer <token>`

### Main Endpoints

1. **GET /api/refill/viewRefillStatus**
    - Get the latest refill status for the authenticated member
    - Returns: RefillStatusResponse with order details and line items

2. **GET /api/refill/getRefillDuesAsOfDate**
    - Get subscriptions due for refill as of a specific date
    - Query Parameter: `dueDate` (YYYY-MM-DD format)
    - Returns: List of RefillDueResponse

3. **POST /api/refill/requestAdhocRefill**
    - Create a new adhoc refill order
    - Body: AdhocRefillRequest with member details and prescriptions
    - Returns: AdhocRefillResponse with order confirmation

4. **GET /api/refill/orders**
    - Get all refill orders for the authenticated member
    - Returns: List of RefillStatusResponse

5. **PUT /api/refill/orders/{refillOrderId}/status**
    - Update refill order status (Admin operation)
    - Path Parameter: `refillOrderId`
    - Query Parameter: `status` (OrderStatus enum)
    - Returns: Updated RefillStatusResponse

## Configuration

The service runs on port **8083** and uses:

- H2 Database for data storage
- JWT for authentication
- Integration with Subscription Service (port 8082)
- Integration with Drugs Service (port 8081)

## Running the Service

1. **Build the project:**

   ```bash
   mvn clean install
   ```

2. **Run the application:**

   ```bash
   mvn spring-boot:run
   ```

3. **Access Swagger UI:**
    - URL: <http://localhost:8083/swagger-ui.html>

4. **Access H2 Console:**
    - URL: <http://localhost:8083/h2-console>
    - JDBC URL: jdbc:h2:file:./data/refill-pharmacy
    - Username: sa
    - Password: password

## Data Model

### RefillOrder

- refillOrderId (Primary Key)
- memberId
- orderDate
- memberLocation
- orderStatus (PENDING, CONFIRMED, IN_PROGRESS, SHIPPED, DELIVERED, CANCELLED)
- orderType (SCHEDULED, ADHOC)
- subscriptionId (for scheduled orders)
- deliveryDate
- trackingNumber

### RefillOrderLineItem

- lineItemId (Primary Key)
- drugCode
- drugName
- quantity
- prescriptionId
- unitPrice
- totalPrice
- refillOrderId (Foreign Key)

## External Dependencies

- **Subscription Service**: For retrieving subscription and prescription details
- **Drugs Service**: For drug information and stock management
- **Auth Service**: For JWT token validation

## Sample Data

The service includes sample refill orders and line items for testing purposes.

## Security

- JWT token validation for all endpoints
- Member-specific data access control
- CORS configuration for cross-origin requests
- H2 console access in development mode

## Testing

Run tests with:

```bash
mvn test
```

The test configuration uses an in-memory H2 database.
