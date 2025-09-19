# MediFlow Authentication Microservice 🔐

Advanced JWT-based authentication and user management service for the MediFlow platform, delivering secure and seamless access control with modern enterprise-grade features.

## Features

- **Advanced JWT Security**: Military-grade token-based authentication with 15-minute expiration
- **Smart User Registration**: Auto-generated MemberID (MEM + 6 digits) with real-time validation
- **Comprehensive User Management**: Full CRUD operations with role-based access control
- **Enterprise Password Security**: BCrypt encryption with strength validation
- **Real-time Validation**: Instant username, email, and MemberID availability checking
- **Production-Ready Security**: Spring Security with custom JWT filters and CORS configuration
- **Scalable Architecture**: Optimized for microservices deployment on Render.com
- **Health Monitoring**: Built-in actuator endpoints for production monitoring

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

## Encryption & Validation

- JWT token validation across all protected endpoints
- BCrypt password hashing
- CORS configuration for cross-origin requests
- Input validation and sanitization
- Automatic MemberID generation (MEM + 6 digits)

## Demo Users

```txt
Username: member1 | Password: password | Member ID: MEM123456
Username: member2 | Password: password | Member ID: MEM789012
Username: member3 | Password: password | Member ID: MEM345678
```

## Production Deployment

- **Render.com Ready**: Optimized Docker configuration for free tier deployment
- **Environment Variables**: Configurable JWT secrets and database connections
- **Health Checks**: `/actuator/health` endpoint for monitoring
- **Performance**: Optimized JVM settings for 512MB containers

## Security Features

- **HS512 Algorithm**: 512-bit JWT signatures for maximum security
- **Token Refresh**: Seamless authentication flow with refresh capabilities
- **Rate Limiting**: Built-in protection against brute force attacks
- **Audit Logging**: Comprehensive security event tracking
- **CORS Protection**: Configured for production domains
