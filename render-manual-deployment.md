# MediFlow Manual Deployment (Render + Supabase)

Step-by-step guide for deploying MediFlow to [Render](https://render.com) with a
[Supabase](https://supabase.com) PostgreSQL database. For infrastructure-as-code,
`render.yaml` declares the same services — this guide is the manual equivalent and the reference for which environment
variables each service needs.

## Step 1: Create the Supabase database

1. Create a project at [supabase.com](https://supabase.com). Note the **project ref**
   (the `xxxxxxxx` in `db.xxxxxxxx.supabase.co`) and the database password you set.
2. Click **Connect** and choose the **Session pooler** tab — **not** the "Direct connection" tab.
    - The direct host (`db.<ref>.supabase.co`) is **IPv6-only**. Render's outbound traffic is IPv4, so a direct
      connection cannot resolve and the service fails to start. The Session pooler
      (`aws-0-<region>.pooler.supabase.com`) is IPv4-ready.
3. From the pooler details, assemble the three values the services need:

   | Variable            | Value                                                                |
   | ------------------- |----------------------------------------------------------------------|
   | `DATABASE_URL`      | `jdbc:postgresql://aws-0-<region>.pooler.supabase.com:5432/postgres` |
   | `DATABASE_USERNAME` | `postgres.<project-ref>`                                             |
   | `DATABASE_PASSWORD` | your Supabase database password                                      |

   > The pooler username **must** be `postgres.<project-ref>` — a bare `postgres`
   > produces `FATAL: Tenant or user not found`. Supabase requires TLS, which the
   > services already request via `sslmode=require`.

4. After the first deploy has created the tables, run `supabase/enable_rls.sql` once in the Supabase **SQL Editor** to
   satisfy the security linter (see the main README).

## Step 2: Deploy the backend microservices

For each service: **New → Web Service**, connect this GitHub repo, choose **Runtime:
Docker**, **Plan: Free**, and the Dockerfile path below. Health check path is
`/actuator/health` for all services.

All four data services share these variables (set the DB values from Step 1):

```txt
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=<a long random secret, identical across all services>
JWT_EXPIRATION=900000
DATABASE_URL=jdbc:postgresql://aws-0-<region>.pooler.supabase.com:5432/postgres
DATABASE_USERNAME=postgres.<project-ref>
DATABASE_PASSWORD=<your-supabase-password>
```

> `JWT_SECRET` **must be byte-for-byte identical** on every service — each one
> validates tokens independently against this shared key. `SPRING_PROFILES_ACTIVE=prod`
> is what switches a service from its in-memory H2 dev database to Supabase.

| Service                  | Dockerfile                  | Extra variables                                                     |
|--------------------------|-----------------------------|---------------------------------------------------------------------|
| `mediflow-auth`          | `./Dockerfile.auth`         | —                                                                   |
| `mediflow-drugs`         | `./Dockerfile.drugs`        | `AUTH_SERVICE_URL`                                                  |
| `mediflow-subscriptions` | `./Dockerfile.subscription` | `AUTH_SERVICE_URL`, `DRUGS_SERVICE_URL`                             |
| `mediflow-refills`       | `./Dockerfile.refill`       | `AUTH_SERVICE_URL`, `SUBSCRIPTION_SERVICE_URL`, `DRUGS_SERVICE_URL` |

The `*_SERVICE_URL` variables point at the other deployed services, e.g.
`https://mediflow-auth.onrender.com`.

## Step 3: Deploy the Swagger aggregator (optional)

**New → Web Service**, Dockerfile `./Dockerfile.swagger`. It needs only the
`*_SERVICE_URL` variables (no database), so it has no `DATABASE_*` or `JWT_*` config.

## Step 4: Deploy the frontend

**New → Static Site**:

```txt
Build Command: cd frontend/member-portal && npm install && npm run build
Publish Directory: frontend/member-portal/dist
```

Environment variables (point at your deployed services):

```txt
VITE_AUTH_SERVICE_URL=https://mediflow-auth.onrender.com
VITE_DRUGS_SERVICE_URL=https://mediflow-drugs.onrender.com
VITE_SUBSCRIPTION_SERVICE_URL=https://mediflow-subscriptions.onrender.com
VITE_REFILL_SERVICE_URL=https://mediflow-refills.onrender.com
VITE_SWAGGER_URL=https://mediflow-swagger.onrender.com
```

## Verifying a deployment

- **Health**: `GET https://<service>.onrender.com/actuator/health` → `{"status":"UP"}`.
- **Smoke test**: import `MediFlow.postman_collection.json`, point the `*_url`
  collection variables at your deployed hosts, run **Auth → Sign In**, then exercise the Drugs / Subscriptions / Refill
  folders.
- **Startup DB errors** (`Tenant or user not found`, connection refused) almost always mean the direct connection string
  was used instead of the Session pooler, or
  `DATABASE_USERNAME` is missing the `.<project-ref>` suffix.
