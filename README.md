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
| **DDL Management**        | Flyway migrations (`db/migration`); Hibernate `ddl-auto: validate` | Versioned, reviewable schema changes; services refuse to start if entities and schema disagree                                  |
| **API Documentation**     | OpenAPI 3.0 via springdoc; a thin Swagger UI host aggregates specs | Each service self-documents; one Swagger UI switches between all service specs via `springdoc.swagger-ui.urls` (no custom code) |
| **Row Level Security**    | Supabase RLS enabled on all tables                                 | Prevents direct API access to data via Supabase's PostgREST layer                                                               |

### Inter-Service Communication Flow

```txt
Frontend ──JWT──▶ Auth Service          (login/signup/validate)
Frontend ──JWT──▶ Drugs Service         (search/stock queries)
Frontend ──JWT──▶ Subscription Service  (subscribe/unsubscribe)
Frontend ──JWT──▶ Refill Service        (order status/adhoc refills)

Refill Service ──REST──▶ Subscription Service  (verify prescriptions, get refill dues)
Refill Service ──REST──▶ Drugs Service         (check availability; reserve/release stock*)
Subscription Service ──REST──▶ Drugs Service   (verify drug availability)
Auth Service ──REST──▶ Refill, Subscription    (erase a deleted member's data*)
```

\* Internal endpoints: called with a short-lived `SERVICE` token, which members can't obtain.
Calls between services time out (`SERVICES_HTTP_CONNECTTIMEOUT` 5s, `SERVICES_HTTP_READTIMEOUT` 15s).
On hosts that put idle services to sleep, set `SERVICES_HTTP_WAKEUPWAIT` (e.g. `90s`) so a call
waits for a waking service instead of failing; only calls that are safe to repeat are retried
after a timeout.

---

## 🔐 Security Implementation

### JWT Authentication Flow

1. **Signup** — User registers with username, email, password, fullName. Password is hashed with **BCrypt**. A unique **Member ID** (`MEM` + 6 digits) is auto-generated.
2. **Signin** — Credentials are validated. An **HS512-signed JWT** is issued containing the username (`sub`), account ID (`uid`), member ID and role as claims. Its lifetime is `JWT_EXPIRATION` milliseconds (15 minutes by default in local development).
3. **Request Auth** — Frontend sends `Authorization: Bearer <token>` header with every request.
4. **Token Validation** — Each microservice has its own `JwtAuthFilter` that validates the JWT signature using a shared secret key, extracts the username, member ID and role, and sets the Spring Security context. The auth service also checks that the token's account still exists (and is the same account, by `uid`); subscription and refill reject tokens issued to a member before their data was erased.

### Access Control

- **Every API call requires a valid JWT** except sign-in, sign-up, token validation, the username/email availability checks, API docs and `/actuator/health`. Missing or invalid tokens get **401**; insufficient role gets **403**.
- **Roles**: every self-registered account is a `MEMBER`. `ADMIN` is granted only in the database: `UPDATE users SET user_role = 'ADMIN' WHERE username = '...';`
- **Members** see only their own data: profile, subscriptions, prescriptions, orders and refill dues. Another member's subscription returns 404.
- **Admins** can list and delete users, edit any profile (including member ID), see all members' refill dues, and change refill order status.
- **Deleting a user erases their data everywhere**: the auth service first has the refill service delete the member's orders (returning the stock of any order that hadn't shipped) and the subscription service delete their subscriptions and prescriptions, using short-lived `SERVICE` tokens. Only then is the account deleted. If either service can't be reached, the user is kept and the admin gets `503`; retrying is safe. The auth service needs `SUBSCRIPTION_SERVICE_URL` and `REFILL_SERVICE_URL` for this.
- **A deleted user's tokens stop working immediately**, not when they expire: the subscription and refill services record each erased member and reject tokens issued to that member before the erasure (`401`), so the old token can't create new subscriptions or orders. Tokens also carry the account's ID (`uid`), so if a deleted username is registered again, the old token doesn't work for the new account.
- **CORS** allows only the origins in `APP_CORS_ALLOWED_ORIGINS` (dev default: `http://localhost:5173`, `http://localhost:3000`, and the Swagger aggregator `http://localhost:8085`); no credentialed CORS. In production, include both the frontend's and the Swagger aggregator's origins.
- **Brute-force protection**: sign-in, sign-up and the availability checks are limited to 20 requests/minute per client address (`429` + `Retry-After`), and 5 failed sign-ins for the same username from the same address lock that combination for 15 minutes. Limits are in-memory, i.e. per service instance.
- **Passwords** can be changed via `PUT /users/{id}` with `password`; members must also send `currentPassword`, admins can reset directly.
- **Content-Security-Policy**: production builds of the member portal carry a CSP that only runs the app's own scripts and only allows API calls to the configured service URLs, so injected scripts can't run or send the session token elsewhere.
- **JWT secret** comes only from the `JWT_SECRET` environment variable — there is no default. Services refuse to start without one, or if it is shorter than 64 bytes. Use the same value for all four services.

