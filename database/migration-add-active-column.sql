-- Migration: Add active column to employees table
-- Run this script to add the active column to existing database

-- Add active column (default to true for existing records)
ALTER TABLE employees ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT true;

-- Update existing records to be active by default
UPDATE employees SET active = true WHERE active IS NULL;

-- Make active column NOT NULL after setting defaults
ALTER TABLE employees ALTER COLUMN active SET NOT NULL;
ALTER TABLE employees ALTER COLUMN active SET DEFAULT true;

