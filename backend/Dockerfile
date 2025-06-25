# syntax=docker/dockerfile:1
# Build stage with Maven pre-installed
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first to leverage Docker cache
COPY pom.xml .

# Download dependencies - this layer will be cached
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (dependencies are already cached)
RUN mvn clean package -DskipTests -B

# Runtime stage - minimal JRE image
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/web-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=test"]