# Before vs After Comparison - Adding phoneNumber Field

## Overview

This document shows the changes made to add the `phoneNumber` field to the Employee CRUD system. All changes were made systematically across the entire stack.

---

## Summary of Changes

| Component | Files Changed | Lines Added | Lines Modified |
|-----------|--------------|-------------|----------------|
| Database | 1 | 2 | 0 |
| Model | 1 | 15 | 3 |
| DAO | 1 | 8 | 4 |
| Service | 1 | 12 | 0 |
| Servlet | 1 | 2 | 0 |
| JSP | 3 | 15 | 3 |
| **Total** | **8** | **54** | **10** |

---

## Detailed Changes

### 1. Database Schema

**File:** `database/schema.sql` → `updated-version/database/schema-update.sql`

**Before:**
```sql
CREATE TABLE employees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    ...
);
```

**After:**
```sql
-- New migration script
ALTER TABLE employees ADD COLUMN phone_number VARCHAR(20) NULL;
```

**Impact:** 
- Adds new column `phone_number` (nullable, max 20 chars)
- Existing data remains intact
- No breaking changes

---

### 2. Employee Model

**File:** `ejb-module/src/model/Employee.java`

**Before:**
```java
public class Employee implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String department;
    
    // Getters and setters for above fields only
}
```

**After:**
```java
public class Employee implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String department;
    private String phoneNumber;  // NEW FIELD
    
    // NEW: Getter and setter
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    // toString() updated to include phoneNumber
}
```

**Changes:**
- Added `phoneNumber` field
- Added getter/setter methods
- Updated `toString()` method

---

### 3. EmployeeDAO

**File:** `ejb-module/src/dao/EmployeeDAO.java`

**Before:**
```java
// INSERT
String sql = "INSERT INTO employees (name, email, department) VALUES (?, ?, ?)";
pstmt.setString(1, employee.getName());
pstmt.setString(2, employee.getEmail());
pstmt.setString(3, employee.getDepartment());

// SELECT
String sql = "SELECT id, name, email, department FROM employees WHERE id = ?";

// UPDATE
String sql = "UPDATE employees SET name = ?, email = ?, department = ? WHERE id = ?";

// Mapping
employee.setId(rs.getLong("id"));
employee.setName(rs.getString("name"));
employee.setEmail(rs.getString("email"));
employee.setDepartment(rs.getString("department"));
```

**After:**
```java
// INSERT - UPDATED
String sql = "INSERT INTO employees (name, email, department, phone_number) VALUES (?, ?, ?, ?)";
pstmt.setString(1, employee.getName());
pstmt.setString(2, employee.getEmail());
pstmt.setString(3, employee.getDepartment());
pstmt.setString(4, employee.getPhoneNumber());  // NEW

// SELECT - UPDATED
String sql = "SELECT id, name, email, department, phone_number FROM employees WHERE id = ?";

// UPDATE - UPDATED
String sql = "UPDATE employees SET name = ?, email = ?, department = ?, phone_number = ? WHERE id = ?";
pstmt.setString(4, employee.getPhoneNumber());  // NEW

// Mapping - UPDATED
employee.setPhoneNumber(rs.getString("phone_number"));  // NEW
```

**Changes:**
- All SQL queries updated to include `phone_number`
- All parameter bindings updated
- ResultSet mapping updated

---

### 4. EmployeeServiceBean

**File:** `ejb-module/src/ejb/EmployeeServiceBean.java`

**Before:**
```java
private void validateEmployee(Employee employee) throws Exception {
    // ... existing validations ...
    if (employee.getDepartment() == null || employee.getDepartment().trim().isEmpty()) {
        throw new Exception("Employee department is required");
    }
}
```

