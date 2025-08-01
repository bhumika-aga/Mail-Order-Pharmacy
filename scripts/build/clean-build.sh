#!/bin/bash

# Clean build script for Mail Order Pharmacy
# This script cleans Docker images and build artifacts, then rebuilds everything

set -e

echo "=== Clean Build for Mail Order Pharmacy ==="

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

print_warning() {
    echo -e "\033[1;33m⚠ $1\033[0m"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker first."
    exit 1
fi

print_status "Cleaning Existing Docker Images"

# Remove existing pharmacy images
echo "Removing existing pharmacy Docker images..."
docker images --format "table {{.Repository}}:{{.Tag}}" | grep "pharmacy/" | while read image; do
    if [[ ! -z "$image" && "$image" != "REPOSITORY:TAG" ]]; then
        echo "Removing $image"
        docker rmi "$image" 2>/dev/null || echo "Image $image not found or in use"
    fi
done

print_status "Cleaning Maven Build Artifacts"

# Clean Maven artifacts
if command -v mvn &> /dev/null; then
    echo "Cleaning Maven target directories..."
    find backend -name "target" -type d -exec rm -rf {} + 2>/dev/null || true
    print_success "Maven artifacts cleaned"
else
    print_warning "Maven not found - skipping Maven clean"
fi

print_status "Cleaning Node.js Build Artifacts"

# Clean Node.js artifacts
if command -v npm &> /dev/null; then
    echo "Cleaning Node.js build artifacts..."
    if [ -d "frontend/member-portal/build" ]; then
        rm -rf frontend/member-portal/build
    fi
    if [ -d "frontend/member-portal/node_modules" ]; then
        echo "Removing node_modules (will be reinstalled during Docker build)..."
        rm -rf frontend/member-portal/node_modules
    fi
    print_success "Node.js artifacts cleaned"
else
    print_warning "Node.js not found - skipping Node.js clean"
fi

print_status "Cleaning Docker Build Cache"

# Clean Docker build cache
echo "Pruning Docker build cache..."
docker builder prune -f

print_status "Starting Clean Build"

# Run the main build script
if [ -f "./scripts/build/build-all.sh" ]; then
    ./scripts/build/build-all.sh
else
    print_error "build-all.sh script not found"
    exit 1
fi

print_success "Clean build completed successfully"