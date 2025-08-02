#!/bin/bash
cd backend/subscription-microservice
chmod +x ./mvnw
./mvnw clean package -DskipTests -Dspring.profiles.active=prod