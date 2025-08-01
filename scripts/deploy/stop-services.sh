#!/bin/bash

# Service shutdown script for Mail Order Pharmacy
# This script stops all services using Docker Compose

set -e

echo "=== Stopping Mail Order Pharmacy Services ==="

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

# Check if docker-compose.yml exists
if [ ! -f "docker-compose.yml" ]; then
    print_error "docker-compose.yml not found in current directory"
    exit 1
fi

# Parse command line arguments
REMOVE_VOLUMES=""
REMOVE_IMAGES=""
while [[ $# -gt 0 ]]; do
    case $1 in
        -v|--volumes)
            REMOVE_VOLUMES="--volumes"
            shift
            ;;
        --rmi)
            REMOVE_IMAGES="--rmi all"
            shift
            ;;
        -h|--help)
            echo "Usage: $0 [options]"
            echo "Options:"
            echo "  -v, --volumes     Remove named volumes declared in compose file"
            echo "  --rmi all         Remove all images used by services"
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

print_status "Stopping services"

# Stop and remove containers
docker-compose down $REMOVE_VOLUMES $REMOVE_IMAGES

print_success "All services stopped successfully"

# Show cleanup information if volumes were removed
if [ ! -z "$REMOVE_VOLUMES" ]; then
    echo ""
    echo "⚠️  Database volumes were removed - all data has been deleted"
    echo "   Run './scripts/db-init/init-databases.sh' before next startup"
fi

if [ ! -z "$REMOVE_IMAGES" ]; then
    echo ""
    echo "⚠️  Docker images were removed - next startup will require rebuild"
    echo "   Run './scripts/build/build-all.sh' to rebuild images"
fi

echo ""
echo "To restart services: ./scripts/deploy/start-services.sh"