# Functional Test Cases - Employee CRUD System

## Test Suite: Employee Management System

### Test Environment
- Application Server: WildFly
- Database: MySQL/PostgreSQL
- Browser: Chrome (for UI tests)
- Java Version: Java 6

---

## Test Case 1: Create Employee - Valid Data

**Test Case ID:** TC_EMP_001  
**Priority:** High  
**Type:** Functional

**Preconditions:**
- Application is deployed and running
- Database is accessible
- User is on employee list page

**Test Steps:**
1. Navigate to employee list page
2. Click "Add New Employee" button
3. Enter valid data:
   - Name: "John Doe"
   - Email: "john.doe@test.com"
   - Department: "IT"
4. Click "Create Employee" button

**Expected Result:**
- Employee is created successfully
- Success message is displayed: "Employee created successfully with ID: [id]"
- New employee appears in the employee list
- All entered data is correctly saved

**Postconditions:**
- Employee record exists in database
- Employee is visible in list

---

## Test Case 2: Create Employee - Duplicate Email

**Test Case ID:** TC_EMP_002  
**Priority:** High  
**Type:** Validation

**Preconditions:**
- Employee with email "john.doe@test.com" already exists

**Test Steps:**
1. Navigate to employee form
2. Enter data:
   - Name: "Jane Smith"
   - Email: "john.doe@test.com" (duplicate)
   - Department: "HR"
3. Click "Create Employee" button

**Expected Result:**
- Error message displayed: "Email already exists: john.doe@test.com"
- Employee is NOT created
- Form remains populated with entered data
- User can correct and resubmit

---

## Test Case 3: Create Employee - Missing Required Fields

**Test Case ID:** TC_EMP_003  
**Priority:** High  
**Type:** Validation

**Test Steps:**
1. Navigate to employee form
2. Leave Name field empty
3. Enter email and department
4. Click "Create Employee" button

**Expected Result:**
- HTML5 validation prevents submission (if browser supports)
- OR error message: "Employee name is required"
- Form is not submitted

**Test Steps (Alternative):**
1. Leave Email field empty
2. Enter name and department
3. Click "Create Employee" button

**Expected Result:**
- Error message: "Employee email is required"

**Test Steps (Alternative):**
1. Leave Department field empty
2. Enter name and email
3. Click "Create Employee" button

**Expected Result:**
- Error message: "Employee department is required"

---

## Test Case 4: Create Employee - Invalid Email Format

**Test Case ID:** TC_EMP_004  
**Priority:** Medium  
**Type:** Validation

**Test Steps:**
1. Navigate to employee form
2. Enter data:
   - Name: "Test User"
   - Email: "invalid-email" (no @ symbol)
   - Department: "IT"
3. Click "Create Employee" button

**Expected Result:**
- Error message: "Invalid email format"
- Employee is NOT created

---

## Test Case 5: View Employee Details

**Test Case ID:** TC_EMP_005  
**Priority:** Medium  
**Type:** Functional

**Preconditions:**
- At least one employee exists in the system

**Test Steps:**
1. Navigate to employee list page
2. Click "View" button for an employee

**Expected Result:**
- Employee detail page is displayed
- All employee information is shown correctly:
  - ID
  - Name
  - Email
  - Department
- "Edit" and "Back to List" buttons are visible

---

## Test Case 6: Update Employee - Valid Data

**Test Case ID:** TC_EMP_006  
**Priority:** High  
**Type:** Functional

**Preconditions:**
- Employee with ID 1 exists

**Test Steps:**
1. Navigate to employee list
2. Click "Edit" for employee ID 1
3. Modify the name from "John Doe" to "John Smith"
4. Click "Update Employee" button

**Expected Result:**
- Success message: "Employee updated successfully"
- Employee list is displayed
- Updated employee shows new name "John Smith"
- Other fields remain unchanged
- Database reflects the update

---

## Test Case 7: Update Employee - Duplicate Email (Different Employee)

**Test Case ID:** TC_EMP_007  
**Priority:** High  
**Type:** Validation

**Preconditions:**
- Employee A with email "employeeA@test.com" exists
- Employee B with email "employeeB@test.com" exists

**Test Steps:**
1. Edit Employee B
2. Change email to "employeeA@test.com"
3. Click "Update Employee" button

**Expected Result:**
- Error message: "Email already exists: employeeA@test.com"
- Update is NOT saved
- Form remains populated with entered data

---

## Test Case 8: Update Employee - Same Email (Same Employee)

**Test Case ID:** TC_EMP_008  
**Priority:** Medium  
**Type:** Functional

**Preconditions:**
- Employee with email "john@test.com" exists

**Test Steps:**
1. Edit the employee
2. Keep the same email "john@test.com"
3. Change other fields
4. Click "Update Employee" button

**Expected Result:**
- Update succeeds (same email is allowed for same employee)
- Success message displayed
- Changes are saved

---

## Test Case 9: Delete Employee

