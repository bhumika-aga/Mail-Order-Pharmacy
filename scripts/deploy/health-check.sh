#!/bin/bash

# Health check script for Mail Order Pharmacy services
# This script checks the health status of all running services

set -e

echo "=== Mail Order Pharmacy Health Check ==="

# Function to print colored output
print_success() {
    echo -e "\033[1;32m✓ $1\033[0m"
}

print_error() {
    echo -e "\033[1;31m✗ $1\033[0m"
}

print_warning() {
    echo -e "\033[1;33m⚠ $1\033[0m"
}

print_info() {
    echo -e "\033[1;36mℹ $1\033[0m"
}

# Function to check service health
check_service() {
    local service_name=$1
    local url=$2
    local timeout=10
    
    echo -n "Checking $service_name... "
    
    if curl -f -s --max-time $timeout "$url" > /dev/null 2>&1; then
        print_success "$service_name is healthy"
        return 0
    else
        print_error "$service_name is not responding"
        return 1
    fi
}

# Function to check Docker container status
check_container() {
    local container_name=$1
    
    if docker ps --format "table {{.Names}}" | grep -q "$container_name"; then
        local status=$(docker ps --format "table {{.Names}}\t{{.Status}}" | grep "$container_name" | awk '{print $2}')
        if [[ $status == "Up" ]]; then
            return 0
        fi
    fi
    return 1
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running"
    exit 1
fi

echo "Checking Docker containers..."

# Check container status
containers=("auth-microservice" "drugs-microservice" "subscription-microservice" "refill-microservice" "swagger-aggregator" "member-portal")
all_containers_running=true

for container in "${containers[@]}"; do
    if check_container "$container"; then
        print_success "$container container is running"
    else
        print_error "$container container is not running"
        all_containers_running=false
    fi
done

if ! $all_containers_running; then
    print_warning "Some containers are not running. Use 'docker-compose ps' for details."
    echo ""
fi

echo ""
echo "Checking service endpoints..."

# Check service health endpoints
services=(
    "Frontend:http://localhost:3000"
    "Auth Service:http://localhost:8084/actuator/health"  
    "Drugs Service:http://localhost:8081/actuator/health"
    "Subscription Service:http://localhost:8082/actuator/health"
    "Refill Service:http://localhost:8083/actuator/health"
    "Swagger Aggregator:http://localhost:8085/actuator/health"
)

healthy_services=0
total_services=${#services[@]}

for service in "${services[@]}"; do
    IFS=':' read -r name url <<< "$service"
    if check_service "$name" "$url"; then
        ((healthy_services++))
    fi
done

echo ""
echo "=== Health Check Summary ==="
echo "Healthy services: $healthy_services/$total_services"

if [ $healthy_services -eq $total_services ]; then
    print_success "All services are healthy!"
    echo ""
    echo "Service URLs:"
    echo "  Frontend:          http://localhost:3000"
    echo "  Auth Service:      http://localhost:8084"
    echo "  Drugs Service:     http://localhost:8081"
    echo "  Subscription:      http://localhost:8082"
    echo "  Refill Service:    http://localhost:8083"
    echo "  Swagger UI:        http://localhost:8085"
else
    print_warning "Some services are not healthy"
    echo ""
    echo "Troubleshooting steps:"
    echo "1. Check logs: docker-compose logs [service-name]"
    echo "2. Restart services: docker-compose restart"
    echo "3. Check container status: docker-compose ps"
    exit 1
fi