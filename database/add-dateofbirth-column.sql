-- Add dateOfBirth column to employees table
-- Compatible with both MySQL and PostgreSQL

-- For MySQL
ALTER TABLE employees ADD COLUMN date_of_birth DATE NULL;

-- For PostgreSQL (if using PostgreSQL, use this instead):
-- ALTER TABLE employees ADD COLUMN IF NOT EXISTS date_of_birth DATE NULL;

-- Note: The column is nullable to allow existing records to remain valid
-- You can update existing records later if needed:
-- UPDATE employees SET date_of_birth = '1990-01-01' WHERE date_of_birth IS NULL;





