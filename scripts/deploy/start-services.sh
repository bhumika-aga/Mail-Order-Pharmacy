#!/bin/bash

# Service startup script for Mail Order Pharmacy
# This script starts all services using Docker Compose

set -e

echo "=== Starting Mail Order Pharmacy Services ==="

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

# Check if docker-compose.yml exists
if [ ! -f "docker-compose.yml" ]; then
    print_error "docker-compose.yml not found in current directory"
    exit 1
fi

# Initialize databases if needed
print_status "Initializing databases"
if [ -f "./scripts/db-init/init-databases.sh" ]; then
    ./scripts/db-init/init-databases.sh
fi

# Parse command line arguments
DETACHED=""
BUILD=""
while [[ $# -gt 0 ]]; do
    case $1 in
        -d|--detached)
            DETACHED="-d"
            shift
            ;;
        -b|--build)
            BUILD="--build"
            shift
            ;;
        -h|--help)
            echo "Usage: $0 [options]"
            echo "Options:"
            echo "  -d, --detached    Run services in background (detached mode)"
            echo "  -b, --build       Build images before starting"
            echo "  -h, --help        Show this help message"
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            echo "Use -h or --help for usage information"
            exit 1
            ;;
    esac
done

# Start services
print_status "Starting services with Docker Compose"

if [ ! -z "$BUILD" ]; then
    echo "Building images before starting..."
fi

if [ ! -z "$DETACHED" ]; then
    echo "Starting services in detached mode..."
    docker-compose up $DETACHED $BUILD
    print_success "Services started in background"
    echo ""
    echo "Service URLs:"
    echo "  Frontend:          http://localhost:3000"
    echo "  Auth Service:      http://localhost:8084"
    echo "  Drugs Service:     http://localhost:8081"
    echo "  Subscription:      http://localhost:8082"
    echo "  Refill Service:    http://localhost:8083"
    echo "  Swagger UI:        http://localhost:8085"
    echo ""
    echo "Use 'docker-compose logs -f' to view logs"
    echo "Use 'docker-compose down' to stop services"
else
    echo "Starting services in foreground mode..."
    echo "Press Ctrl+C to stop all services"
    docker-compose up $BUILD
fi