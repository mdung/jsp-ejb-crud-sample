-- Employee CRUD Demo - Database Schema UPDATE
-- Adding phoneNumber field
-- Compatible with MySQL and PostgreSQL

-- ALTER TABLE to add phoneNumber column
ALTER TABLE employees ADD COLUMN phone_number VARCHAR(20) NULL;

-- Update existing records (optional)
-- UPDATE employees SET phone_number = '555-0001' WHERE id = 1;
-- UPDATE employees SET phone_number = '555-0002' WHERE id = 2;
-- UPDATE employees SET phone_number = '555-0003' WHERE id = 3;

