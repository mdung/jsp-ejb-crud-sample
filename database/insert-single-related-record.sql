-- Insert 1 employee with 1 related performance record
-- Simple version - can be run multiple times safely

-- ============================================
-- FOR POSTGRESQL
-- ============================================

-- Insert employee (or get existing ID)
WITH emp AS (
    INSERT INTO employees (name, email, department, active, date_of_birth) 
    VALUES ('John Doe', 'john.doe@example.com', 'IT', true, '1990-05-15')
    ON CONFLICT (email) DO UPDATE SET name = EXCLUDED.name
    RETURNING id
)
-- Insert performance for that employee
INSERT INTO employee_performance (employee_id, month, performance_score, rating, notes)
SELECT 
    (SELECT id FROM employees WHERE email = 'john.doe@example.com'),
    '2025-11',
    95.50,
    'Excellent',
    'Outstanding performance this month. Exceeded all expectations.'
ON CONFLICT (employee_id, month) DO UPDATE 
SET performance_score = EXCLUDED.performance_score,
    rating = EXCLUDED.rating,
    notes = EXCLUDED.notes;

-- View the result
SELECT 
    e.id,
    e.name,
    e.email,
    e.department,
    e.date_of_birth,
    ep.month,
    ep.performance_score,
    ep.rating,
    ep.notes
FROM employees e
INNER JOIN employee_performance ep ON e.id = ep.employee_id
WHERE e.email = 'john.doe@example.com';




