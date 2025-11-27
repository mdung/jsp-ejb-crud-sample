-- ============================================
-- CREATE TABLES: employees and employee_performance
-- ============================================

-- Drop tables if they exist (for clean start)
DROP TABLE IF EXISTS employee_performance CASCADE;
DROP TABLE IF EXISTS employees CASCADE;

-- Create employees table
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    firstname VARCHAR(50) NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    date_of_birth DATE NULL,
    last_month_performance NUMERIC(5,2) NULL,
    performance_rating VARCHAR(20) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create employee_performance table with foreign key
CREATE TABLE employee_performance (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    month VARCHAR(7) NOT NULL,  -- Format: 'YYYY-MM'
    performance_score NUMERIC(5,2) CHECK (performance_score >= 0 AND performance_score <= 100),
    rating VARCHAR(20),  -- 'Excellent', 'Good', 'Average', 'Poor'
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    UNIQUE(employee_id, month)
);

-- Create indexes for better performance
CREATE INDEX idx_employee_performance_employee_id ON employee_performance(employee_id);
CREATE INDEX idx_employee_performance_month ON employee_performance(month);

-- ============================================
-- INSERT 5 EMPLOYEES
-- ============================================

INSERT INTO employees (firstname, name, email, department, active, date_of_birth) 
VALUES 
    ('John', 'John Doe', 'john.doe@example.com', 'IT', true, '1990-05-15'),
    ('Jane', 'Jane Smith', 'jane.smith@example.com', 'HR', true, '1985-08-22'),
    ('Bob', 'Bob Johnson', 'bob.johnson@example.com', 'Finance', true, '1992-11-10'),
    ('Alice', 'Alice Williams', 'alice.williams@example.com', 'Marketing', true, '1988-03-25'),
    ('Charlie', 'Charlie Brown', 'charlie.brown@example.com', 'IT', true, '1995-07-18');

-- ============================================
-- INSERT 5 PERFORMANCE RECORDS (related to employees above)
-- ============================================

-- Get employee IDs and insert performance records
-- Note: Assuming employees are inserted with IDs 1, 2, 3, 4, 5
INSERT INTO employee_performance (employee_id, month, performance_score, rating, notes) 
VALUES 
    (1, '2025-11', 95.50, 'Excellent', 'Outstanding performance this month. Completed all projects on time.'),
    (2, '2025-11', 88.00, 'Good', 'Good work, keep it up. Strong team collaboration.'),
    (3, '2025-11', 75.50, 'Average', 'Room for improvement in time management.'),
    (4, '2025-11', 92.00, 'Excellent', 'Excellent marketing campaign results.'),
    (5, '2025-11', 82.50, 'Good', 'Good technical skills, improving communication.');

-- ============================================
-- VERIFY DATA
-- ============================================

-- Show all employees
SELECT id, firstname, name, email, department, active, date_of_birth 
FROM employees 
ORDER BY id;

-- Show all performance records with employee names
SELECT 
    ep.id,
    e.name AS employee_name,
    e.email,
    ep.month,
    ep.performance_score,
    ep.rating,
    ep.notes,
    ep.created_at
FROM employee_performance ep
JOIN employees e ON ep.employee_id = e.id
ORDER BY ep.employee_id, ep.month;

