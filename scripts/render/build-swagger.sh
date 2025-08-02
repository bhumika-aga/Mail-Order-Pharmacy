#!/bin/bash
cd backend/swagger-aggregator
chmod +x ./mvnw
./mvnw clean package -DskipTests -Dspring.profiles.active=prod