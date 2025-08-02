# Manual Render.com Deployment Guide

Since render.yaml has compatibility issues, follow this step-by-step manual deployment approach:

## Step 1: Create PostgreSQL Database

1. Login to [Render Dashboard](https://dashboard.render.com)
2. Click "New" → "PostgreSQL"
3. Configuration:
   - Name: `pharmacy-db`
   - Database: `pharmacy`
   - User: `pharmacy_user`
   - Plan: Free
   - Region: Oregon
4. Save the **Internal Database URL** from the database info page

## Step 2: Deploy Backend Microservices

### Auth Microservice

1. New → Web Service
2. Connect GitHub repository
3. Configuration:

   ``` txt
   Name: auth-microservice
   Runtime: Docker
   Dockerfile Path: ./Dockerfile.auth
   Plan: Free
   ```

4. Environment Variables:

   ``` txt
   SPRING_PROFILES_ACTIVE=prod
   JWT_SECRET=pharmacySecretKeyForJWTTokenGenerationOnRender2024
   JWT_EXPIRATION=900000
   DATABASE_URL=[Your PostgreSQL Internal Database URL]
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
   DATABASE_URL=[Your PostgreSQL Internal Database URL]
   AUTH_SERVICE_URL=https://auth-microservice.onrender.com
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
   DATABASE_URL=[Your PostgreSQL Internal Database URL]
   AUTH_SERVICE_URL=https://auth-microservice.onrender.com
   DRUGS_SERVICE_URL=https://drugs-microservice.onrender.com
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
   DATABASE_URL=[Your PostgreSQL Internal Database URL]
   AUTH_SERVICE_URL=https://auth-microservice.onrender.com
   SUBSCRIPTION_SERVICE_URL=https://subscription-microservice.onrender.com
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
   AUTH_SERVICE_URL=https://auth-microservice.onrender.com
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
   VITE_API_BASE_URL=https://auth-microservice.onrender.com
   VITE_DRUGS_SERVICE_URL=https://drugs-microservice.onrender.com
   VITE_SUBSCRIPTION_SERVICE_URL=https://subscription-microservice.onrender.com
   VITE_REFILL_SERVICE_URL=https://refill-microservice.onrender.com
   VITE_SWAGGER_URL=https://swagger-aggregator.onrender.com
   ```

## Final URLs

- Frontend: <https://member-portal.onrender.com>
- Auth API: <https://auth-microservice.onrender.com>
- Drugs API: <https://drugs-microservice.onrender.com>
- Subscriptions API: <https://subscription-microservice.onrender.com>
- Refills API: <https://refill-microservice.onrender.com>
- Swagger UI: <https://swagger-aggregator.onrender.com>
