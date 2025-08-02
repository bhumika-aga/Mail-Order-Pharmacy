#!/bin/bash
cd backend/drugs-microservice
chmod +x ./mvnw
./mvnw clean package -DskipTests -Dspring.profiles.active=prod