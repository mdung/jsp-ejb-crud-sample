# Database Migration - Add Active Column

## Problem
The `employees` table is **missing the `active` column**, which causes errors when the application tries to:
- Load employees (SELECT includes `active` column)
- Update employee status
- Display active/inactive status

## Solution
Run the migration script to add the `active` column.

## Steps to Fix

### Option 1: Using psql Command Line

```cmd
psql -U your_username -d your_database -f database/add-active-column-now.sql
```

Or if you know your connection details:
```cmd
psql -h localhost -U postgres -d employee_db -f database/add-active-column-now.sql
```

### Option 2: Using pgAdmin or Database Tool

1. Open your database tool (pgAdmin, DBeaver, etc.)
2. Connect to your database
3. Open the SQL script: `database/add-active-column-now.sql`
4. Execute the script

### Option 3: Direct SQL Command

Run this SQL in your database:

```sql
ALTER TABLE employees ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE;
UPDATE employees SET active = TRUE WHERE active IS NULL;
```

## Verify

After running the migration, verify the column was added:

```sql
SELECT column_name, data_type, column_default 
FROM information_schema.columns 
WHERE table_name = 'employees' 
AND column_name = 'active';
```

You should see:
```
 column_name | data_type | column_default
-------------+-----------+----------------
 active      | boolean   | true
```

## Expected Result

After adding the column:
- ✅ No more "Error" messages on employee-list page
- ✅ Employees will load successfully
- ✅ Active/Inactive status will display
- ✅ Activate/Deactivate buttons will work

## Current Database Schema

**employees table should have:**
- id
- name
- email
- department
- **active** ← **MISSING - NEEDS TO BE ADDED**
- created_at
- updated_at
- last_month_performance
- performance_rating

## After Migration

1. Restart WildFly (if needed)
2. Test: http://localhost:8080/employee-demo/employee-list.xhtml
3. Errors should be gone!

---

**Status**: ⚠️ **MIGRATION REQUIRED**

