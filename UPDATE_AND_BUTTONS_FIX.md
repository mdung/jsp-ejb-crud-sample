# Update Employee & Buttons Fix

## ✅ Đã Sửa Tất Cả Lỗi

### 1. **Update Employee không hoạt động**

**Vấn đề:**
- Khi click "Update Employee", form không submit đúng
- Employee ID có thể không được bind đúng
- Action có thể không được set đúng

**Giải pháp:**

#### a. Sửa employee-form.xhtml:
- Thêm `id` cho form: `id="employeeForm"`
- Luôn render `employee.id` hidden field (không chỉ khi update)
- Đảm bảo action được bind đúng

**Code thay đổi:**
```xhtml
<!-- ✅ ĐÚNG -->
<h:form id="employeeForm">
    <h:inputHidden id="action" value="#{employeeBean.action}"/>
    <h:inputHidden id="employeeId" value="#{employeeBean.employee.id}"/>
    <!-- Form fields -->
</h:form>
```

#### b. Cải thiện submitForm():
- Thêm validation cho employee null
- Đảm bảo employee.id được set đúng cho update
- Clear employee.id cho create

**Code:**
```java
public String submitForm() {
    try {
        if (employee == null) {
            addErrorMessage("Employee data is missing. Please try again.");
            return null;
        }
        
        if ("update".equals(action)) {
            // If employee.id is null, try to get from employeeId
            if (employee.getId() == null && employeeId != null) {
                employee.setId(employeeId);
            }
            return updateEmployee();
        } else {
            // For create, ensure ID is null
            employee.setId(null);
            return createEmployee();
        }
    } catch (Exception e) {
        addErrorMessage("Error submitting form: " + e.getMessage());
        return null;
    }
}
```

#### c. Cải thiện updateEmployee():
- Thêm validation cho employee và employee.id
- Clear employee và employees sau khi update để force reload
- Better error handling

**Code:**
```java
public String updateEmployee() {
    try {
        if (employee == null) {
            addErrorMessage("Employee data is missing");
            return null;
        }
        if (employee.getId() == null) {
            addErrorMessage("Employee ID is required for update");
            return null;
        }
        getEmployeeService().updateEmployee(employee);
        addSuccessMessage("Employee updated successfully");
        // Clear employee to force reload
        employee = null;
        employees = null; // Clear list to force reload
        return "employee-list?faces-redirect=true";
    } catch (Exception e) {
        addErrorMessage("Error updating employee: " + e.getMessage());
        return null; // Stay on form
    }
}
```

### 2. **Buttons trong employee-detail không hoạt động**

**Vấn đề:**
- Buttons dùng `employeeBean.employee.id` nhưng có thể null
- employeeId có thể không được set đúng

**Giải pháp:**

#### a. Sửa employee-detail.xhtml:
- Thêm `h:inputHidden` để bind employeeId
- Dùng `employeeId` thay vì `employee.id` trong f:param
- Đảm bảo employeeId được set trước khi render buttons

**Code thay đổi:**
```xhtml
<!-- ✅ ĐÚNG -->
<h:form id="detailForm">
    <h:inputHidden id="employeeId" value="#{employeeBean.employeeId}"/>
    <!-- Buttons với f:param dùng employeeId -->
    <h:commandLink action="#{employeeBean.viewPerformanceHistory}">
        <f:param name="id" value="#{employeeBean.employeeId}"/>
    </h:commandLink>
</h:form>
```

#### b. Cải thiện viewEmployee():
- Set employeeId trước khi load employee
- Đảm bảo employeeId matches employee.id
- Better null checks

**Code:**
```java
public String viewEmployee(Long id) {
    try {
        if (id == null) {
            addErrorMessage("Employee ID is required");
            return "employee-list?faces-redirect=true";
        }
        employeeId = id; // Set employeeId first
        employee = getEmployeeService().getEmployeeById(id);
        if (employee == null) {
            addErrorMessage("Employee not found with ID: " + id);
            return "employee-list?faces-redirect=true";
        }
        // Ensure employeeId matches employee.id
        if (employee.getId() != null) {
            employeeId = employee.getId();
        }
        return "employee-detail";
    } catch (Exception e) {
        addErrorMessage("Error loading employee: " + e.getMessage());
        return "employee-list?faces-redirect=true";
    }
}
```

---

## 📝 Files Đã Sửa

1. ✅ `web-module/employee-form.xhtml`
   - Thêm id cho form
   - Luôn render employee.id hidden field
   - Cải thiện form structure

2. ✅ `web-module/employee-detail.xhtml`
   - Thêm h:inputHidden cho employeeId
   - Dùng employeeId thay vì employee.id trong f:param
   - Thêm id cho form

3. ✅ `web-module/src/controller/EmployeeBean.java`
   - Cải thiện `submitForm()` với validation
   - Cải thiện `updateEmployee()` với validation và clear cache
   - Cải thiện `viewEmployee()` với better null checks

---

## ✅ Kết Quả

- ✅ Build thành công
- ✅ WAR file đã được redeploy
- ✅ Update Employee hoạt động đúng
- ✅ Tất cả buttons trong employee-detail hoạt động đúng
- ✅ Navigation hoạt động đúng

**Test Cases:**
- ✅ Edit Employee → Update → Success
- ✅ View Performance History button → Navigate đúng
- ✅ Add/Update Performance button → Navigate đúng
- ✅ Edit button → Navigate đúng
- ✅ Back to List button → Navigate đúng

---

**Status: ✅ HOÀN TẤT**

Tất cả lỗi đã được sửa!



