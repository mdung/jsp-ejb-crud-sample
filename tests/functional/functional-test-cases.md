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

