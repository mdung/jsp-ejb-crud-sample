-- Employee CRUD Demo - Complete Database Schema with Date of Birth
-- Compatible with PostgreSQL (primary) and MySQL
-- Run this script to create a fresh database with all columns including date_of_birth

-- For PostgreSQL
CREATE TABLE IF NOT EXISTS employees (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    date_of_birth DATE NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- For MySQL (uncomment if using MySQL instead):
-- CREATE TABLE IF NOT EXISTS employees (
--     id BIGINT PRIMARY KEY AUTO_INCREMENT,
--     name VARCHAR(100) NOT NULL,
--     email VARCHAR(100) NOT NULL UNIQUE,
--     department VARCHAR(50) NOT NULL,
--     active BOOLEAN DEFAULT TRUE,
--     date_of_birth DATE NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
-- );

-- Add date_of_birth column if table already exists (for existing databases)
ALTER TABLE employees ADD COLUMN IF NOT EXISTS date_of_birth DATE NULL;

-- Sample Data with Date of Birth
INSERT INTO employees (name, email, department, active, date_of_birth) VALUES
('John Doe', 'john.doe@example.com', 'IT', true, '1990-05-15'),
('Jane Smith', 'jane.smith@example.com', 'HR', true, '1985-08-22'),
('Bob Johnson', 'bob.johnson@example.com', 'Finance', true, '1992-11-10')
ON CONFLICT (email) DO NOTHING;  -- For PostgreSQL, prevents duplicate email errors

-- For MySQL, use this instead:
-- INSERT IGNORE INTO employees (name, email, department, active, date_of_birth) VALUES
-- ('John Doe', 'john.doe@example.com', 'IT', true, '1990-05-15'),
-- ('Jane Smith', 'jane.smith@example.com', 'HR', true, '1985-08-22'),
-- ('Bob Johnson', 'bob.johnson@example.com', 'Finance', true, '1992-11-10');

-- Verify the data
SELECT id, name, email, department, active, date_of_birth, created_at FROM employees ORDER BY id;




