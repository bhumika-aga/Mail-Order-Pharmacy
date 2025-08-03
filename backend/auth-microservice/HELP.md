# Authentication Microservice

JWT-based authentication and user management service for the Mail-Order Pharmacy system.

## Features

- **JWT Authentication**: Secure token-based authentication with 15-minute expiration
- **User Registration**: Auto-generated MemberID with real-time validation
- **User Management**: Complete CRUD operations for user accounts
- **Password Security**: BCrypt encryption with validation
- **Real-time Validation**: Username, email, and MemberID availability checking
- **Spring Security**: Comprehensive security configuration

## API Endpoints

### Public Endpoints

- `POST /api/auth/signin` - User login
- `POST /api/auth/signup` - User registration
- `GET /api/auth/check-username/{username}` - Check username availability
- `GET /api/auth/check-email/{email}` - Check email availability
- `GET /api/auth/check-memberid/{memberId}` - Check member ID availability

### Protected Endpoints

- `POST /api/auth/validate` - Token validation
- `GET /api/auth/users` - Get all users (Admin)
- `PUT /api/auth/users/{id}` - Update user (Admin)
- `DELETE /api/auth/users/{id}` - Delete user (Admin)

## Configuration

- **Port**: 8084
- **Database**: H2 (jdbc:h2:file:./data/auth-pharmacy)
- **JWT Secret**: Configurable via environment variable
- **Token Expiration**: 15 minutes (900000 ms)

## Quick Start

```bash
# Build and run
./mvnw clean spring-boot:run

# Access H2 Console
open http://localhost:8084/h2-console

# View API Documentation
open http://localhost:8084/swagger-ui.html
```

## Security Features

- JWT token validation across all protected endpoints
- BCrypt password hashing
- CORS configuration for cross-origin requests
- Input validation and sanitization
- Automatic MemberID generation (MEM + 6 digits)

## Demo Users

```
Username: member1 | Password: password | Member ID: MEM001
Username: member2 | Password: password | Member ID: MEM002
Username: member3 | Password: password | Member ID: MEM003
```
