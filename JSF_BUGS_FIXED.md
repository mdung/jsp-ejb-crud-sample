# JSF Bugs Fixed

## ✅ Đã Sửa Các Lỗi

### 1. Lỗi error.xhtml Not Found
**Vấn đề:** `web.xml` có error-page trỏ đến `/error.xhtml` nhưng file không tồn tại

**Đã sửa:**
- Xóa error-page config trong `web.xml`
- Sử dụng JSF error handling mặc định

### 2. Duplicate Table Headers
**Vấn đề:** Table header bị hiển thị 2 lần do có cả `f:facet name="header"` bên ngoài và trong `h:column`

**Đã sửa:**
- Xóa `f:facet name="header"` bên ngoài dataTable
- Chỉ giữ `f:facet name="header"` trong từng `h:column`
- Files: `employee-list.xhtml`, `employee-performance-history.xhtml`

### 3. Add New Performance không chạy
**Vấn đề:** `employeeId` chưa được set đúng khi navigate

**Đã sửa:**
- Set `employeeId = id` trước khi load employee trong `showPerformanceForm()`
- Set `employeeId` trong `viewEmployee()` và `viewPerformanceHistory()`
- Reset form fields khi mở performance form mới

### 4. Converter Error
**Vấn đề:** Dùng `javax.faces.BigDecimal` thay vì `jakarta.faces.BigDecimal`

**Đã sửa:**
- Đổi converter trong `employee-performance-form.xhtml`
- Từ: `converter="javax.faces.BigDecimal"`
- Thành: `converter="jakarta.faces.BigDecimal"`

### 5. Validation trong savePerformance()
**Đã cải thiện:**
- Thêm validation cho month, performanceScore, rating
- Set employee sau khi save để hiển thị trong detail view

---

## 📝 Files Đã Sửa

1. ✅ `web-module/WEB-INF/web.xml` - Xóa error-page
2. ✅ `web-module/employee-list.xhtml` - Sửa duplicate headers
3. ✅ `web-module/employee-performance-history.xhtml` - Sửa duplicate headers
4. ✅ `web-module/employee-performance-form.xhtml` - Sửa converter
5. ✅ `web-module/src/controller/EmployeeBean.java` - Sửa navigation và validation

---

## ✅ Kết Quả

- ✅ Build thành công
- ✅ WAR file đã được redeploy
- ✅ Tất cả lỗi đã được sửa

**Test lại:**
- Click "Add New Performance" ✅
- Click các buttons khác ✅
- Table headers không còn duplicate ✅
- Không còn lỗi error.xhtml ✅



