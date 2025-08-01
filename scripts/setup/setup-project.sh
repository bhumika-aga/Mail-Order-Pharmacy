#!/bin/bash

# Project setup script for Mail Order Pharmacy
# This script sets up the development environment

set -e

echo "=== Mail Order Pharmacy Project Setup ==="

# Check if Docker is installed
echo "Checking Docker installation..."
if ! command -v docker &> /dev/null; then
    echo "ERROR: Docker is not installed. Please install Docker first."
    echo "Visit: https://docs.docker.com/get-docker/"
    exit 1
fi

# Check if Docker Compose is installed
echo "Checking Docker Compose installation..."
if ! command -v docker-compose &> /dev/null; then
    echo "ERROR: Docker Compose is not installed. Please install Docker Compose first."
    echo "Visit: https://docs.docker.com/compose/install/"
    exit 1
fi

echo "Docker and Docker Compose are installed ✓"

# Check if Maven is installed (for local development)
echo "Checking Maven installation..."
if ! command -v mvn &> /dev/null; then
    echo "WARNING: Maven is not installed. You'll need Maven for local development."
    echo "Visit: https://maven.apache.org/install.html"
else
    echo "Maven is installed ✓"
fi

# Check if Node.js is installed (for local development)
echo "Checking Node.js installation..."
if ! command -v node &> /dev/null; then
    echo "WARNING: Node.js is not installed. You'll need Node.js for local frontend development."
    echo "Visit: https://nodejs.org/"
else
    echo "Node.js is installed ✓"
    echo "Node.js version: $(node --version)"
fi

# Initialize databases
echo "Initializing databases..."
if [ -f "./scripts/db-init/init-databases.sh" ]; then
    ./scripts/db-init/init-databases.sh
else
    echo "Database initialization script not found. Creating data directories manually..."
    mkdir -p ./data/{auth,drugs,subscription,refill}
    chmod -R 755 ./data
fi

# Install frontend dependencies (if Node.js is available)
if command -v node &> /dev/null; then
    echo "Installing frontend dependencies..."
    cd frontend/member-portal
    npm install
    cd ../..
    echo "Frontend dependencies installed ✓"
fi

echo ""
echo "=== Project Setup Complete ==="
echo ""
echo "Available commands:"
echo "  ./scripts/build/build-all.sh          - Build all services"
echo "  ./scripts/build/build-backend.sh      - Build only backend services"
echo "  ./scripts/build/build-frontend.sh     - Build only frontend"
echo "  docker-compose up                      - Start all services"
echo "  docker-compose up -d                   - Start all services in background"
echo "  docker-compose down                    - Stop all services"
echo ""
echo "Service URLs (when running):"
echo "  Frontend:          http://localhost:3000"
echo "  Auth Service:      http://localhost:8084"
echo "  Drugs Service:     http://localhost:8081"
echo "  Subscription:      http://localhost:8082"
echo "  Refill Service:    http://localhost:8083"
echo "  Swagger UI:        http://localhost:8085"