**After:**
```java
private void validateEmployee(Employee employee) throws Exception {
    // ... existing validations ...
    if (employee.getDepartment() == null || employee.getDepartment().trim().isEmpty()) {
        throw new Exception("Employee department is required");
    }
    
    // NEW: Phone number validation (optional field)
    if (employee.getPhoneNumber() != null && !employee.getPhoneNumber().trim().isEmpty()) {
        String phone = employee.getPhoneNumber().trim();
        if (phone.length() > 20) {
            throw new Exception("Phone number must be 20 characters or less");
        }
    }
}
```

**Changes:**
- Added phone number validation (optional field)
- Length validation (max 20 characters)

---

### 5. EmployeeServlet

**File:** `web-module/src/controller/EmployeeServlet.java`

**Before:**
```java
private void createEmployee(...) {
    Employee employee = new Employee();
    employee.setName(request.getParameter("name"));
    employee.setEmail(request.getParameter("email"));
    employee.setDepartment(request.getParameter("department"));
    // ...
}

private void updateEmployee(...) {
    Employee employee = new Employee();
    employee.setId(Long.parseLong(request.getParameter("id")));
    employee.setName(request.getParameter("name"));
    employee.setEmail(request.getParameter("email"));
    employee.setDepartment(request.getParameter("department"));
    // ...
}
```

**After:**
```java
private void createEmployee(...) {
    Employee employee = new Employee();
    employee.setName(request.getParameter("name"));
    employee.setEmail(request.getParameter("email"));
    employee.setDepartment(request.getParameter("department"));
    employee.setPhoneNumber(request.getParameter("phoneNumber"));  // NEW
    // ...
}

private void updateEmployee(...) {
    Employee employee = new Employee();
    employee.setId(Long.parseLong(request.getParameter("id")));
    employee.setName(request.getParameter("name"));
    employee.setEmail(request.getParameter("email"));
    employee.setDepartment(request.getParameter("department"));
    employee.setPhoneNumber(request.getParameter("phoneNumber"));  // NEW
    // ...
}
```

**Changes:**
- Extract `phoneNumber` parameter in create and update methods

---

### 6. JSP Pages

#### 6.1 employee-list.jsp

**Before:**
```jsp
<thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Email</th>
        <th>Department</th>
        <th>Actions</th>
    </tr>
</thead>
<tbody>
    <td>${employee.id}</td>
    <td>${employee.name}</td>
    <td>${employee.email}</td>
    <td>${employee.department}</td>
    <td>...</td>
</tbody>
```

**After:**
```jsp
<thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Email</th>
        <th>Department</th>
        <th>Phone Number</th>  <!-- NEW -->
        <th>Actions</th>
    </tr>
</thead>
<tbody>
    <td>${employee.id}</td>
    <td>${employee.name}</td>
    <td>${employee.email}</td>
    <td>${employee.department}</td>
    <td>${employee.phoneNumber != null ? employee.phoneNumber : 'N/A'}</td>  <!-- NEW -->
    <td>...</td>
</tbody>
```

#### 6.2 employee-form.jsp

**Before:**
```jsp
<div class="form-group">
    <label for="department">Department <span class="required">*</span></label>
    <input type="text" id="department" name="department" 
           value="${employee.department}" 
           required 
           maxlength="50" />
</div>

<div class="form-group">
    <button type="submit" class="btn btn-primary">...</button>
</div>
```

**After:**
```jsp
<div class="form-group">
    <label for="department">Department <span class="required">*</span></label>
    <input type="text" id="department" name="department" 
           value="${employee.department}" 
           required 
           maxlength="50" />
</div>

<!-- NEW: Phone Number field -->
<div class="form-group">
    <label for="phoneNumber">Phone Number</label>
    <input type="tel" id="phoneNumber" name="phoneNumber" 
           value="${employee.phoneNumber}" 
           maxlength="20" 
           placeholder="e.g., 555-123-4567" />
</div>

<div class="form-group">
    <button type="submit" class="btn btn-primary">...</button>
</div>
```

#### 6.3 employee-detail.jsp

