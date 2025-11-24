# Phân Tích: Thêm Tính Năng Performance Hàng Tháng

## Yêu Cầu
Thêm thông tin về **performance hàng tháng** của nhân viên vào hệ thống.

## Phân Tích Các File Cần Sửa

### 📊 TỔNG QUAN
Để thêm tính năng performance hàng tháng, cần sửa **12-15 files** và tạo **2-3 files mới**.

---

## 1. DATABASE LAYER (2 files)

### 1.1. `database/schema.sql` (hoặc tạo mới `database/schema-performance.sql`)
**Cần làm gì:**
- Tạo bảng mới `employee_performance` để lưu performance hàng tháng
- Hoặc thêm cột vào bảng `employees` (nếu chỉ lưu 1 tháng)

**Cấu trúc đề xuất:**
```sql
CREATE TABLE employee_performance (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    month VARCHAR(7) NOT NULL,  -- Format: '2025-11'
    performance_score DECIMAL(5,2),  -- Điểm từ 0-100
    rating VARCHAR(20),  -- 'Excellent', 'Good', 'Average', 'Poor'
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id),
    UNIQUE(employee_id, month)
);
```

**Hoặc nếu đơn giản hơn - thêm vào bảng employees:**
```sql
ALTER TABLE employees ADD COLUMN last_month_performance DECIMAL(5,2);
ALTER TABLE employees ADD COLUMN performance_rating VARCHAR(20);
```

---

## 2. MODEL LAYER (2-3 files)

### 2.1. `ejb-module/src/model/Employee.java`
**Cần sửa:**
- Thêm các field mới:
  ```java
  private BigDecimal lastMonthPerformance;  // hoặc
  private String performanceRating;
  ```
- Thêm getter/setter cho các field mới
- Cập nhật `toString()` method

**Hoặc tạo model mới:**

### 2.2. `ejb-module/src/model/EmployeePerformance.java` (FILE MỚI)
**Cần tạo:**
- Model class mới để đại diện cho performance hàng tháng
- Fields: id, employeeId, month, performanceScore, rating, notes
- Getters/setters
- Constructor

---

## 3. DAO LAYER (2 files)

### 3.1. `ejb-module/src/dao/EmployeeDAO.java`
**Cần sửa:**
- **SELECT queries:** Thêm các cột performance vào SELECT
  ```java
  // Từ: SELECT id, name, email, department FROM employees
  // Thành: SELECT id, name, email, department, last_month_performance, performance_rating FROM employees
  ```
- **UPDATE queries:** Thêm cập nhật performance
  ```java
  // UPDATE employees SET ..., last_month_performance=?, performance_rating=? WHERE id=?
  ```
- **mapResultSetToEmployee():** Map thêm các field performance

**Hoặc nếu dùng bảng riêng:**

### 3.2. `ejb-module/src/dao/EmployeePerformanceDAO.java` (FILE MỚI)
**Cần tạo:**
- `create(EmployeePerformance perf)` - INSERT performance
- `findByEmployeeId(Long employeeId)` - Lấy tất cả performance của 1 nhân viên
- `findByEmployeeIdAndMonth(Long employeeId, String month)` - Lấy performance theo tháng
- `update(EmployeePerformance perf)` - UPDATE performance
- `delete(Long id)` - Xóa performance

---

## 4. EJB SERVICE LAYER (2 files)

### 4.1. `ejb-module/src/ejb/EmployeeService.java`
**Cần sửa:**
- Thêm method mới:
  ```java
  void updateEmployeePerformance(Long employeeId, BigDecimal score, String rating) throws Exception;
  EmployeePerformance getEmployeePerformance(Long employeeId, String month) throws Exception;
  List<EmployeePerformance> getEmployeePerformanceHistory(Long employeeId) throws Exception;
  ```

### 4.2. `ejb-module/src/ejb/EmployeeServiceBean.java`
**Cần sửa:**
- Implement các method mới từ interface
- Thêm validation cho performance score (0-100)
- Thêm validation cho rating
- Cập nhật `validateEmployee()` nếu cần validate performance khi update employee

---

## 5. SERVLET LAYER (1 file)

### 5.1. `web-module/src/controller/EmployeeServlet.java`
**Cần sửa:**
- **doGet():** Thêm action mới:
  ```java
  else if ("performance".equals(action)) {
      showPerformanceForm(request, response, service);
  } else if ("viewPerformance".equals(action)) {
      viewPerformance(request, response, service);
  }
  ```
- **doPost():** Thêm action:
  ```java
  else if ("updatePerformance".equals(action)) {
      updatePerformance(request, response, service);
  }
  ```
- Thêm các method mới:
  - `showPerformanceForm()` - Hiển thị form nhập performance
  - `viewPerformance()` - Xem lịch sử performance
  - `updatePerformance()` - Lưu performance

---

## 6. JSP LAYER (4-5 files)