### Supabase Row Level Security (RLS)

Since the database is hosted on **Supabase**, all public tables are exposed via Supabase's PostgREST API. To prevent unauthorized direct access:

- **RLS is enabled automatically.** After every Flyway migration, each service enables RLS on every table it owns that lacks it — its own tables, its `flyway_history_*` table, and any table added by a future migration (`EnableRowLevelSecurityCallback`). PostgreSQL only; no manual SQL step.
- The Spring Boot backend connects as the **`postgres` user** (table owner), which **bypasses RLS automatically** — no policies are needed for the backend, and with no policies the PostgREST API sees nothing
- `PostgresMigrationTest` in each service fails if any table is left without RLS

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
- **Availability & reservation**: `availability` answers whether a quantity can be dispatched from a location; `reserve` decrements stock in a single conditional `UPDATE` (atomic, so concurrent orders can never oversell) and `release` returns it. Both are internal: only the refill service may call them, using a short-lived `SERVICE` token signed with the shared JWT secret

### 2. Subscription Management (Subscription Microservice)

- **Subscribe**: Links a member to a drug via a prescription, setting a refill frequency (WEEKLY/MONTHLY)
- **Unsubscribe**: Sets subscription status to INACTIVE (soft delete preserving history)
- **Drug verification**: Before creating a subscription, the service calls the Drugs Service to verify the drug is available at the member's location
- **Refill due calculation**: Refills are scheduled every week/month from the subscription date. "Due as of date D" lists active subscriptions whose next scheduled refill (on or after today) falls on or before D, so today's date shows refills due today and a later date looks ahead

### 3. Refill Processing (Refill Microservice)

