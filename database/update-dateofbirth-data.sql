-- Update date_of_birth for existing employees
-- Run this after adding the date_of_birth column

-- For PostgreSQL
UPDATE employees SET date_of_birth = '1990-05-15' WHERE id = 1;
UPDATE employees SET date_of_birth = '1985-08-22' WHERE id = 2;
UPDATE employees SET date_of_birth = '1992-11-10' WHERE id = 3;

-- Verify the updates
SELECT id, name, email, date_of_birth FROM employees ORDER BY id;





