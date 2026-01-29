# 🏥 MediFlow - Project Implementation Guide

This document explains the logical implementation, architectural decisions, and core concepts behind the MediFlow Mail-Order Pharmacy platform.

## 🌟 Project Vision & Architecture

MediFlow is designed as a scalable, cloud-native **Microservices Architecture**. Unlike a monolithic application, MediFlow breaks down functionality into distinct, loosely coupled services that can be developed, deployed, and scaled independently.

### 🏗️ Architectural Pattern

We strictly follow the **Microservices** pattern with the following key components:

1. **Frontend (Member Portal)**: A single-page application (SPA) built with React.
2. **API Gateway / Aggregator**: The `swagger-aggregator` acts as a unified entry point for documentation and can facilitate service discovery.
3. **Core Services**:
   - **Auth Service**: Identity and Access Management.
   - **Drugs Service**: Product Catalog and Inventory.
   - **Subscription Service**: Business logic for recurring orders.
   - **Refill Service**: Order fulfillment and tracking.
4. **Database Strategy**: **Database per Service** pattern. Each microservice manages its own data (tables) to ensure loose coupling. In development, we use H2 (in-memory/file); in production, we use PostgreSQL.

---

## 🛠️ Technology Stack & Rationale

### Backend: Java & Spring Boot

- **Spring Boot 3.4+**: Chosen for its robust ecosystem, dependency injection, and "convention over configuration" approach.
- **Spring Data JPA**: Simplifies database interactions using Hibernate ORM.
- **Maven**: Dependency management and build automation.

### Frontend: React & Modern UI

- **React 18**: selected for its component-based architecture and efficient Virtual DOM.
- **Vite**: Next-generation build tool that is significantly faster than Webpack.
- **Material UI (MUI)**: Provides a polished, accessible design system. We customized it to mimic **Apple/Vercel aesthetics** (Glassmorphism, Inter font, clean lines).
- **TypeScript**: Ensures type safety and reduces runtime errors.

### Infrastructure: Docker & Cloud

- **Docker**: Used for "Write Once, Run Anywhere". We use **Multi-stage builds** to keep image sizes small (~150MB).
  - _Stage 1_: Build the JAR using Maven.
  - _Stage 2_: Run the JAR using a lightweight JRE (Distroless/Alpine).
- **Render.com**: Chosen for its ease of deployment and free-tier support for Docker containers.

---

## 🧠 Core Implementation Logic

### 1. Authentication & Security (Auth Microservice)

- **Concept**: Stateless Authentication using **JWT (JSON Web Tokens)**.
- **Flow**:
  1. User signs up/logs in.
  2. Server validates credentials and issues a signed JWT containing the user's ID (`sub`) and expiration time.
  3. Frontend stores this token (e.g., in localStorage).
  4. Subsequent requests include the token in the `Authorization: Bearer <token>` header.
  5. Each microservice validates the token's signature independently (Shared Key approach).
- **Logic**:
  - `AuthController`: Handles `/signin`, `/signup`.
  - `JwtUtils`: Generates and validates tokens using HS512 algorithm.

### 2. Drug Inventory (Drugs Microservice)

- **Concept**: Catalog management with intelligent search.
- **Logic**:
  - **Search**: A dual-strategy search. If the input matches a specific ID format (`D[0-9]+`), it searches by ID; otherwise, it performs a fuzzy search by name.
  - **Dispatchable Stock**: Calculates availability based on location rules (checking logic across warehouses in NY, LA, Chicago).

### 3. Subscription Management (Subscription Microservice)

- **Concept**: Managing recurring prescriptions.
- **Logic**:
  - Links a `MemberID` to a `DrugID` with a set frequency (Weekly/Monthly).
  - **Status Tracking**: Subscriptions have statuses (ACTIVE, CANCELLED) to control refill generation.

### 4. Refill Processing (Refill Microservice)

- **Concept**: The transactional heart of the system.
- **Logic**:
  - **Ad-hoc Refills**: Users can manually request a refill. The system validates subscription status before creating a refill order.
  - **Order Lifecycle**: Orders move through states: `PENDING` -> `PROCESSING` -> `DELIVERED`.

---

## 🎨 Frontend Implementation

### Design System

We implemented a custom Design System stored in `src/theme/`.

- **ThemeContext**: Uses React Context API to manage Dark/Light mode state globally. Persists preference to `localStorage`.
- **Components**: We built wrapper components (e.g., `ModernCard`, `ModernLayout`) to enforce consistency.

### State Management

- **React Query**: Used for fetching data. It handles caching, loading states, and refetching automatically, which simplifies the UI logic significantly compared to raw `useEffect`.

---

## 🚀 Deployment & DevOps Concepts

### Containerization Strategy

We use `Dockerfile`s for every service. Key optimizations:

- **Layer Caching**: Copying `pom.xml` and creating a dependency layer _before_ copying source code. This means if code changes but dependencies don't, Docker reuses the heavy dependency layer.
- **Non-Root User**: Running containers as a non-privileged user for security.

### Service Discovery & Routing

In a containerized environment (like Render or Docker Compose), services refer to each other by **Service Names** (e.g., `auth-microservice`), not localhost.

- **Environment Variables**: We inject service URLs (`VITE_AUTH_SERVICE_URL`, etc.) at runtime/build time, allowing the code to be environment-agnostic.

---

## 🧹 Code Quality & Standards

- **Clean Code**: Services are structured into generic layers: `Controller` (API) -> `Service` (Logic) -> `Repository` (Data) -> `Entity` (Model).
- **Swagger/OpenAPI**: Used for automatic API documentation (`/v3/api-docs`), ensuring the frontend team always has up-to-date contracts.

This architecture ensures MediFlow is robust, maintainable, and ready for scale.
