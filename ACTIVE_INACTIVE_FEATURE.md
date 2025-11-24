# Active/Inactive Employee Feature

## ✅ Feature Completed

### Database Changes
- Added `active` column to `employees` table (BOOLEAN, default TRUE)
- Migration script: `database/schema-postgresql.sql`

### Backend Changes
- **EmployeeService.java**: Added `activateEmployee()` and `deactivateEmployee()` methods
- **EmployeeServiceBean.java**: Implemented activation/deactivation logic
- **EmployeeDAO.java**: 
  - Added `activate()` and `deactivate()` methods
  - Updated all queries to include `active` column
  - Updated `mapResultSetToEmployee()` to handle `active` field

### Frontend Changes
- **employee-list.xhtml**: 
  - Added "Status" column showing Active/Inactive
  - Added Activate/Deactivate buttons in Status column
- **employee-detail.xhtml**: 
  - Added Status display
  - Added Activate/Deactivate button
- **employee-form.xhtml**: 
  - Added checkbox to set active status when creating/editing

### Controller Changes
- **EmployeeBean.java**: 
  - Added `activateEmployee()` method
  - Added `deactivateEmployee()` method
  - Both methods reload employee list after status change

## Build Note

⚠️ **Java Version Requirement**: 
- WildFly 38 requires Java 11+ to run
- The JARs from WildFly 38 are compiled with Java 11 (class version 55.0)
- To build with Java 8, you need Java 8 compatible Jakarta EE 8 JARs
- **Recommended**: Use Java 11+ for building and running

## Testing Checklist
- [ ] Create new employee (should default to Active)
- [ ] Activate an inactive employee
- [ ] Deactivate an active employee
- [ ] Edit employee and change active status
- [ ] Verify status displays correctly in list and detail views
- [ ] Verify status persists after page refresh

