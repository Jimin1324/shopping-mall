#!/bin/bash

# Build JAR locally (faster if you have Maven installed)
echo "Building JAR locally..."
cd "$(dirname "$0")"
./mvnw clean package -DskipTests

# Build minimal Docker image with just the JAR
echo "Building Docker image..."
docker build -f Dockerfile.minimal -t shopping-mall-backend .

echo "Build complete!"