**Test Case ID:** TC_EMP_009  
**Priority:** High  
**Type:** Functional

**Preconditions:**
- Employee with ID 1 exists

**Test Steps:**
1. Navigate to employee list
2. Click "Delete" button for employee ID 1
3. Confirm deletion in browser dialog

**Expected Result:**
- Confirmation dialog appears
- After confirmation, success message: "Employee deleted successfully"
- Employee is removed from the list
- Employee is deleted from database

---

## Test Case 10: Delete Employee - Cancel

**Test Case ID:** TC_EMP_010  
**Priority:** Low  
**Type:** Functional

**Preconditions:**
- Employee with ID 1 exists

**Test Steps:**
1. Navigate to employee list
2. Click "Delete" button for employee ID 1
3. Cancel deletion in browser dialog

**Expected Result:**
- Employee remains in the list
- No deletion occurs
- Database unchanged

---

## Test Case 11: List All Employees

**Test Case ID:** TC_EMP_011  
**Priority:** High  
**Type:** Functional

**Preconditions:**
- Multiple employees exist in database

**Test Steps:**
1. Navigate to employee list page (or root URL)

**Expected Result:**
- All employees are displayed in a table
- Table shows: ID, Name, Email, Department
- Each row has View, Edit, Delete actions
- "Add New Employee" button is visible
- Employees are sorted by ID

---

## Test Case 12: List Employees - Empty Database

**Test Case ID:** TC_EMP_012  
**Priority:** Low  
**Type:** Functional

**Preconditions:**
- No employees exist in database

**Test Steps:**
1. Navigate to employee list page

**Expected Result:**
- Message displayed: "No employees found."
- Table structure is visible
- "Add New Employee" button is available

---

## Test Case 13: Field Length Validation

**Test Case ID:** TC_EMP_013  
**Priority:** Medium  
**Type:** Validation

**Test Steps:**
1. Navigate to employee form
2. Enter data exceeding max length:
   - Name: 101 characters
   - Email: 101 characters
   - Department: 51 characters
3. Attempt to submit

**Expected Result:**
- HTML5 maxlength validation prevents excessive input
- OR database constraint error is handled gracefully
- Appropriate error message displayed

---

## Test Case 14: Special Characters in Fields

**Test Case ID:** TC_EMP_014  
**Priority:** Low  
**Type:** Functional

**Test Steps:**
1. Create employee with special characters:
   - Name: "O'Brien-Smith"
   - Email: "test+tag@example.com"
   - Department: "R&D"
2. Submit form

**Expected Result:**
- Employee is created successfully
- All special characters are preserved
- Data displays correctly in list and detail views

---

## Test Case 15: SQL Injection Prevention

**Test Case ID:** TC_EMP_015  
**Priority:** Critical  
**Type:** Security

**Test Steps:**
1. Attempt to create employee with SQL injection:
   - Name: "'; DROP TABLE employees; --"
   - Email: "test@test.com"
   - Department: "IT"
2. Submit form

**Expected Result:**
- Employee is created with literal string value
- No SQL execution occurs
- Database remains intact
- Special characters are escaped/parameterized

---

---

## Test Case 16: Create Employee Performance - Valid Data

**Test Case ID:** TC_PERF_001  
**Priority:** High  
**Type:** Functional

**Preconditions:**
- Employee with ID 1 exists
- User is on employee detail page

**Test Steps:**
1. Navigate to employee detail page for employee ID 1
2. Click "Add/Update Performance" button
3. Enter valid performance data:
   - Month: "2025-11"
   - Performance Score: 85.5
   - Rating: "Good"
   - Notes: "Good performance this month"
4. Click "Save Performance" button

**Expected Result:**
- Performance record is created successfully
- Success message is displayed
- Redirected to employee detail page
- Performance can be viewed in performance history

---

## Test Case 17: Create Employee Performance - Invalid Score Range

**Test Case ID:** TC_PERF_002  
**Priority:** High  
**Type:** Validation

**Preconditions:**
- Employee with ID 1 exists

**Test Steps:**
1. Navigate to performance form for employee ID 1
2. Enter data:
   - Month: "2025-11"
   - Performance Score: 150 (exceeds 100)
   - Rating: "Excellent"
3. Click "Save Performance" button

**Expected Result:**
- Error message: "Performance score must be between 0 and 100"
- Performance is NOT saved
- Form remains populated

**Alternative Test:**
- Enter negative score: -10
- Same error expected

---

## Test Case 18: Create Employee Performance - Invalid Month Format

**Test Case ID:** TC_PERF_003  
**Priority:** Medium  
**Type:** Validation

**Test Steps:**
1. Navigate to performance form
2. Enter data:
   - Month: "2025/11" (invalid format)
   - Performance Score: 80
   - Rating: "Good"
3. Click "Save Performance" button

**Expected Result:**
- HTML5 validation prevents submission (pattern validation)
- OR error message: "Invalid month format. Must be YYYY-MM (e.g., 2025-11)"
- Form is not submitted

---

