-- Update existing employees with date of birth
-- Run this if you already have employees in the database

-- Update date_of_birth for existing employees
UPDATE employees SET date_of_birth = '1990-05-15' WHERE id = 1 AND date_of_birth IS NULL;
UPDATE employees SET date_of_birth = '1985-08-22' WHERE id = 2 AND date_of_birth IS NULL;
UPDATE employees SET date_of_birth = '1992-11-10' WHERE id = 3 AND date_of_birth IS NULL;

-- Or update by email (more reliable if IDs might differ)
UPDATE employees SET date_of_birth = '1990-05-15' WHERE email = 'john.doe@example.com' AND date_of_birth IS NULL;
UPDATE employees SET date_of_birth = '1985-08-22' WHERE email = 'jane.smith@example.com' AND date_of_birth IS NULL;
UPDATE employees SET date_of_birth = '1992-11-10' WHERE email = 'bob.johnson@example.com' AND date_of_birth IS NULL;

-- Verify the updates
SELECT id, name, email, department, active, date_of_birth FROM employees ORDER BY id;




