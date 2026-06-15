#!/bin/bash
set -e

echo "Starting MySQL daemon in the background..."
# Run the official mysql entrypoint script but pass mysqld in the background
# We bind it to all interfaces (0.0.0.0) just in case, though localhost is fine for internal
# Using the default mysql entrypoint will ensure the database is initialized
# We override the entrypoint temporarily to run it in background
docker-entrypoint.sh mysqld &

# Wait for MySQL to be fully ready (final TCP server, not the temporary socket server)
echo "Waiting for MySQL to start..."
until mysqladmin ping -h"127.0.0.1" -P3306 -uroot -p"${MYSQL_ROOT_PASSWORD}" --silent; do
    sleep 2
done

echo "MySQL is up and running!"

echo "Starting Spring Boot Application..."
# The database connection details are managed by application-prod.properties
exec java -jar /app/app.jar
