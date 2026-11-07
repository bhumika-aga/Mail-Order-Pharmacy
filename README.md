# 🏥 MediFlow - Mail-Order Pharmacy System

![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)
![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.2-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.3-61DAFB?logo=react)](https://reactjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-6.0-3178C6?logo=typescript)](https://www.typescriptlang.org/)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk)](https://openjdk.org/)
[![Supabase](https://img.shields.io/badge/Supabase-PostgreSQL-3ECF8E?logo=supabase)](https://supabase.com/)

A comprehensive mail-order pharmacy management system built with a **Microservices Architecture**. The platform streamlines prescription refills, drug inventory management, and customer subscriptions through automated workflows and intelligent drug discovery.

---

## 📐 Architecture & Design Concepts

### Microservices Pattern

MediFlow follows the **Microservices Architecture** pattern — each business capability is implemented as an independent, loosely coupled service that can be developed, deployed, and scaled independently.

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

### Key Architectural Decisions

| Decision                  | Approach                                                           | Rationale                                                                                                                       |
| ------------------------- | ------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------- |
| **Service Communication** | Synchronous REST over HTTP                                         | Simple, well-understood, adequate for the request/response pattern                                                              |
| **Authentication**        | Stateless JWT (HS512) with shared secret                           | Each service validates tokens independently — no central auth dependency at runtime                                             |
| **Database Strategy**     | Database-per-service (logical)                                     | Loose coupling: each service owns its tables; H2 for dev, PostgreSQL/Supabase for prod                                          |
| **DDL Management**        | Hibernate `ddl-auto: update` (prod) / `create-drop` (dev)          | Dev gets clean state on each restart; prod preserves data and schema                                                            |
| **API Documentation**     | OpenAPI 3.0 via springdoc; a thin Swagger UI host aggregates specs | Each service self-documents; one Swagger UI switches between all service specs via `springdoc.swagger-ui.urls` (no custom code) |
| **Row Level Security**    | Supabase RLS enabled on all tables                                 | Prevents direct API access to data via Supabase's PostgREST layer                                                               |

### Inter-Service Communication Flow

```txt
Frontend ──JWT──▶ Auth Service          (login/signup/validate)
Frontend ──JWT──▶ Drugs Service         (search/stock queries)
Frontend ──JWT──▶ Subscription Service  (subscribe/unsubscribe)
Frontend ──JWT──▶ Refill Service        (order status/adhoc refills)

Refill Service ──REST──▶ Subscription Service  (get refill dues)
Refill Service ──REST──▶ Drugs Service         (check availability, reserve stock)
Subscription Service ──REST──▶ Drugs Service   (verify drug availability)
```

---

## 🔐 Security Implementation

### JWT Authentication Flow

1. **Signup** — User registers with username, email, password, fullName. Password is hashed with **BCrypt**. A unique **Member ID** (`MEM` + 6 digits) is auto-generated.
2. **Signin** — Credentials are validated. An **HS512-signed JWT** is issued containing the username (`sub`), member ID and role as claims. Token expires in 15 minutes.
3. **Request Auth** — Frontend sends `Authorization: Bearer <token>` header with every request.
4. **Token Validation** — Each microservice has its own `JwtAuthFilter` that validates the JWT signature using a shared secret key, extracts the username, member ID and role, and sets the Spring Security context.

### Access Control

- **Every API call requires a valid JWT** except sign-in, sign-up, token validation, the username/email availability checks, API docs and `/actuator/health`. Missing or invalid tokens get **401**; insufficient role gets **403**.
- **Roles**: every self-registered account is a `MEMBER`. `ADMIN` is granted only in the database: `UPDATE users SET user_role = 'ADMIN' WHERE username = '...';`
- **Members** see only their own data: profile, subscriptions, prescriptions, orders and refill dues. Another member's subscription returns 404.
- **Admins** can list and delete users, edit any profile (including member ID), see all members' refill dues, and change refill order status.
- **CORS** allows only the origins in `APP_CORS_ALLOWED_ORIGINS` (dev default: `http://localhost:5173`, `http://localhost:3000`); no credentialed CORS.
- **JWT secret** comes only from the `JWT_SECRET` environment variable — there is no default. Services refuse to start without one, or if it is shorter than 64 bytes. Use the same value for all four services.

### Supabase Row Level Security (RLS)

Since the database is hosted on **Supabase**, all public tables are exposed via Supabase's PostgREST API. To prevent unauthorized direct access:

- **RLS is enabled** on all 7 tables: `users`, `drugs`, `drug_locations`, `member_prescriptions`, `member_subscriptions`, `refill_orders`, `refill_order_line_items`
- The Spring Boot backend connects as the **`postgres` user** (table owner), which **bypasses RLS automatically** — no additional policies are needed for the backend
- The `supabase/enable_rls.sql` script must be run once in the Supabase SQL Editor after initial deployment

### Password Security

- Passwords are **never stored in plaintext** — BCrypt hashing with salt rounds (`$2a$10$`)
- The `password` column in the `users` table is protected by RLS to prevent exposure via the Supabase PostgREST API
- JWT tokens do not contain passwords

---

## 🧠 Core Business Logic

### 1. Drug Inventory (Drugs Microservice)

- **Search**: Dedicated endpoints for exact ID lookup (`searchDrugsByID`) and case-insensitive name search (`searchDrugsByName`); name search only returns non-expired drugs
- **Expiry validation**: Only drugs with `expiry_date` after today are treated as valid/dispatchable
- **Multi-location stock**: Each drug has inventory across New York, Los Angeles, and Chicago
- **Dispatchable stock**: Calculates availability by location, combining expiry and quantity checks
- **Availability & reservation**: `availability` answers whether a quantity can be dispatched from a location; `reserve` atomically decrements stock (used by the refill service when confirming an order)

### 2. Subscription Management (Subscription Microservice)

- **Subscribe**: Links a member to a drug via a prescription, setting a refill frequency (WEEKLY/MONTHLY)
- **Unsubscribe**: Sets subscription status to INACTIVE (soft delete preserving history)
- **Drug verification**: Before creating a subscription, the service calls the Drugs Service to verify the drug is available at the member's location
- **Refill due calculation**: Determines which subscriptions are due for refill based on subscription date + frequency

### 3. Refill Processing (Refill Microservice)

- **Scheduled refills**: System queries active subscriptions due as of a given date
- **Adhoc refills**: User manually requests a refill — the system validates drug availability, reserves stock, creates order line items, and confirms the order
- **Order lifecycle**: `PENDING` → `CONFIRMED` → `IN_PROGRESS` → `SHIPPED` → `DELIVERED`
- **Tracking**: Tracking numbers are auto-generated when an order is shipped

### 4. Authentication (Auth Microservice)

- **User CRUD**: Create, read, update, delete users
- **Uniqueness enforcement**: Username, email, and member ID must all be unique
- **Member ID generation**: Auto-generated in format `MEM` + 6 random digits with collision detection

---

## 🚀 Quick Start

### Prerequisites

- **Java 21+** (JDK)
- **Maven 3.6+**
- **Node.js 20.19+** with npm

### Local Development

#### 1. Clone & Setup

```bash
git clone <your-repo-url>
cd Mail-Order-Pharmacy
```

#### 2. Start Backend Services

Every service needs the same `JWT_SECRET`. Create it once:

```bash
cp .env.example .env
echo "JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')" >> .env
```

Open 4 separate terminals and load it in each (`set -a; source .env; set +a` from the repo root), then:

```bash
# Terminal 1: Auth Service (port 8084)
cd backend/auth-microservice && mvn spring-boot:run

# Terminal 2: Drugs Service (port 8081)
cd backend/drugs-microservice && mvn spring-boot:run

# Terminal 3: Subscription Service (port 8082)
cd backend/subscription-microservice && mvn spring-boot:run

# Terminal 4: Refill Service (port 8083)
cd backend/refill-microservice && mvn spring-boot:run
```

All services use **H2 in-memory database** for development — no external DB setup required. Seed data is loaded automatically from `data.sql` files, including the logins `member1` / `member2` / `member3` and an `admin` account, all with password `password`.

#### 3. Start Frontend

```bash
cd frontend/member-portal
npm install
npm run dev
```

#### 4. Access Points

| Service             | URL                                     |
| ------------------- | --------------------------------------- |
| Member Portal       | <http://localhost:5173>                 |
| Swagger API Docs    | <http://localhost:8085>                 |
| Auth Health         | <http://localhost:8084/actuator/health> |
| Drugs Health        | <http://localhost:8081/actuator/health> |
| Subscription Health | <http://localhost:8082/actuator/health> |
| Refill Health       | <http://localhost:8083/actuator/health> |

---

## 📋 API Reference

### Auth Service (`/api/auth`)

| Method   | Endpoint                     | Description                    |
| -------- | ---------------------------- | ------------------------------ |
| `POST`   | `/signin`                    | Authenticate user, returns JWT (public) |
| `POST`   | `/signup`                    | Register new user (public)              |
| `POST`   | `/validate`                  | Validate JWT token (public)             |
| `GET`    | `/users`                     | List all users (admin)                  |
| `GET`    | `/users/{id}`                | Get user by ID (self or admin)          |
| `PUT`    | `/users/{id}`                | Update user (self or admin)             |
| `DELETE` | `/users/{id}`                | Delete user (admin)                     |
| `GET`    | `/check-username/{username}` | Check username availability (public)    |
| `GET`    | `/check-email/{email}`       | Check email availability (public)       |

All other endpoints below require a valid JWT.

### Drugs Service (`/api/drugs`)

| Method | Endpoint                                      | Description                                   |
| ------ | --------------------------------------------- | --------------------------------------------- |
| `GET`  | `/searchDrugsByID?drugId=`                    | Search by drug ID                             |
| `GET`  | `/searchDrugsByName?drugName=`                | Search by name (fuzzy, non-expired)           |
| `GET`  | `/all`                                        | All valid (non-expired) drugs                 |
| `GET`  | `/{drugId}/locations`                         | Stock rows for a drug across locations        |
| `POST` | `/getDispatchableDrugStock`                   | Stock availability for drug IDs at a location |
| `GET`  | `/availability?drugCode=&location=&quantity=` | Can this quantity be dispatched? (bool)       |
| `POST` | `/reserve`                                    | Atomically decrement stock (bool)             |

### Subscription Service (`/api/subscriptions`)

| Method | Endpoint               | Description               |
| ------ | ---------------------- | ------------------------- |
| `POST` | `/subscribe`           | Create subscription       |
| `GET`  | `/my-subscriptions`    | User's subscriptions      |
| `GET`  | `/active`              | Active subscriptions only |
| `POST` | `/unsubscribe`         | Cancel subscription       |
| `GET`  | `/{subscriptionId}`    | One of the member's subscriptions (admin: any) |
| `GET`  | `/refillDues?dueDate=` | Member's refills due as of date (admin: all members) |

### Refill Service (`/api/refill`)

| Method | Endpoint                       | Description                                         |
| ------ | ------------------------------ | --------------------------------------------------- |
| `GET`  | `/viewRefillStatus`            | Latest refill status                                |
| `GET`  | `/orders`                      | All orders for member                               |
| `POST` | `/requestAdhocRefill`          | Create adhoc refill order                           |
| `GET`  | `/getRefillDuesAsOfDate?date=` | Due refills (delegates to subscription service)     |
| `PUT`  | `/orders/{id}/status?status=`  | Update order status, admin only (auto-sets tracking on SHIPPED) |

---

## 🗄️ Database Schema

### Development vs Production

| Aspect       | Development                           | Production                          |
| ------------ | ------------------------------------- | ----------------------------------- |
| Database     | H2 in-memory                          | PostgreSQL (Supabase)               |
| DDL Strategy | `create-drop` (fresh on each restart) | `update` (preserves data)           |
| Seed Data    | Loaded from `data.sql` on startup     | Not loaded (`sql.init.mode: never`) |
| RLS          | N/A (H2 doesn't support it)           | Enabled on all 7 tables             |

### Entity Relationships

```txt
Auth Service:
  users (id, username, email, password, member_id, full_name, created_at, updated_at)

Drugs Service:
  drugs (drug_id PK, drug_name, manufacturer, manufactured_date, expiry_date,
         units_per_package, cost_per_package, medical_composition)
  drug_locations (drug_id + location PK, quantity_available) → drugs (FK)

Subscription Service:
  member_prescriptions (prescription_id PK, member_id, insurance_policy_number,
                        insurance_provider, prescription_date, drug_id,
                        dosage_per_day, prescription_course, doctor_details)
  member_subscriptions (subscription_id PK, member_id, subscription_date,
                        prescription_id FK, refill_frequency, member_location,
                        subscription_status) → member_prescriptions (FK)

Refill Service:
  refill_orders (refill_order_id PK, member_id, order_date, member_location,
                 order_status, order_type, subscription_id, delivery_date,
                 tracking_number)
  refill_order_line_items (line_item_id PK, drug_code, drug_name, quantity,
                           prescription_id, unit_price, total_price,
                           refill_order_id FK) → refill_orders (FK)
```

---

## 🔧 Supabase RLS Setup

After deploying to production with Supabase, run the following SQL once in the **Supabase SQL Editor** (`supabase/enable_rls.sql`):

```sql
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.drugs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.drug_locations ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.member_prescriptions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.member_subscriptions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.refill_orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.refill_order_line_items ENABLE ROW LEVEL SECURITY;
```

> **Why no policies?** The Spring Boot backend connects as the `postgres` user, which is the table owner and automatically bypasses RLS. This satisfies the Supabase linter while keeping the backend fully functional.

---

## 🐳 Docker Deployment

```bash
# Build and start all services (reads JWT_SECRET from .env — see Quick Start)
docker compose up --build

# Individual service builds
docker build -f Dockerfile.auth -t auth-service .
docker build -f Dockerfile.auth.drugs -t drugs-service .
docker build -f Dockerfile.auth.subscription -t subscription-service .
docker build -f Dockerfile.auth.refill -t refill-service .
docker build -f Dockerfile.auth.swagger -t swagger-service .
```

Docker images use **multi-stage builds** for optimized image sizes (~150MB each):

1. **Build stage**: Maven compiles the JAR with dependencies
2. **Runtime stage**: Lightweight JRE runs the JAR as a non-root user

---

## 🛠️ Build & Test

```bash
# Build all backend services
cd backend/auth-microservice && mvn clean install
cd backend/drugs-microservice && mvn clean install
cd backend/subscription-microservice && mvn clean install
cd backend/refill-microservice && mvn clean install
cd backend/swagger-aggregator && mvn clean install

# Frontend
cd frontend/member-portal
npm install
npm run build
```

### API Testing (Postman)

Import `MediFlow.postman_collection.json`. Run **Auth → Sign In** first — its test
script captures the JWT into the `token` collection variable that every other request
reuses. For the admin-only requests (List Users, Delete User, Update Refill Order Status),
run **Auth → Sign In (Admin)**, which stores `admin_token`. The `*_url` variables default to
the local dev ports; override them to target a deployed environment. Seed logins are
`member1` / `member2` / `member3` and `admin`, all with password `password`. The collection
ships with captured example responses for the happy path plus the 400/403/404 error cases.

---

## 📁 Project Structure

```txt
Mail-Order-Pharmacy/
├── backend/
│   ├── auth-microservice/          # JWT authentication, user management
│   ├── drugs-microservice/         # Drug catalog, inventory, stock queries
│   ├── subscription-microservice/  # Prescription subscriptions, refill scheduling
│   ├── refill-microservice/        # Order processing, adhoc refills, tracking
│   └── swagger-aggregator/         # Thin Swagger UI host (springdoc urls dropdown)
├── frontend/
│   └── member-portal/              # React TypeScript SPA
├── supabase/
│   └── enable_rls.sql                  # RLS migration for Supabase
├── MediFlow.postman_collection.json    # Postman API collection (full flow)
├── Dockerfile.* (5 files)              # Multi-stage Docker builds
└── docker-compose.yml                  # Local Docker orchestration
```

---

**Tech Stack**: Spring Boot 4.1 • React 19 • TypeScript 6 • Java 21 • Maven • Vite 8 • H2/PostgreSQL • Supabase • Docker

**Architecture**: Microservices • REST APIs • JWT Auth • Database per Service • Row Level Security
