#!/bin/bash

# Database cleanup script for Mail Order Pharmacy
# This script removes all database files and data directories

set -e

echo "=== Mail Order Pharmacy Database Cleanup ==="

# Confirm with user before proceeding
read -p "This will delete all database files and data. Are you sure? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Cleanup cancelled."
    exit 0
fi

echo "Cleaning up database files..."

# Remove data directories
if [ -d "./data" ]; then
    echo "Removing data directory..."
    rm -rf ./data
    echo "Data directory removed."
else
    echo "Data directory does not exist."
fi

# Remove any .db files in the project root (if any)
if ls *.db 1> /dev/null 2>&1; then
    echo "Removing .db files..."
    rm -f *.db
    echo ".db files removed."
fi

echo "=== Database cleanup completed successfully ==="