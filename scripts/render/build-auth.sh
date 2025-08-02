#!/bin/bash
cd backend/auth-microservice
chmod +x ./mvnw
./mvnw clean package -DskipTests -Dspring.profiles.active=prod