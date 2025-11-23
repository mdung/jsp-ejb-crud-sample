-- Employee CRUD Demo - Database Schema
-- Compatible with MySQL and PostgreSQL

-- For MySQL
-- CREATE DATABASE IF NOT EXISTS employee_db;
-- USE employee_db;

-- For PostgreSQL
-- CREATE DATABASE employee_db;

-- Employee Table
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,  -- For MySQL
    -- id BIGSERIAL PRIMARY KEY,  -- For PostgreSQL (uncomment if using PostgreSQL)
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- For PostgreSQL, use this instead:
-- CREATE TABLE IF NOT EXISTS employees (
--     id BIGSERIAL PRIMARY KEY,
--     name VARCHAR(100) NOT NULL,
--     email VARCHAR(100) NOT NULL UNIQUE,
--     department VARCHAR(50) NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- Sample Data
INSERT INTO employees (name, email, department) VALUES
('John Doe', 'john.doe@example.com', 'IT'),
('Jane Smith', 'jane.smith@example.com', 'HR'),
('Bob Johnson', 'bob.johnson@example.com', 'Finance');

