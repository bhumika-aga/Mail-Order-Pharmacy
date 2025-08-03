# Drugs Microservice

Drug inventory management and stock availability service for the Mail-Order Pharmacy system.

## Features

- **Drug Inventory**: Comprehensive drug catalog with manufacturer details
- **Stock Management**: Location-based stock availability tracking
- **Search Functionality**: Search drugs by ID or name
- **Stock Dispatch**: Check dispatchable drug stock for orders
- **JWT Security**: Protected endpoints with token validation
- **Multi-location Support**: Stock tracking across multiple locations

## API Endpoints

### Drug Search

- `GET /api/drugs/searchDrugsByID?drugId={id}` - Search drug by ID
- `GET /api/drugs/searchDrugsByName?drugName={name}` - Search drugs by name

### Stock Management

- `POST /api/drugs/getDispatchableDrugStock` - Get available stock for dispatch
  - Body: DrugStockRequest with drug IDs and required quantities
  - Returns: DrugStockResponse with availability details

## Configuration

- **Port**: 8081
- **Database**: H2 (jdbc:h2:file:./data/drugs-pharmacy)
- **Authentication**: JWT token validation
- **CORS**: Configured for cross-origin requests

## Quick Start

```bash
# Build and run
./mvnw clean spring-boot:run

# Access H2 Console
open http://localhost:8081/h2-console

# View API Documentation
open http://localhost:8081/swagger-ui.html
```

## Data Model

### Drug Entity
- drugId (Primary Key)
- drugName
- manufacturer
- packagingDate
- expiryDate
- unitPrice
- composition

### DrugLocation Entity
- drugId (Foreign Key)
- location (New York, Los Angeles, Chicago)
- quantityAvailable

## Sample Data

Includes 10 drugs (D001-D010) with:
- Pricing information
- Composition details
- Stock quantities across 3 locations
- Various manufacturers

## Security

- JWT token validation for all endpoints
- Spring Security configuration
- Input validation and sanitization
- Protected database console access
