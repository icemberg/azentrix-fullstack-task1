#!/bin/bash
set -e

echo "Starting MySQL daemon in the background..."
# Run the official mysql entrypoint script but pass mysqld in the background
# We bind it to all interfaces (0.0.0.0) just in case, though localhost is fine for internal
# Using the default mysql entrypoint will ensure the database is initialized
# We override the entrypoint temporarily to run it in background
docker-entrypoint.sh mysqld &

# Wait for MySQL to be fully ready
echo "Waiting for MySQL to start..."
until mysqladmin ping -h"localhost" -uroot -p"${MYSQL_ROOT_PASSWORD}" --silent; do
    sleep 2
done

echo "MySQL is up and running!"

echo "Starting Spring Boot Application..."
# The database connection details are overridden via environment variables
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/${MYSQL_DATABASE}?useSSL=false&allowPublicKeyRetrieval=true"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="${MYSQL_ROOT_PASSWORD}"
# We update the schema instead of create-drop so data persists across app restarts (until container dies)
export SPRING_JPA_HIBERNATE_DDL_AUTO="update"
export SPRING_H2_CONSOLE_ENABLED="false"

# Run the backend (which also serves the frontend assets from /static)
exec java -jar /app/app.jar