### 6.1. `web-module/employee-list.jsp`
**Cần sửa:**
- Thêm cột "Performance" vào bảng
- Hiển thị performance rating hoặc score
- Thêm button "View Performance" hoặc "Update Performance"

### 6.2. `web-module/employee-form.jsp`
**Cần sửa:**
- Thêm input fields cho performance (nếu nhập khi tạo/sửa employee)
- Hoặc giữ nguyên nếu performance được quản lý riêng

### 6.3. `web-module/employee-detail.jsp`
**Cần sửa:**
- Thêm section hiển thị performance hiện tại
- Thêm link "View Performance History"

### 6.4. `web-module/employee-performance-form.jsp` (FILE MỚI)
**Cần tạo:**
- Form để nhập/cập nhật performance hàng tháng
- Fields: Month (dropdown), Performance Score (0-100), Rating (dropdown), Notes (textarea)
- Validation

### 6.5. `web-module/employee-performance-history.jsp` (FILE MỚI - TÙY CHỌN)
**Cần tạo:**
- Trang hiển thị lịch sử performance của nhân viên
- Bảng hoặc biểu đồ performance theo tháng
- Có thể dùng chart library để vẽ biểu đồ

---

## 7. CONFIGURATION FILES (0-1 file)

### 7.1. `web-module/WEB-INF/web.xml`
**Cần kiểm tra:**
- Có thể cần thêm servlet mapping mới (nếu tạo servlet riêng cho performance)
- Thường không cần sửa nếu dùng cùng EmployeeServlet

---

## 8. TEST FILES (3-4 files)

### 8.1. `tests/functional/functional-test-cases.md`
**Cần sửa:**
- Thêm test cases cho performance:
  - TC_PERF_001: Create performance record
  - TC_PERF_002: Update performance
  - TC_PERF_003: View performance history
  - TC_PERF_004: Validation (score 0-100)

### 8.2. `tests/selenium/EmployeePerformancePage.java` (FILE MỚI)
**Cần tạo:**
- Page Object cho performance form
- Methods: enterPerformanceScore(), selectRating(), submitPerformance()

### 8.3. `tests/selenium/EmployeeCRUDTest.java`
**Cần sửa:**
- Thêm test methods:
  - `testUpdateEmployeePerformance()`
  - `testViewPerformanceHistory()`

### 8.4. `tests/test-data/employee-test-data.json`
**Cần sửa:**
- Thêm dữ liệu test cho performance

---

## 📋 TÓM TẮT CÁC FILE CẦN SỬA/TẠO

### Files Cần SỬA (10 files):
1. ✅ `database/schema.sql` - Thêm bảng/cột performance
2. ✅ `ejb-module/src/model/Employee.java` - Thêm fields performance
3. ✅ `ejb-module/src/dao/EmployeeDAO.java` - Update SQL queries
4. ✅ `ejb-module/src/ejb/EmployeeService.java` - Thêm methods
5. ✅ `ejb-module/src/ejb/EmployeeServiceBean.java` - Implement methods
6. ✅ `web-module/src/controller/EmployeeServlet.java` - Thêm handlers
7. ✅ `web-module/employee-list.jsp` - Thêm cột performance
8. ✅ `web-module/employee-detail.jsp` - Hiển thị performance
9. ✅ `tests/functional/functional-test-cases.md` - Thêm test cases
10. ✅ `tests/selenium/EmployeeCRUDTest.java` - Thêm tests

### Files Cần TẠO MỚI (3-4 files):
1. 🆕 `ejb-module/src/model/EmployeePerformance.java` (nếu dùng bảng riêng)
2. 🆕 `ejb-module/src/dao/EmployeePerformanceDAO.java` (nếu dùng bảng riêng)
3. 🆕 `web-module/employee-performance-form.jsp`
4. 🆕 `web-module/employee-performance-history.jsp` (tùy chọn)
5. 🆕 `tests/selenium/EmployeePerformancePage.java`

---

## 🔄 FLOW HOẠT ĐỘNG

### Scenario: Cập nhật Performance hàng tháng

```
1. User click "Update Performance" trên employee-list.jsp
   ↓
2. EmployeeServlet.doGet(action="performance")
   ↓
3. Forward đến employee-performance-form.jsp
   ↓
4. User nhập: Month, Score, Rating, Notes
   ↓
5. Submit form → EmployeeServlet.doPost(action="updatePerformance")
   ↓
6. EmployeeServiceBean.updateEmployeePerformance()
   ↓
7. EmployeeDAO.updatePerformance() hoặc EmployeePerformanceDAO.create()
   ↓
8. INSERT/UPDATE vào database
   ↓
9. Redirect về employee-detail.jsp với performance mới
```

---

## ⚠️ QUYẾT ĐỊNH THIẾT KẾ

### Option 1: Performance trong bảng employees (Đơn giản)
- ✅ Chỉ lưu performance tháng gần nhất
- ✅ Dễ implement
- ❌ Không có lịch sử

