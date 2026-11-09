# Member portal: build the Vite SPA, then serve it with an unprivileged nginx.
#   docker build -t member-portal \
#     --build-arg VITE_AUTH_SERVICE_URL=https://auth.example.com ... .

# ---- Build stage ----
FROM node:24-alpine AS builder
WORKDIR /app

# Install all dependencies (Vite and TypeScript are dev dependencies)
COPY package.json package-lock.json ./
RUN npm ci

COPY . .

# Service URLs are compiled into the bundle, so they are build arguments.
ARG VITE_AUTH_SERVICE_URL
ARG VITE_DRUGS_SERVICE_URL
ARG VITE_SUBSCRIPTION_SERVICE_URL
ARG VITE_REFILL_SERVICE_URL
ARG VITE_SHOW_DEMO_CREDENTIALS
RUN npm run build

# ---- Runtime stage: official non-root nginx image (listens on 8080) ----
FROM nginxinc/nginx-unprivileged:1.30-alpine

LABEL description="Member portal frontend for Mail Order Pharmacy"

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 \
    CMD wget -q --spider http://127.0.0.1:8080/ || exit 1
