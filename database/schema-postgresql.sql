-- Employee CRUD Demo - Database Schema for PostgreSQL
-- Use this file when setting up with PostgreSQL

-- Employee Table (PostgreSQL version)
CREATE TABLE IF NOT EXISTS employees (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sample Data
INSERT INTO employees (name, email, department) VALUES
('John Doe', 'john.doe@example.com', 'IT'),
('Jane Smith', 'jane.smith@example.com', 'HR'),
('Bob Johnson', 'bob.johnson@example.com', 'Finance');

