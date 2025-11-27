-- Insert 1 related record for employees and employee_performance tables
-- Compatible with PostgreSQL (primary) and MySQL

-- ============================================
-- FOR POSTGRESQL
-- ============================================

-- Step 1: Insert 1 employee record
INSERT INTO employees (name, email, department, active, date_of_birth) 
VALUES ('John Doe', 'john.doe@example.com', 'IT', true, '1990-05-15')
ON CONFLICT (email) DO NOTHING
RETURNING id;

-- Step 2: Insert 1 related performance record for the employee above
-- Note: Replace 1 with the actual employee_id if you need to specify
INSERT INTO employee_performance (employee_id, month, performance_score, rating, notes) 
VALUES (
    (SELECT id FROM employees WHERE email = 'john.doe@example.com'),  -- Get employee_id from email
    '2025-11',           -- Current month
    95.50,              -- Performance score
    'Excellent',        -- Rating
    'Outstanding performance this month. Exceeded all expectations.'
)
ON CONFLICT (employee_id, month) DO UPDATE 
SET performance_score = EXCLUDED.performance_score,
    rating = EXCLUDED.rating,
    notes = EXCLUDED.notes;

-- Verify the inserted records
SELECT 
    e.id as employee_id,
    e.name,
    e.email,
    e.department,
    e.date_of_birth,
    ep.month,
    ep.performance_score,
    ep.rating,
    ep.notes
FROM employees e
LEFT JOIN employee_performance ep ON e.id = ep.employee_id
WHERE e.email = 'john.doe@example.com';

-- ============================================
-- FOR MYSQL (Alternative version)
-- ============================================
/*
-- Step 1: Insert 1 employee record
INSERT IGNORE INTO employees (name, email, department, active, date_of_birth) 
VALUES ('John Doe', 'john.doe@example.com', 'IT', true, '1990-05-15');

-- Step 2: Insert 1 related performance record
INSERT INTO employee_performance (employee_id, month, performance_score, rating, notes) 
VALUES (
    (SELECT id FROM employees WHERE email = 'john.doe@example.com'),
    '2025-11',
    95.50,
    'Excellent',
    'Outstanding performance this month. Exceeded all expectations.'
)
ON DUPLICATE KEY UPDATE 
    performance_score = VALUES(performance_score),
    rating = VALUES(rating),
    notes = VALUES(notes);

-- Verify the inserted records
SELECT 
    e.id as employee_id,
    e.name,
    e.email,
    e.department,
    e.date_of_birth,
    ep.month,
    ep.performance_score,
    ep.rating,
    ep.notes
FROM employees e
LEFT JOIN employee_performance ep ON e.id = ep.employee_id
WHERE e.email = 'john.doe@example.com';
*/




