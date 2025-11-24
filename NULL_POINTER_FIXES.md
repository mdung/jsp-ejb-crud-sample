# Null Pointer Exception Fixes

## ✅ Đã Sửa Tất Cả Lỗi NullPointerException

### 1. **Lỗi khi Edit Employee - employee null**
**Vấn đề:** `employeeBean.employee.name` trả về null khi page load

**Giải pháp:**
- Thêm `rendered="#{not empty employeeBean.employee}"` để chỉ render form khi employee đã được init
- Thêm fallback UI khi employee chưa được load
- Cải thiện error handling trong `showEditForm()`

**Code thay đổi:**
```xhtml
<!-- ✅ ĐÚNG -->
<h:panelGroup rendered="#{not empty employeeBean.employee}">
    <h:form>
        <!-- Form fields -->
    </h:form>
</h:panelGroup>

<h:panelGroup rendered="#{empty employeeBean.employee}">
    <p>Loading form...</p>
    <h:commandLink value="Back to List" action="employee-list?faces-redirect=true"/>
</h:panelGroup>
```

### 2. **Lỗi khi Add New Employee - employee null**
**Vấn đề:** Khi click "Add New Employee", employee có thể chưa được init

**Giải pháp:**
- Cải thiện `showNewForm()` với try-catch
- Đảm bảo employee được init trước khi return navigation
- Thêm null check

**Code thay đổi:**
```java
public String showNewForm() {
    try {
        employee = new Employee();
        action = "create";
        employeeId = null;
        return "employee-form";
    } catch (Exception e) {
        addErrorMessage("Error initializing form: " + e.getMessage());
        return "employee-list?faces-redirect=true";
    }
}
```

### 3. **Button Cancel trong employee-performance-form không hoạt động**
**Vấn đề:** Button Cancel dùng `f:param` nhưng employeeId có thể null

**Giải pháp:**
- Tạo method riêng `cancelPerformanceForm()`
- Method này sẽ load employee từ employeeId và navigate về employee-detail
- Không cần dùng f:param

**Code thay đổi:**
```xhtml
<!-- ✅ ĐÚNG -->
<h:commandLink value="Cancel" 
              action="#{employeeBean.cancelPerformanceForm}"
              styleClass="btn btn-cancel"/>
```

```java
public String cancelPerformanceForm() {
    if (employeeId != null) {
        try {
            employee = getEmployeeService().getEmployeeById(employeeId);
            return "employee-detail";
        } catch (Exception e) {
            addErrorMessage("Error loading employee: " + e.getMessage());
        }
    }
    return "employee-list?faces-redirect=true";
}
```

### 4. **Buttons trong employee-detail không navigate đúng**
**Vấn đề:** Buttons có thể không hoạt động vì employee có thể null

**Giải pháp:**
- Đảm bảo employee được load trong `viewEmployee()`
- Xóa `h:inputHidden` không cần thiết
- Đảm bảo employeeId được set đúng

**Code thay đổi:**
```xhtml
<!-- ✅ ĐÚNG -->
<h:form>
    <!-- Buttons với f:param -->
</h:form>
```

### 5. **Cải thiện showEditForm()**
**Thay đổi:**
- Thêm null check cho id parameter
- Cải thiện error messages
- Đảm bảo employee được load trước khi return

**Code:**
```java
public String showEditForm(Long id) {
    try {
        if (id == null) {
            addErrorMessage("Employee ID is required");
            return "employee-list?faces-redirect=true";
        }
        employee = getEmployeeService().getEmployeeById(id);
        if (employee == null) {
            addErrorMessage("Employee not found with ID: " + id);
            return "employee-list?faces-redirect=true";
        }
        employeeId = id;
        action = "update";
        return "employee-form";
    } catch (Exception e) {
        addErrorMessage("Error loading employee: " + e.getMessage());
        return "employee-list?faces-redirect=true";
    }
}
```

---

## 📝 Files Đã Sửa

1. ✅ `web-module/employee-form.xhtml`
   - Thêm rendered condition cho form
   - Thêm fallback UI khi employee null

2. ✅ `web-module/employee-performance-form.xhtml`
   - Sửa button Cancel - dùng `cancelPerformanceForm()` method

3. ✅ `web-module/employee-detail.xhtml`
   - Xóa `h:inputHidden` không cần thiết

4. ✅ `web-module/src/controller/EmployeeBean.java`
   - Cải thiện `showNewForm()` với error handling
   - Cải thiện `showEditForm()` với null checks
   - Thêm method `cancelPerformanceForm()`

---

## ✅ Kết Quả

- ✅ Build thành công
- ✅ WAR file đã được redeploy
- ✅ Tất cả NullPointerException đã được fix
- ✅ Buttons hoạt động đúng
- ✅ Navigation hoạt động đúng

**Test Cases:**
- ✅ Add New Employee - không còn lỗi null
- ✅ Edit Employee - không còn lỗi null
- ✅ Button Cancel trong performance form - hoạt động
- ✅ Buttons trong employee-detail - navigate đúng

---

**Status: ✅ HOÀN TẤT**

Tất cả lỗi NullPointerException đã được sửa!