### Option 2: Bảng riêng employee_performance (Đầy đủ)
- ✅ Lưu được lịch sử nhiều tháng
- ✅ Có thể phân tích xu hướng
- ❌ Phức tạp hơn

**Khuyến nghị:** Option 2 (bảng riêng) để có đầy đủ tính năng.

---

## 📝 CHI TIẾT THAY ĐỔI TỪNG FILE

### File 1: `database/schema-performance.sql` (MỚI)
```sql
-- Tạo bảng performance
CREATE TABLE employee_performance (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    month VARCHAR(7) NOT NULL,
    performance_score DECIMAL(5,2) CHECK (performance_score >= 0 AND performance_score <= 100),
    rating VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    UNIQUE(employee_id, month)
);

-- Index để query nhanh
CREATE INDEX idx_employee_performance_employee_id ON employee_performance(employee_id);
CREATE INDEX idx_employee_performance_month ON employee_performance(month);
```

### File 2: `ejb-module/src/model/EmployeePerformance.java` (MỚI)
```java
package model;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class EmployeePerformance implements Serializable {
    private Long id;
    private Long employeeId;
    private String month;  // "2025-11"
    private BigDecimal performanceScore;
    private String rating;
    private String notes;
    private Timestamp createdAt;
    
    // Constructors, getters, setters
}
```

### File 3: `ejb-module/src/dao/EmployeePerformanceDAO.java` (MỚI)
```java
package dao;
import model.EmployeePerformance;
// ... imports

public class EmployeePerformanceDAO {
    public Long create(EmployeePerformance perf) {
        // INSERT INTO employee_performance ...
    }
    
    public EmployeePerformance findByEmployeeIdAndMonth(Long employeeId, String month) {
        // SELECT ... WHERE employee_id=? AND month=?
    }
    
    public List<EmployeePerformance> findByEmployeeId(Long employeeId) {
        // SELECT ... WHERE employee_id=? ORDER BY month DESC
    }
    
    public void update(EmployeePerformance perf) {
        // UPDATE employee_performance ...
    }
}
```

### File 4: `ejb-module/src/ejb/EmployeeService.java`
**Thêm methods:**
```java
void updateEmployeePerformance(Long employeeId, String month, 
    BigDecimal score, String rating, String notes) throws Exception;
    
EmployeePerformance getEmployeePerformance(Long employeeId, String month) throws Exception;

List<EmployeePerformance> getEmployeePerformanceHistory(Long employeeId) throws Exception;
```

### File 5: `web-module/src/controller/EmployeeServlet.java`
**Thêm vào doGet():**
```java
else if ("performance".equals(action)) {
    showPerformanceForm(request, response, service);
} else if ("performanceHistory".equals(action)) {
    viewPerformanceHistory(request, response, service);
}
```

**Thêm vào doPost():**
```java
else if ("updatePerformance".equals(action)) {
    updatePerformance(request, response, service);
}
```

**Thêm methods mới:**
```java
private void showPerformanceForm(...) { }
private void viewPerformanceHistory(...) { }
private void updatePerformance(...) { }
```

### File 6: `web-module/employee-list.jsp`
**Thêm cột:**
```jsp
<th>Last Performance</th>
...
<td>${employee.lastPerformanceRating != null ? employee.lastPerformanceRating : 'N/A'}</td>
```

**Thêm button:**
```jsp
<a href="employee?action=performance&id=${employee.id}">Performance</a>
```

### File 7: `web-module/employee-performance-form.jsp` (MỚI)
```jsp
<form method="post" action="employee">
    <input type="hidden" name="action" value="updatePerformance" />
    <input type="hidden" name="employeeId" value="${employee.id}" />
    
    <label>Month:</label>
    <select name="month">...</select>
    
    <label>Performance Score (0-100):</label>
    <input type="number" name="performanceScore" min="0" max="100" />
    
    <label>Rating:</label>
    <select name="rating">
        <option>Excellent</option>
        <option>Good</option>
        <option>Average</option>
        <option>Poor</option>
    </select>
    
    <label>Notes:</label>
    <textarea name="notes"></textarea>
    
    <button type="submit">Save Performance</button>
</form>
```

---

## 🎯 THỨ TỰ THỰC HIỆN

1. **Database** → Tạo bảng/cột performance
2. **Model** → Tạo EmployeePerformance.java
3. **DAO** → Tạo EmployeePerformanceDAO.java
4. **Service** → Thêm methods vào EmployeeService/Bean
5. **Servlet** → Thêm handlers
6. **JSP** → Tạo form và cập nhật list/detail
7. **Test** → Thêm test cases

---

## ✅ KẾT LUẬN

**Tổng cộng:**
- **10-12 files cần SỬA**
- **3-4 files cần TẠO MỚI**
- **Tổng: ~15 files**

Bạn muốn tôi bắt đầu implement không? Tôi sẽ làm từng bước một cách có hệ thống.

