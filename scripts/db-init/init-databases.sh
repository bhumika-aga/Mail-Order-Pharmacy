#!/bin/bash

# Database initialization script for Mail Order Pharmacy
# This script creates necessary directories and initializes H2 databases

set -e

echo "=== Mail Order Pharmacy Database Initialization ==="

# Create data directories for each microservice
echo "Creating data directories..."

mkdir -p ./data/auth
mkdir -p ./data/drugs  
mkdir -p ./data/subscription
mkdir -p ./data/refill

echo "Data directories created successfully."

# Set proper permissions
echo "Setting permissions..."
chmod -R 755 ./data

echo "=== Database initialization completed successfully ==="
echo ""
echo "Data directories created:"
echo "  - ./data/auth (for auth-microservice)"
echo "  - ./data/drugs (for drugs-microservice)"
echo "  - ./data/subscription (for subscription-microservice)"  
echo "  - ./data/refill (for refill-microservice)"
echo ""
echo "H2 databases will be automatically created when services start."