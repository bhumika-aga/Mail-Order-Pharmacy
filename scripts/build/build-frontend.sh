#!/bin/bash

# Build script for frontend only
# This script builds the React member portal

set -e

echo "=== Building Frontend Application ==="

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

# Build member-portal
print_status "Building member-portal"
cd frontend/member-portal

if docker build -t pharmacy/member-portal:latest .; then
    print_success "member-portal built successfully"
else
    print_error "Failed to build member-portal"
    exit 1
fi

cd ../..

# Calculate build time
end_time=$(date +%s)
duration=$((end_time - start_time))

print_status "Frontend Build Summary"
echo "Built Docker image:"
echo "  - pharmacy/member-portal:latest"
echo ""
print_success "Frontend built successfully in ${duration} seconds"
echo ""
echo "Next steps:"
echo "  - Run './scripts/build/build-backend.sh' to build backend services"
echo "  - Run 'docker-compose up' to start all services"
echo "  - Access frontend at http://localhost:3000"