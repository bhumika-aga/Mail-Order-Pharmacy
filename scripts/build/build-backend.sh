#!/bin/bash

# Build script for backend microservices only
# This script builds all Spring Boot microservices

set -e

echo "=== Building Backend Microservices ==="

# Start time for performance tracking
start_time=$(date +%s)

# Function to print colored output
print_status() {
    echo -e "\033[1;34m=== $1 ===\033[0m"
}

print_success() {
    echo -e "\033[1;32m✓ $1\033[0m"
}

print_error() {
    echo -e "\033[1;31m✗ $1\033[0m"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker first."
    exit 1
fi

# Build auth-microservice
print_status "Building auth-microservice"
cd backend/auth-microservice
if docker build -t pharmacy/auth-microservice:latest .; then
    print_success "auth-microservice built successfully"
else
    print_error "Failed to build auth-microservice"
    exit 1
fi
cd ../..

# Build drugs-microservice
print_status "Building drugs-microservice"
cd backend/drugs-microservice
if docker build -t pharmacy/drugs-microservice:latest .; then
    print_success "drugs-microservice built successfully"
else
    print_error "Failed to build drugs-microservice"
    exit 1
fi
cd ../..

# Build subscription-microservice
print_status "Building subscription-microservice"
cd backend/subscription-microservice
if docker build -t pharmacy/subscription-microservice:latest .; then
    print_success "subscription-microservice built successfully"
else
    print_error "Failed to build subscription-microservice"
    exit 1
fi
cd ../..

# Build refill-microservice
print_status "Building refill-microservice"
cd backend/refill-microservice
if docker build -t pharmacy/refill-microservice:latest .; then
    print_success "refill-microservice built successfully"
else
    print_error "Failed to build refill-microservice"
    exit 1
fi
cd ../..

# Build swagger-aggregator
print_status "Building swagger-aggregator"
cd backend/swagger-aggregator
if docker build -t pharmacy/swagger-aggregator:latest .; then
    print_success "swagger-aggregator built successfully"
else
    print_error "Failed to build swagger-aggregator"
    exit 1
fi
cd ../..

# Calculate build time
end_time=$(date +%s)
duration=$((end_time - start_time))

print_status "Backend Build Summary"
echo "Built Docker images:"
echo "  - pharmacy/auth-microservice:latest"
echo "  - pharmacy/drugs-microservice:latest"
echo "  - pharmacy/subscription-microservice:latest"
echo "  - pharmacy/refill-microservice:latest"
echo "  - pharmacy/swagger-aggregator:latest"
echo ""
print_success "All backend services built successfully in ${duration} seconds"
echo ""
echo "Next steps:"
echo "  - Run './scripts/build/build-frontend.sh' to build frontend"
echo "  - Run 'docker-compose up' to start all services"