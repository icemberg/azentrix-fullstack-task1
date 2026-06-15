# ==========================================
# Stage 1: Build the React Frontend
# ==========================================
FROM node:18-alpine AS frontend-build
WORKDIR /app/frontend

# Install dependencies
COPY personal-budget-trackerf/package.json personal-budget-trackerf/package-lock.json* ./
RUN npm install

# Build the frontend assets
COPY personal-budget-trackerf/ ./
RUN npm run build


# ==========================================
# Stage 2: Build the Spring Boot Backend
# ==========================================
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app/backend

# Copy the pom.xml and download dependencies
COPY personal-budget-tracker/pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code
COPY personal-budget-tracker/src ./src

# Copy the built React assets from Stage 1 into the Spring Boot static folder
# This allows Spring Boot to serve the frontend UI on the same port as the API
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static

# Package the application
RUN mvn clean package -DskipTests


# ==========================================
# Stage 3: Final All-In-One Image (MySQL + Java)
# ==========================================
FROM mysql:8.0.35-debian

# Install OpenJDK 17 Runtime
RUN apt-get update && \
    apt-get install -y openjdk-17-jre-headless && \
    rm -rf /var/lib/apt/lists/*

# Configure basic MySQL Environment Variables for the container
ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=budgetdb

# Copy the packaged Spring Boot Jar
WORKDIR /app
COPY --from=backend-build /app/backend/target/personal-budget-tracker-0.0.1-SNAPSHOT.jar app.jar

# Copy our custom start script
COPY start.sh /start.sh
RUN chmod +x /start.sh

# Expose ports
# 8080: Spring Boot (Frontend & API)
# 3306: MySQL Database
EXPOSE 8080 3306

# Use the custom script to start both MySQL and the Spring Boot app
ENTRYPOINT ["/start.sh"]
