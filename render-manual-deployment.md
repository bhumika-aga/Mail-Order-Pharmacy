# MediFlow Manual Deployment - Alternative Method 🏥

**Note**: For most users, we recommend the [Quick Deploy Guide](QUICK_DEPLOY.md). Use this manual method if you need more control over the deployment process.

Complete step-by-step manual deployment guide for MediFlow on Render.com's free tier with optimized configuration for performance and cost-efficiency.

## Step 1: Create PostgreSQL Database

1. Login to [Render Dashboard](https://dashboard.render.com)
2. Click "New" → "PostgreSQL"
3. Configuration:
   - Name: `mediflow-database`
   - Database: `mediflow`
   - User: `mediflow_user`
   - Plan: Free
   - Region: Oregon
4. Save the **Internal Database URL** from the database info page
5. **Important**: Convert the Internal Database URL to JDBC format:
   - Original: `postgresql://username:password@host/database`
   - JDBC Format: `jdbc:postgresql://username:password@host:5432/database`
   - Add `jdbc:` prefix and `:5432` port number

## Step 2: Deploy Backend Microservices

### Auth Microservice

1. New → Web Service
2. Connect GitHub repository
3. Configuration:

   ``` txt
   Name: mediflow-auth
   Runtime: Docker
   Dockerfile Path: ./Dockerfile.auth
   Plan: Free
   ```

4. Environment Variables:

   ``` txt
   SPRING_PROFILES_ACTIVE=prod
   JWT_SECRET=mediFlowSecureSecretKeyForProductionDeploymentWithSufficientLengthForHS512Algorithm2024
   JWT_EXPIRATION=900000
   DATABASE_URL=jdbc:postgresql://dpg-d2a7qjadbo4c73b2j4n0-a.oregon-postgres.render.com:5432/mediflow?user=mediflow_user&password=p9b7x3MCz3VJFZycM6AagAu4023WzX8Z&sslmode=require
   ```

### Drugs Microservice

1. New → Web Service
2. Configuration:

   ``` txt
   Name: drugs-microservice
   Runtime: Docker
   Dockerfile Path: ./Dockerfile.drugs
   Plan: Free
   ```

3. Environment Variables:

   ``` txt
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=jdbc:postgresql://dpg-d2a7qjadbo4c73b2j4n0-a.oregon-postgres.render.com:5432/mediflow?user=mediflow_user&password=p9b7x3MCz3VJFZycM6AagAu4023WzX8Z&sslmode=require
   AUTH_SERVICE_URL=https://mediflow-auth.onrender.com
   JWT_SECRET=mediFlowSecureSecretKeyForProductionDeploymentWithSufficientLengthForHS512Algorithm2024
   JWT_EXPIRATION=900000
   ```

### Subscription Microservice

1. New → Web Service
2. Configuration:

   ``` txt
   Name: subscription-microservice
   Runtime: Docker
   Dockerfile Path: ./Dockerfile.subscription
   Plan: Free
   ```

3. Environment Variables:

   ``` txt
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=jdbc:postgresql://dpg-d2a7qjadbo4c73b2j4n0-a.oregon-postgres.render.com:5432/mediflow?user=mediflow_user&password=p9b7x3MCz3VJFZycM6AagAu4023WzX8Z&sslmode=require
   AUTH_SERVICE_URL=https://mediflow-auth.onrender.com
   DRUGS_SERVICE_URL=https://drugs-microservice.onrender.com
   JWT_SECRET=mediFlowSecureSecretKeyForProductionDeploymentWithSufficientLengthForHS512Algorithm2024
   JWT_EXPIRATION=900000
   ```

### Refill Microservice

1. New → Web Service
2. Configuration:

   ``` txt
   Name: refill-microservice
   Runtime: Docker
   Dockerfile Path: ./Dockerfile.refill
   Plan: Free
   ```

3. Environment Variables:

   ``` txt
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=jdbc:postgresql://dpg-d2a7qjadbo4c73b2j4n0-a.oregon-postgres.render.com:5432/mediflow?user=mediflow_user&password=p9b7x3MCz3VJFZycM6AagAu4023WzX8Z&sslmode=require
   AUTH_SERVICE_URL=https://mediflow-auth.onrender.com
   SUBSCRIPTION_SERVICE_URL=https://subscription-microservice.onrender.com
   JWT_SECRET=mediFlowSecureSecretKeyForProductionDeploymentWithSufficientLengthForHS512Algorithm2024
   JWT_EXPIRATION=900000
   ```

### Swagger Aggregator

1. New → Web Service
2. Configuration:

   ``` txt
   Name: swagger-aggregator
   Runtime: Docker
   Dockerfile Path: ./Dockerfile.swagger
   Plan: Free
   ```

3. Environment Variables:

   ``` txt
   SPRING_PROFILES_ACTIVE=prod
   AUTH_SERVICE_URL=https://mediflow-auth.onrender.com
   DRUGS_SERVICE_URL=https://drugs-microservice.onrender.com
   SUBSCRIPTION_SERVICE_URL=https://subscription-microservice.onrender.com
   REFILL_SERVICE_URL=https://refill-microservice.onrender.com
   ```

## Step 3: Deploy Frontend

### React Member Portal

1. New → Static Site
2. Configuration:

   ``` txt
   Name: member-portal
   Build Command: cd frontend/member-portal && npm install && npm run build
   Publish Directory: frontend/member-portal/dist
   ```

3. Environment Variables:

   ``` txt
   VITE_AUTH_SERVICE_URL=https://mediflow-auth.onrender.com
   VITE_DRUGS_SERVICE_URL=https://drugs-microservice.onrender.com
   VITE_SUBSCRIPTION_SERVICE_URL=https://subscription-microservice.onrender.com
   VITE_REFILL_SERVICE_URL=https://refill-microservice.onrender.com
   VITE_SWAGGER_URL=https://swagger-aggregator.onrender.com
   ```

## Final URLs

- Frontend: <https://member-portal.onrender.com>
- Auth API: <https://mediflow-auth.onrender.com>
- Drugs API: <https://drugs-microservice.onrender.com>
- Subscriptions API: <https://subscription-microservice.onrender.com>
- Refills API: <https://refill-microservice.onrender.com>
- Swagger UI: <https://swagger-aggregator.onrender.com>