- **Refill dues**: lists the active subscriptions whose next scheduled refill falls on or before a given date (the member's own; admins see all). Orders for them are not created automatically
- **Adhoc refills**: User manually requests a refill — each line must name one of the member's own prescriptions for that drug; the system checks stock for every line, reserves it line by line (releasing everything already reserved if any line or the save fails), and confirms the order
- **Order lifecycle**: `CONFIRMED` → `IN_PROGRESS` (optional) → `SHIPPED` → `DELIVERED`. Orders only move forward; anything else (including re-setting the current status) is `409`. An order can be `CANCELLED` until it ships, which returns its reserved stock (see below). `DELIVERED` and `CANCELLED` are final. The order row is locked during a change, so concurrent requests can't both apply.
- **Tracking**: A tracking number is generated once, when the order ships
- **Returning stock reliably**: stock to be returned (cancelled or erased orders, or an order that failed part-way) is stored in `pending_stock_releases` in the same transaction as the change, sent to the drugs service, and removed once confirmed. If the drugs service is down, it stays queued and is retried until it succeeds: after 1 minute, then with the gap doubling up to once an hour. From the 6th failure each one is logged as an ERROR naming the likely causes. Each release has an ID that the drugs service records (`stock_releases`, pruned after 30 days), so a retry never returns the same stock twice

### 4. Authentication (Auth Microservice)

- **User CRUD**: Create, read, update, delete users
- **Uniqueness enforcement**: Username, email, and member ID must all be unique; usernames and emails regardless of case (`Bob` = `bob`). Emails are stored in lower case. Lower-cased `username_key`/`email_key` columns carry unique constraints, so even simultaneous sign-ups can't create case-variants; the loser gets the same clean `400` as any duplicate. (Case-variants that existed before these constraints keep working; the oldest account holds the key.)
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

All services use **H2 in-memory database** for development — no external DB setup required. Flyway creates the schema and loads the dev seed data (`db/seed`), including the logins `member1` / `member2` / `member3` and an `admin` account, all with password `password`.

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

| Method   | Endpoint                     | Description                                                      |
| -------- | ---------------------------- | ---------------------------------------------------------------- |
| `POST`   | `/signin`                    | Authenticate user, returns JWT (public)                          |
| `POST`   | `/signup`                    | Register new user (public)                                       |
| `POST`   | `/validate`                  | Validate JWT token (public; 401 if its account no longer exists) |
| `GET`    | `/users`                     | List all users (admin)                                           |
| `GET`    | `/users/{id}`                | Get user by ID (self or admin)                                   |
| `PUT`    | `/users/{id}`                | Update user (self or admin)                                      |
| `DELETE` | `/users/{id}`                | Delete user and erase their data (admin)                         |
| `GET`    | `/check-username/{username}` | Check username availability (public)                             |
| `GET`    | `/check-email/{email}`       | Check email availability (public)                                |

All other endpoints below require a valid JWT.

### Drugs Service (`/api/drugs`)

| Method | Endpoint                                      | Description                                                        |
| ------ | --------------------------------------------- | ------------------------------------------------------------------ |
| `GET`  | `/searchDrugsByID?drugId=`                    | Search by drug ID                                                  |
| `GET`  | `/searchDrugsByName?drugName=`                | Search by name (fuzzy, non-expired)                                |
| `GET`  | `/all`                                        | All valid (non-expired) drugs                                      |
| `GET`  | `/{drugId}/locations`                         | Stock rows for a drug across locations                             |
| `POST` | `/getDispatchableDrugStock`                   | Stock availability for drug IDs at a location                      |
| `GET`  | `/availability?drugCode=&location=&quantity=` | Can this quantity be dispatched? (bool)                            |
| `POST` | `/reserve`                                    | Atomically decrement stock (bool) — internal, `SERVICE` token only |
| `POST` | `/release`                                    | Return reserved stock (bool) — internal, `SERVICE` token only      |

### Subscription Service (`/api/subscriptions`)

| Method   | Endpoint               | Description                                                                               |
| -------- | ---------------------- | ----------------------------------------------------------------------------------------- |
| `POST`   | `/subscribe`           | Create subscription                                                                       |
| `GET`    | `/my-subscriptions`    | User's subscriptions                                                                      |
| `GET`    | `/active`              | Active subscriptions only                                                                 |
| `POST`   | `/unsubscribe`         | Cancel subscription                                                                       |
| `GET`    | `/{subscriptionId}`    | One of the member's subscriptions (admin: any)                                            |
| `GET`    | `/refillDues?dueDate=` | Member's subscriptions whose next refill falls on or before the date (admin: all members) |
| `DELETE` | `/members/{memberId}`  | Erase a deleted member's subscriptions and prescriptions — internal, `SERVICE` token only |

### Refill Service (`/api/refill`)

| Method   | Endpoint                       | Description                                                                                 |
| -------- | ------------------------------ | ------------------------------------------------------------------------------------------- |
| `GET`    | `/viewRefillStatus`            | Latest refill status                                                                        |
| `GET`    | `/orders`                      | All orders for member                                                                       |
| `POST`   | `/requestAdhocRefill`          | Create adhoc refill order                                                                   |
| `GET`    | `/getRefillDuesAsOfDate?date=` | Due refills (delegates to subscription service)                                             |
| `PUT`    | `/orders/{id}/status?status=`  | Move order along its lifecycle, admin only (`409` if not allowed; cancelling returns stock) |
| `DELETE` | `/members/{memberId}`          | Erase a deleted member's orders — internal, `SERVICE` token only                            |

---

## 🗄️ Database Schema

### Development vs Production

| Aspect    | Development                                     | Production                                                  |
| --------- | ----------------------------------------------- | ----------------------------------------------------------- |
| Database  | H2 in-memory                                    | PostgreSQL (Supabase)                                       |
| Schema    | Flyway migrations (fresh in-memory DB each run) | Flyway migrations (`ddl-auto: validate`)                    |
| Seed Data | Flyway repeatable migration in `db/seed`        | Never loaded (prod reads `db/migration` only)               |
| RLS       | N/A (H2 doesn't support it)                     | Enabled automatically on every table (incl. Flyway history) |

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

### Schema Migrations (Flyway)

Each service owns its migrations in `src/main/resources/db/migration` (`V1__…`, `V2__…`).
Hibernate only **validates** the schema, so a service will not start if its entities and
the database disagree. To change the schema, add the next versioned SQL file; never edit
one that has already run.

All services share one database, so each keeps its own history table
(`flyway_history_auth`, `flyway_history_drugs`, …) and baselines at version 0. The V1
scripts use `CREATE TABLE IF NOT EXISTS`, so the first deploy adopts the existing tables
without touching data. `PostgresMigrationTest` in each service checks this against a
real (embedded) PostgreSQL, for both a fresh and an existing database.

---

## 🐳 Docker Deployment

```bash
# Build and start all services (reads JWT_SECRET from .env — see Quick Start)
# Member portal: http://localhost:3000 · API docs: http://localhost:8085
docker compose up --build

# Individual service builds (each service folder is its own build context)
docker build -t auth-service backend/auth-microservice
docker build -t drugs-service backend/drugs-microservice
docker build -t subscription-service backend/subscription-microservice
docker build -t refill-service backend/refill-microservice
docker build -t swagger-service backend/swagger-aggregator
docker build -t member-portal frontend/member-portal   # pass --build-arg VITE_*_SERVICE_URL=... for production
```

Docker images use **multi-stage builds** for optimized image sizes (~150MB each):

1. **Build stage**: Maven compiles the JAR with dependencies
2. **Runtime stage**: Lightweight JRE runs the JAR as a non-root user, on `$PORT` if the host sets it (otherwise the service's default port), with a container health check on `/actuator/health`

On a hosting platform, point each service at its folder: Dockerfile path `backend/<service>/Dockerfile`, build context `backend/<service>`.

### Production configuration

Every backend service runs with `SPRING_PROFILES_ACTIVE=prod`, which requires the variables
marked **required**; a service refuses to start without them. `PORT` is read automatically.

| Variable                                                                                                     |  drugs   | subscription |  refill  |   auth   | swagger |
| ------------------------------------------------------------------------------------------------------------ | :------: | :----------: | :------: | :------: | :-----: |
| `DATABASE_URL` (JDBC form: `jdbc:postgresql://host:5432/postgres`), `DATABASE_USERNAME`, `DATABASE_PASSWORD` | required |   required   | required | required |    –    |
| `JWT_SECRET` (64+ bytes, the same on all four)                                                               | required |   required   | required | required |    –    |
| `APP_CORS_ALLOWED_ORIGINS` (portal and Swagger UI origins, comma-separated)                                  | required |   required   | required | required |    –    |
| `JWT_EXPIRATION` (token lifetime, ms)                                                                        |    –     |      –       |    –     | required |    –    |
| `DRUGS_SERVICE_URL`                                                                                          |    –     |   required   | required |    –     |  set\*  |
| `SUBSCRIPTION_SERVICE_URL`                                                                                   |    –     |      –       | required | required |  set\*  |
| `REFILL_SERVICE_URL`                                                                                         |    –     |      –       |    –     | required |  set\*  |
| `AUTH_SERVICE_URL`                                                                                           |    –     |      –       |    –     |    –     |  set\*  |
| `SERVICES_HTTP_WAKEUPWAIT` (wait for sleeping services, e.g. `90s`)                                          |    –     |   optional   | optional | optional |    –    |
| `SERVICES_HTTP_CONNECTTIMEOUT` / `SERVICES_HTTP_READTIMEOUT` (default `5s` / `15s`)                          |    –     |   optional   | optional | optional |    –    |

\* The Swagger UI host starts without these but then points at `localhost`; set them to the services' public URLs.

The member portal is configured at build time with `VITE_AUTH_SERVICE_URL`,
`VITE_DRUGS_SERVICE_URL`, `VITE_SUBSCRIPTION_SERVICE_URL` and `VITE_REFILL_SERVICE_URL`;
the build's Content-Security-Policy allows API calls only to those origins.

### Hosting the member portal (SPA fallback)

The portal is a single-page app: routes like `/refills` exist only in the browser, so
the host must answer any unknown path with `index.html` (status 200). Otherwise a
page refresh or a shared link gives a 404.

| Host                                               | How the fallback is configured                                                                     |
| -------------------------------------------------- | -------------------------------------------------------------------------------------------------- |
| Docker image (`frontend/member-portal/Dockerfile`) | Built in: `nginx.conf` uses `try_files $uri $uri/ /index.html`                                     |
| Netlify, Cloudflare Pages                          | Built in: `public/_redirects` (`/*  /index.html  200`) is copied into the build                    |
| Vercel                                             | Add a `vercel.json`: `{ "rewrites": [{ "source": "/(.*)", "destination": "/index.html" }] }`       |
| Render (static site)                               | Dashboard → Redirects/Rewrites: source `/*`, destination `/index.html`, action **Rewrite**         |
| GitHub Pages                                       | No rewrites: copy `index.html` to `404.html` in the build output (pages then load with status 404) |
| Other / your own server                            | Rewrite every path that isn't a file to `/index.html`, as in `nginx.conf`                          |

Static hosts other than the Docker image also don't send the security headers in
`nginx.conf` (`X-Frame-Options`, `nosniff`, `Referrer-Policy`); add them in the host's
header settings if it supports them. The Content-Security-Policy is in `index.html`, so
it applies everywhere.

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
├── MediFlow.postman_collection.json    # Postman API collection (full flow)
└── docker-compose.yml                  # Local Docker orchestration
```

---

**Tech Stack**: Spring Boot 4.1 • React 19 • TypeScript 6 • Java 21 • Maven • Vite 8 • H2/PostgreSQL • Supabase • Docker

**Architecture**: Microservices • REST APIs • JWT Auth • Shared database with per-service tables and migrations • Row Level Security