**Before:**
```jsp
<div class="detail-group">
    <div class="detail-label">Department</div>
    <div class="detail-value">${employee.department}</div>
</div>

<div>
    <a href="employee?action=edit&id=${employee.id}" class="btn btn-edit">Edit</a>
</div>
```

**After:**
```jsp
<div class="detail-group">
    <div class="detail-label">Department</div>
    <div class="detail-value">${employee.department}</div>
</div>

<!-- NEW: Phone Number display -->
<div class="detail-group">
    <div class="detail-label">Phone Number</div>
    <div class="detail-value">${employee.phoneNumber != null ? employee.phoneNumber : 'N/A'}</div>
</div>

<div>
    <a href="employee?action=edit&id=${employee.id}" class="btn btn-edit">Edit</a>
</div>
```

---

## Testing Impact

### Updated Test Cases Needed

1. **Create Employee with Phone Number**
   - Test valid phone number formats
   - Test empty/null phone number (optional field)
   - Test phone number length validation

2. **Update Employee Phone Number**
   - Test updating phone number
   - Test clearing phone number (setting to null)

3. **Display Phone Number**
   - Verify phone number appears in list
   - Verify phone number appears in detail view
   - Verify "N/A" displayed when phone number is null

### Updated Selenium Tests Needed

1. Update `EmployeeFormPage.java`:
   - Add `enterPhoneNumber()` method
   - Add `getPhoneNumberValue()` method

2. Update `EmployeeListPage.java`:
   - Verify phone number column exists
   - Verify phone number values displayed

3. Update `EmployeeDetailPage.java`:
   - Add `getEmployeePhoneNumber()` method

4. Update test methods:
   - Include phone number in create/update test scenarios

---

## Deployment Steps

1. **Database Migration:**
   ```sql
   ALTER TABLE employees ADD COLUMN phone_number VARCHAR(20) NULL;
   ```

2. **Rebuild Application:**
   - Recompile EJB module
   - Recompile Web module
   - Package into EAR

3. **Redeploy:**
   - Undeploy old version
   - Deploy new version
   - Verify deployment success

4. **Verify:**
   - Test create with phone number
   - Test update phone number
   - Test display in list and detail views

---

## Rollback Plan

If issues occur:

1. **Database:**
   ```sql
   ALTER TABLE employees DROP COLUMN phone_number;
   ```

2. **Application:**
   - Redeploy previous version (without phoneNumber support)
   - Data loss: phone numbers will be lost, but other data intact

---

## Impact Analysis

### Breaking Changes
- **None** - Field is optional, backward compatible

### Data Migration
- Existing records will have `NULL` phone numbers
- No data loss for existing fields

### Performance Impact
- Minimal - one additional column in SELECT/INSERT/UPDATE
- No additional indexes needed (optional field)

### UI Impact
- New column in list view (table width may need adjustment)
- New field in form (optional, no validation errors if empty)
- New field in detail view

---

## Files Modified Summary

```
✅ database/schema-update.sql (NEW)
✅ ejb-module/src/model/Employee.java
✅ ejb-module/src/dao/EmployeeDAO.java
✅ ejb-module/src/ejb/EmployeeServiceBean.java
✅ web-module/src/controller/EmployeeServlet.java
✅ web-module/employee-list.jsp
✅ web-module/employee-form.jsp
✅ web-module/employee-detail.jsp
```

**Total: 8 files modified/created**

---

## Verification Checklist

- [ ] Database migration script tested
- [ ] Model updated with getter/setter
- [ ] All SQL queries updated (INSERT, SELECT, UPDATE)
- [ ] Validation logic added
- [ ] Servlet parameter extraction updated
- [ ] All JSP pages updated (list, form, detail)
- [ ] Test cases updated
- [ ] Selenium tests updated
- [ ] Application rebuilt and redeployed
- [ ] Manual testing completed
- [ ] No regression in existing functionality

