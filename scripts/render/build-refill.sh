#!/bin/bash
cd backend/refill-microservice
chmod +x ./mvnw
./mvnw clean package -DskipTests -Dspring.profiles.active=prod