-- Add active column to employees table
-- Run this script in your PostgreSQL database

-- Check if column exists, if not add it
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'employees' 
        AND column_name = 'active'
    ) THEN
        ALTER TABLE employees ADD COLUMN active BOOLEAN DEFAULT TRUE;
        UPDATE employees SET active = TRUE WHERE active IS NULL;
        COMMENT ON COLUMN employees.active IS 'Employee active status: true = active, false = inactive';
        RAISE NOTICE 'Column active added successfully';
    ELSE
        RAISE NOTICE 'Column active already exists';
    END IF;
END $$;

-- Verify the column was added
SELECT column_name, data_type, column_default 
FROM information_schema.columns 
WHERE table_name = 'employees' 
AND column_name = 'active';

