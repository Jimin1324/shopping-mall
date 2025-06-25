-- Initialize database with proper user permissions
-- This script runs automatically when MySQL container starts for the first time

-- Create the shopping_mall database if it doesn't exist
CREATE DATABASE IF NOT EXISTS shopping_mall;

-- Use the database
USE shopping_mall;

-- Ensure database user exists and has proper permissions for all connection types
-- Drop existing users first to avoid conflicts
DROP USER IF EXISTS '${SPRING_DATASOURCE_USERNAME}'@'localhost';
DROP USER IF EXISTS '${SPRING_DATASOURCE_USERNAME}'@'127.0.0.1';
DROP USER IF EXISTS '${SPRING_DATASOURCE_USERNAME}'@'%';

-- Create database user for all host variations with mysql_native_password
CREATE USER '${SPRING_DATASOURCE_USERNAME}'@'localhost' IDENTIFIED WITH mysql_native_password BY '${SPRING_DATASOURCE_PASSWORD}';
CREATE USER '${SPRING_DATASOURCE_USERNAME}'@'127.0.0.1' IDENTIFIED WITH mysql_native_password BY '${SPRING_DATASOURCE_PASSWORD}';
CREATE USER '${SPRING_DATASOURCE_USERNAME}'@'%' IDENTIFIED WITH mysql_native_password BY '${SPRING_DATASOURCE_PASSWORD}';

-- Grant all privileges to all database user variations
GRANT ALL PRIVILEGES ON *.* TO '${SPRING_DATASOURCE_USERNAME}'@'localhost' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO '${SPRING_DATASOURCE_USERNAME}'@'127.0.0.1' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO '${SPRING_DATASOURCE_USERNAME}'@'%' WITH GRANT OPTION;

-- Specifically grant privileges on the shopping_mall database
GRANT ALL PRIVILEGES ON shopping_mall.* TO '${SPRING_DATASOURCE_USERNAME}'@'localhost';
GRANT ALL PRIVILEGES ON shopping_mall.* TO '${SPRING_DATASOURCE_USERNAME}'@'127.0.0.1';
GRANT ALL PRIVILEGES ON shopping_mall.* TO '${SPRING_DATASOURCE_USERNAME}'@'%';

FLUSH PRIVILEGES;