## Test Case 19: Create Employee Performance - Missing Required Fields

**Test Case ID:** TC_PERF_004  
**Priority:** High  
**Type:** Validation

**Test Steps:**
1. Navigate to performance form
2. Leave Month field empty
3. Enter score and rating
4. Click "Save Performance" button

**Expected Result:**
- HTML5 validation prevents submission
- OR error message: "Month is required"

**Alternative Tests:**
- Leave Performance Score empty → Error: "Performance score is required"
- Leave Rating empty → Error: "Rating is required"

---

## Test Case 20: Update Existing Performance Record

**Test Case ID:** TC_PERF_005  
**Priority:** High  
**Type:** Functional

**Preconditions:**
- Performance record exists for employee ID 1, month "2025-11"

**Test Steps:**
1. Navigate to performance form for employee ID 1, month "2025-11"
2. Update performance score from 80 to 90
3. Change rating from "Good" to "Excellent"
4. Update notes
5. Click "Save Performance" button

**Expected Result:**
- Existing performance record is updated (not duplicated)
- Success message displayed
- Updated data is saved correctly
- Only one record exists for that month

---

## Test Case 21: View Performance History

**Test Case ID:** TC_PERF_006  
**Priority:** Medium  
**Type:** Functional

**Preconditions:**
- Employee with ID 1 exists
- Multiple performance records exist for employee ID 1

**Test Steps:**
1. Navigate to employee detail page for employee ID 1
2. Click "View Performance History" button

**Expected Result:**
- Performance history page is displayed
- All performance records are shown in a table
- Records are sorted by month (newest first)
- Table shows: Month, Performance Score, Rating, Notes, Created At
- "Add New Performance" button is visible

---

## Test Case 22: View Performance History - No Records

**Test Case ID:** TC_PERF_007  
**Priority:** Low  
**Type:** Functional

**Preconditions:**
- Employee with ID 1 exists
- No performance records exist for employee ID 1

**Test Steps:**
1. Navigate to performance history for employee ID 1

**Expected Result:**
- Message displayed: "No performance records found."
- Table structure is visible
- "Add New Performance" button is available

---

## Test Case 23: Performance Rating Validation

**Test Case ID:** TC_PERF_008  
**Priority:** Medium  
**Type:** Validation

**Test Steps:**
1. Navigate to performance form
2. Enter valid month and score
3. Select invalid rating (if possible via direct form manipulation)
4. Submit form

**Expected Result:**
- Only valid ratings are selectable: Excellent, Good, Average, Poor
- OR error message: "Invalid rating. Must be: Excellent, Good, Average, or Poor"

---

## Test Case 24: Multiple Performance Records for Different Months

**Test Case ID:** TC_PERF_009  
**Priority:** Medium  
**Type:** Functional

**Preconditions:**
- Employee with ID 1 exists

**Test Steps:**
1. Create performance record for month "2025-10"
2. Create performance record for month "2025-11"
3. Create performance record for month "2025-12"
4. View performance history

**Expected Result:**
- All three records are displayed
- Each record has correct month
- Records are sorted by month (descending)
- No duplicate records

---

## Test Case 25: Performance Score Decimal Values

**Test Case ID:** TC_PERF_010  
**Priority:** Low  
**Type:** Functional

**Test Steps:**
1. Create performance with decimal score: 87.5
2. View performance history

**Expected Result:**
- Decimal value is preserved and displayed correctly
- Score shows as "87.5" in history

---

## Test Summary

| Test Case ID | Status | Priority | Type |
|--------------|--------|----------|------|
| TC_EMP_001 | Pass | High | Functional |
| TC_EMP_002 | Pass | High | Validation |
| TC_EMP_003 | Pass | High | Validation |
| TC_EMP_004 | Pass | Medium | Validation |
| TC_EMP_005 | Pass | Medium | Functional |
| TC_EMP_006 | Pass | High | Functional |
| TC_EMP_007 | Pass | High | Validation |
| TC_EMP_008 | Pass | Medium | Functional |
| TC_EMP_009 | Pass | High | Functional |
| TC_EMP_010 | Pass | Low | Functional |
| TC_EMP_011 | Pass | High | Functional |
| TC_EMP_012 | Pass | Low | Functional |
| TC_EMP_013 | Pass | Medium | Validation |
| TC_EMP_014 | Pass | Low | Functional |
| TC_EMP_015 | Pass | Critical | Security |
| TC_PERF_001 | Pass | High | Functional |
| TC_PERF_002 | Pass | High | Validation |
| TC_PERF_003 | Pass | Medium | Validation |
| TC_PERF_004 | Pass | High | Validation |
| TC_PERF_005 | Pass | High | Functional |
| TC_PERF_006 | Pass | Medium | Functional |
| TC_PERF_007 | Pass | Low | Functional |
| TC_PERF_008 | Pass | Medium | Validation |
| TC_PERF_009 | Pass | Medium | Functional |
| TC_PERF_010 | Pass | Low | Functional |

