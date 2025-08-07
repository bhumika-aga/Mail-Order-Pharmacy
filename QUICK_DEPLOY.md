# MediFlow - Quick Deployment Guide ⚡

## 🚀 Deploy to Render.com (Free Tier)

### Step 1: Create Database

1. Go to [Render Dashboard](https://dashboard.render.com)
2. **New** → **PostgreSQL**
3. Name: `mediflow-database`, Plan: **Free**
4. Save the **Internal Database URL**

### Step 2: Deploy Services

#### Option A: Blueprint (Recommended)

```bash
# Push your code to GitHub
git add .
git commit -m "Deploy MediFlow"
git push origin main

# In Render Dashboard:
# 1. New → Blueprint
# 2. Connect GitHub repo  
# 3. Deploy (uses render.yaml)
```

#### Option B: Manual Services

Create each service manually:

- `mediflow-auth` (Dockerfile.auth)
- `mediflow-drugs` (Dockerfile.drugs)  
- `mediflow-subscriptions` (Dockerfile.subscription)
- `mediflow-refills` (Dockerfile.refill)
- `mediflow-swagger` (Dockerfile.swagger)
- `mediflow-app` (Static Site - frontend)

### Step 3: Environment Variables

**All Backend Services:**

```bash
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=mediFlowSecureSecretKeyForProductionDeploymentWithSufficientLengthForHS512Algorithm2024
JWT_EXPIRATION=900000
DATABASE_URL=[Your PostgreSQL URL from Step 1]
```

**Service-Specific URLs:**

```bash
# Add to each service as needed:
AUTH_SERVICE_URL=https://mediflow-auth.onrender.com
DRUGS_SERVICE_URL=https://mediflow-drugs.onrender.com
SUBSCRIPTION_SERVICE_URL=https://mediflow-subscriptions.onrender.com  
REFILL_SERVICE_URL=https://mediflow-refills.onrender.com
```

**Frontend:**

```bash
VITE_AUTH_SERVICE_URL=https://mediflow-auth.onrender.com
VITE_DRUGS_SERVICE_URL=https://mediflow-drugs.onrender.com
VITE_SUBSCRIPTION_SERVICE_URL=https://mediflow-subscriptions.onrender.com
VITE_REFILL_SERVICE_URL=https://mediflow-refills.onrender.com
VITE_SWAGGER_URL=https://mediflow-swagger.onrender.com
```

### Step 4: Keep Services Alive (Optional)

Create `.github/workflows/keep-alive.yml`:

```yaml
name: Keep MediFlow Alive
on:
  schedule:
    - cron: '*/14 * * * *'
jobs:
  wake:
    runs-on: ubuntu-latest
    steps:
      - run: |
          curl -f https://mediflow-auth.onrender.com/actuator/health
          curl -f https://mediflow-drugs.onrender.com/actuator/health
          curl -f https://mediflow-subscriptions.onrender.com/actuator/health
          curl -f https://mediflow-refills.onrender.com/actuator/health
```

## ✅ Final URLs

After deployment, your MediFlow services will be available at:

- **App**: <https://mediflow-app.onrender.com>
- **API Docs**: <https://mediflow-swagger.onrender.com>
- **Health Checks**: <https://mediflow-auth.onrender.com/actuator/health>

## 🔧 Troubleshooting

**Service Won't Start?**

- Check environment variables are set
- Verify DATABASE_URL format
- Review build logs in Render dashboard

**Database Connection Failed?**

- Ensure DATABASE_URL uses correct format: `jdbc:postgresql://user:pass@host:port/db`
- Verify PostgreSQL service is running

**Blueprint Deployment Failed?**

- Create database manually first
- Use manual deployment as fallback

For complete details, see [MediFlow Complete Guide](MEDIFLOW_COMPLETE_GUIDE.md)

---

🏥 **MediFlow** - Your Health, Delivered Seamlessly
