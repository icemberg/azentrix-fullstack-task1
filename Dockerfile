# ==========================================
# Stage 1: Build the Spring Boot Backend
# ==========================================
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app/backend

# Copy the pom.xml and download dependencies
COPY personal-budget-tracker/pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code
COPY personal-budget-tracker/src ./src

# Package the application
RUN mvn clean package -DskipTests


# ==========================================
# Stage 2: Final Image (MySQL + Java)
# ==========================================
FROM mysql:8.0.35-debian

# Install OpenJDK 17 Runtime
# Note: We remove the mysql.list first because the GPG key in older mysql images has expired, breaking apt-get update.
RUN rm -f /etc/apt/sources.list.d/mysql.list && \
    apt-get update && \
    apt-get install -y openjdk-17-jre-headless && \
    rm -rf /var/lib/apt/lists/*

# Configure basic MySQL Environment Variables for the container
ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=budgetdb

# Set Spring profile to production
ENV SPRING_PROFILES_ACTIVE=prod

# Copy the packaged Spring Boot Jar
WORKDIR /app
COPY --from=backend-build /app/backend/target/personal-budget-tracker-0.0.1-SNAPSHOT.jar app.jar

# Copy our custom start script
COPY start.sh /start.sh
RUN chmod +x /start.sh

# Expose ports
# 8080: Spring Boot API
# 3306: MySQL Database
EXPOSE 8080 3306

# Use the custom script to start both MySQL and the Spring Boot app
ENTRYPOINT ["/start.sh"]
