-- Employee CRUD Demo - Safe Create or Alter Script
-- Compatible with PostgreSQL (primary) and MySQL
-- This script can be run multiple times safely - it will create table if not exists, or add missing columns

-- ============================================
-- FOR POSTGRESQL
-- ============================================

-- Step 1: Create table if it doesn't exist
CREATE TABLE IF NOT EXISTS employees (
    id BIGSERIAL PRIMARY KEY,
    firstname VARCHAR(50),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Step 2: Create employee_performance table if it doesn't exist
CREATE TABLE IF NOT EXISTS employee_performance (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    month VARCHAR(7) NOT NULL,  -- Format: '2025-11'
    performance_score NUMERIC(5,2) CHECK (performance_score >= 0 AND performance_score <= 100),
    rating VARCHAR(20),  -- 'Excellent', 'Good', 'Average', 'Poor'
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    UNIQUE(employee_id, month)
);

-- Create indexes for employee_performance
CREATE INDEX IF NOT EXISTS idx_employee_performance_employee_id ON employee_performance(employee_id);
CREATE INDEX IF NOT EXISTS idx_employee_performance_month ON employee_performance(month);

-- Step 3: Add missing columns to employees table if they don't exist
DO $$ 
BEGIN
    -- Add firstname column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'employees' AND column_name = 'firstname') THEN
        ALTER TABLE employees ADD COLUMN firstname VARCHAR(50);
    END IF;
    -- Add active column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'employees' AND column_name = 'active') THEN
        ALTER TABLE employees ADD COLUMN active BOOLEAN DEFAULT TRUE;
    END IF;
    
    -- Add date_of_birth column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'employees' AND column_name = 'date_of_birth') THEN
        ALTER TABLE employees ADD COLUMN date_of_birth DATE NULL;
    END IF;
    
    -- Add last_month_performance column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'employees' AND column_name = 'last_month_performance') THEN
        ALTER TABLE employees ADD COLUMN last_month_performance NUMERIC(5,2) NULL;
    END IF;
    
    -- Add performance_rating column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'employees' AND column_name = 'performance_rating') THEN
        ALTER TABLE employees ADD COLUMN performance_rating VARCHAR(20) NULL;
    END IF;
END $$;

-- Step 4: Insert sample data (only if not exists)
INSERT INTO employees (firstname, name, email, department, active, date_of_birth) 
VALUES 
    ('John',  'John Doe',  'john.doe@example.com', 'IT',       true, '1990-05-15'),
    ('Jane',  'Jane Smith','jane.smith@example.com','HR',      true, '1985-08-22'),
    ('Bob',   'Bob Johnson','bob.johnson@example.com','Finance',true,'1992-11-10')
ON CONFLICT (email) DO NOTHING;

-- Step 5: Insert sample performance data
INSERT INTO employee_performance (employee_id, month, performance_score, rating, notes) 
VALUES 
    (1, '2025-11', 95.50, 'Excellent', 'Outstanding performance this month'),
    (2, '2025-11', 88.00, 'Good', 'Good work, keep it up'),
    (3, '2025-11', 75.50, 'Average', 'Room for improvement')
ON CONFLICT (employee_id, month) DO NOTHING;

-- Step 6: Update existing records that have NULL date_of_birth
UPDATE employees 
SET date_of_birth = CASE 
    WHEN email = 'john.doe@example.com' THEN '1990-05-15'
    WHEN email = 'jane.smith@example.com' THEN '1985-08-22'
    WHEN email = 'bob.johnson@example.com' THEN '1992-11-10'
    ELSE date_of_birth
END
WHERE date_of_birth IS NULL;

-- Verify the table structure and data
SELECT 
    column_name, 
    data_type, 
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_name = 'employees' 
ORDER BY ordinal_position;

SELECT id, name, email, department, active, date_of_birth FROM employees ORDER BY id;

-- ============================================
-- FOR MYSQL (Alternative version)
-- ============================================
/*
-- Step 1: Create table if it doesn't exist
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Step 2: Add missing columns (MySQL doesn't support IF NOT EXISTS in ALTER, so we check first)
SET @dbname = DATABASE();
SET @tablename = 'employees';
SET @preparedStatement = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = 'active') > 0,
    'SELECT 1',
    'ALTER TABLE employees ADD COLUMN active BOOLEAN DEFAULT TRUE'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @preparedStatement = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = 'date_of_birth') > 0,
    'SELECT 1',
    'ALTER TABLE employees ADD COLUMN date_of_birth DATE NULL'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Step 3: Insert sample data
INSERT IGNORE INTO employees (name, email, department, active, date_of_birth) 
VALUES 
    ('John Doe', 'john.doe@example.com', 'IT', true, '1990-05-15'),
    ('Jane Smith', 'jane.smith@example.com', 'HR', true, '1985-08-22'),
    ('Bob Johnson', 'bob.johnson@example.com', 'Finance', true, '1992-11-10');

-- Step 4: Update existing records
UPDATE employees 
SET date_of_birth = CASE 
    WHEN email = 'john.doe@example.com' THEN '1990-05-15'
    WHEN email = 'jane.smith@example.com' THEN '1985-08-22'
    WHEN email = 'bob.johnson@example.com' THEN '1992-11-10'
    ELSE date_of_birth
END
WHERE date_of_birth IS NULL;
*/

