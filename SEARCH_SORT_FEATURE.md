# Search and Sort Feature Implementation

## ✅ Đã Thêm Tính Năng Search và Sort

### 1. **Search Feature**

**Chức năng:**
- Tìm kiếm employees theo name, email, hoặc department
- Case-insensitive search
- Real-time search với button "Search"
- Button "Clear" để xóa search và hiển thị tất cả

**Implementation:**
- `EmployeeDAO.search(String keyword)` - Search ở database level
- `EmployeeService.searchEmployees(String keyword)` - Business logic
- `EmployeeBean.searchEmployees()` - JSF action method
- `EmployeeBean.clearSearch()` - Clear search method

**UI:**
- Search box với placeholder "Search by name, email, or department..."
- "Search" button để thực hiện search
- "Clear" button (chỉ hiển thị khi có search keyword)

### 2. **Sort Feature**

**Chức năng:**
- Sort theo các cột: ID, Name, Email, Department
- Click vào header để sort
- Toggle ASC/DESC khi click cùng cột
- Hiển thị arrow (↑/↓) để chỉ sort direction

**Implementation:**
- `EmployeeDAO.findAllSorted(String sortBy, String sortOrder)` - Sort ở database level
- `EmployeeService.getAllEmployeesSorted(String sortBy, String sortOrder)` - Business logic
- `EmployeeBean.sortEmployees()` - JSF action method với toggle logic

**UI:**
- Clickable column headers (ID, Name, Email, Department)
- Arrow indicators (↑ for ASC, ↓ for DESC)
- Visual feedback cho current sort column

### 3. **Files Đã Sửa**

#### Backend:
1. ✅ `ejb-module/src/ejb/EmployeeService.java`
   - Thêm `searchEmployees(String keyword)`
   - Thêm `getAllEmployeesSorted(String sortBy, String sortOrder)`

2. ✅ `ejb-module/src/ejb/EmployeeServiceBean.java`
   - Implement search và sort methods

3. ✅ `ejb-module/src/dao/EmployeeDAO.java`
   - Thêm `search(String keyword)` - SQL LIKE query
   - Thêm `findAllSorted(String sortBy, String sortOrder)` - SQL ORDER BY với validation

#### Frontend:
4. ✅ `web-module/src/controller/EmployeeBean.java`
   - Thêm properties: `searchKeyword`, `sortBy`, `sortOrder`
   - Thêm methods: `searchEmployees()`, `clearSearch()`, `sortEmployees()`
   - Cập nhật `loadEmployees()` để hỗ trợ search và sort
   - Thêm getters/setters cho search và sort properties

5. ✅ `web-module/employee-list.xhtml`
   - Thêm search form với input box và buttons
   - Thêm clickable sort headers cho các cột
   - Thêm arrow indicators cho sort direction
   - Thêm CSS styles cho search form

### 4. **SQL Queries**

**Search Query:**
```sql
SELECT id, name, email, department FROM employees 
WHERE LOWER(name) LIKE ? OR LOWER(email) LIKE ? OR LOWER(department) LIKE ? 
ORDER BY id
```

**Sort Query:**
```sql
SELECT id, name, email, department FROM employees 
ORDER BY {sortBy} {sortOrder}
```

**Security:**
- SQL injection protection: Validate `sortBy` và `sortOrder` trước khi dùng trong query
- Chỉ cho phép sort theo các cột hợp lệ: id, name, email, department
- Chỉ cho phép sortOrder: ASC hoặc DESC

### 5. **User Experience**

**Search:**
1. User nhập keyword vào search box
2. Click "Search" button
3. Kết quả được filter theo keyword
4. Click "Clear" để xóa search và hiển thị tất cả

**Sort:**
1. User click vào column header (ID, Name, Email, Department)
2. Table được sort theo cột đó (ASC)
3. Click lại cùng cột → toggle sang DESC
4. Click cột khác → sort theo cột mới (ASC)
5. Arrow indicator hiển thị sort direction

### 6. **Features**

✅ **Search:**
- Case-insensitive
- Search trong name, email, và department
- Real-time với button click
- Clear search functionality

✅ **Sort:**
- Sort theo ID, Name, Email, Department
- ASC/DESC toggle
- Visual indicators (arrows)
- Persistent sort state

✅ **Combined:**
- Search và Sort có thể dùng cùng lúc
- Search results được sort theo current sort settings

---

## ✅ Kết Quả

- ✅ Build thành công
- ✅ WAR file đã được deploy
- ✅ Search feature hoạt động
- ✅ Sort feature hoạt động
- ✅ UI/UX improvements

**Test:**
- ✅ Search by name
- ✅ Search by email
- ✅ Search by department
- ✅ Sort by ID (ASC/DESC)
- ✅ Sort by Name (ASC/DESC)
- ✅ Sort by Email (ASC/DESC)
- ✅ Sort by Department (ASC/DESC)
- ✅ Clear search
- ✅ Combined search + sort

---

**Status: ✅ HOÀN TẤT**

Search và Sort features đã được implement và deploy thành công!



