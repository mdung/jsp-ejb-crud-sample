# Phân Tích: Chuyển Từ JSP Sang JSF

## 📊 TỔNG QUAN

**Hiện tại:** JSP (JavaServer Pages) + Servlet  
**Muốn chuyển sang:** JSF (JavaServer Faces)

---

## 🔍 KHÁC BIỆT JSP vs JSF

### JSP (Hiện tại):
- **View Technology:** JSP pages với JSTL
- **Controller:** HttpServlet (EmployeeServlet)
- **Request Handling:** doGet()/doPost() methods
- **Navigation:** RequestDispatcher.forward()
- **Data Binding:** Request attributes + JSTL tags

### JSF (Mục tiêu):
- **View Technology:** Facelets (XHTML files)
- **Controller:** Managed Beans (@ManagedBean hoặc CDI @Named)
- **Request Handling:** JSF Lifecycle tự động
- **Navigation:** Navigation rules (faces-config.xml) hoặc implicit navigation
- **Data Binding:** EL expressions với managed beans

---

## 📋 FILES CẦN SỬA/TẠO

### 1. WEB CONFIGURATION (2-3 files)

#### 1.1. `web-module/WEB-INF/web.xml` ⚠️ **SỬA LỚN**
**Cần thay đổi:**
- Thay đổi servlet mapping từ Servlet → FacesServlet
- Thêm JSF configuration
- Thêm Facelets configuration
- Xóa servlet mapping cũ

**Trước (JSP):**
```xml
<servlet>
    <servlet-name>EmployeeServlet</servlet-name>
    <servlet-class>controller.EmployeeServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>EmployeeServlet</servlet-name>
    <url-pattern>/employee</url-pattern>
</servlet-mapping>
```

**Sau (JSF):**
```xml
<servlet>
    <servlet-name>Faces Servlet</servlet-name>
    <servlet-class>jakarta.faces.webapp.FacesServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>Faces Servlet</servlet-name>
    <url-pattern>*.xhtml</url-pattern>
</servlet-mapping>
```

#### 1.2. `web-module/WEB-INF/faces-config.xml` 🆕 **TẠO MỚI**
**Cần tạo:**
- JSF configuration file
- Navigation rules (nếu dùng explicit navigation)
- Managed bean declarations (nếu không dùng annotations)

**Nội dung:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<faces-config xmlns="https://jakarta.ee/xml/ns/jakartaee"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee
              https://jakarta.ee/xml/ns/jakartaee/web-facesconfig_3_0.xsd"
              version="3.0">
    
    <application>
        <resource-bundle>
            <base-name>messages</base-name>
            <var>msg</var>
        </resource-bundle>
    </application>
    
    <!-- Navigation rules (optional) -->
    <navigation-rule>
        <from-view-id>/employee-list.xhtml</from-view-id>
        <navigation-case>
            <from-outcome>view</from-outcome>
            <to-view-id>/employee-detail.xhtml</to-view-id>
        </navigation-case>
    </navigation-rule>
</faces-config>
```

---

### 2. JSP → FACELETS (XHTML) - 4-5 files

#### 2.1. `web-module/employee-list.jsp` → `web-module/employee-list.xhtml` ⚠️ **SỬA LỚN**
**Thay đổi:**
- Đổi extension từ `.jsp` → `.xhtml`
- Thay JSTL tags → JSF tags
- Thay `${}` EL → JSF EL (giữ nguyên nhưng context khác)
- Thay `<c:forEach>` → `<ui:repeat>` hoặc `<h:dataTable>`
- Thay form submit → JSF command buttons

**Trước (JSP):**
```jsp
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:forEach var="employee" items="${employees}">
    <tr>
        <td>${employee.name}</td>
        <td><a href="employee?action=view&id=${employee.id}">View</a></td>
    </tr>
</c:forEach>
```

**Sau (JSF):**
```xhtml
<ui:repeat value="#{employeeBean.employees}" var="employee">
    <tr>
        <td>#{employee.name}</td>
        <td><h:commandLink value="View" action="#{employeeBean.viewEmployee(employee.id)}" /></td>
    </tr>
</ui:repeat>
```

#### 2.2. `web-module/employee-form.jsp` → `web-module/employee-form.xhtml` ⚠️ **SỬA LỚN**
**Thay đổi:**
- Form HTML → `<h:form>`
- Input HTML → `<h:inputText>`, `<h:inputTextarea>`
- Submit button → `<h:commandButton>`
- Action URL → JSF action method

**Trước (JSP):**
```jsp
<form method="post" action="employee">
    <input type="hidden" name="action" value="create" />
    <input type="text" name="name" />
    <button type="submit">Create</button>
</form>
```

**Sau (JSF):**
```xhtml
<h:form>
    <h:inputHidden value="#{employeeBean.action}" />
    <h:inputText value="#{employeeBean.employee.name}" />
    <h:commandButton value="Create" action="#{employeeBean.createEmployee}" />
</h:form>
```

#### 2.3. `web-module/employee-detail.jsp` → `web-module/employee-detail.xhtml` ⚠️ **SỬA LỚN**
**Tương tự như trên**

#### 2.4. `web-module/employee-performance-form.jsp` → `web-module/employee-performance-form.xhtml` ⚠️ **SỬA LỚN**
**Tương tự như trên**

#### 2.5. `web-module/employee-performance-history.jsp` → `web-module/employee-performance-history.xhtml` ⚠️ **SỬA LỚN**
**Tương tự như trên**

#### 2.6. `web-module/index.jsp` → `web-module/index.xhtml` hoặc redirect ⚠️ **SỬA**
**Có thể:**
- Tạo `index.xhtml` với redirect
- Hoặc giữ `index.jsp` redirect đến JSF page

---

### 3. SERVLET → MANAGED BEAN (1 file lớn)

#### 3.1. `web-module/src/controller/EmployeeServlet.java` → `web-module/src/controller/EmployeeBean.java` ⚠️ **SỬA LỚN**
**Thay đổi hoàn toàn:**

**Trước (Servlet):**
```java
public class EmployeeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String action = request.getParameter("action");
        // ...
    }
}
```

**Sau (Managed Bean):**
```java
@Named
@RequestScoped  // hoặc @ViewScoped, @SessionScoped
public class EmployeeBean {
    @Inject
    private EmployeeService employeeService;
    
    private List<Employee> employees;
    private Employee employee;
    
    @PostConstruct
    public void init() {
        loadEmployees();
    }
    
    public String createEmployee() {
        // Business logic
        return "employee-list?faces-redirect=true";
    }
    
    public void loadEmployees() {
        // Load data
    }
}
```

**Cần thay đổi:**
- Xóa extends HttpServlet
- Xóa doGet()/doPost()
- Thêm @Named hoặc @ManagedBean
- Thêm @Inject cho EJB service
- Thay RequestDispatcher → return String (navigation)
- Thay request.getParameter() → properties trong bean
- Thay request.setAttribute() → bean properties

---

### 4. DEPENDENCIES & LIBRARIES

#### 4.1. `build-windows.bat` hoặc `pom.xml` (nếu có) ⚠️ **SỬA**
**Cần thêm:**
- JSF API JAR
- JSF Implementation JAR (Mojarra hoặc MyFaces)
- Facelets JAR (thường đi kèm với JSF)

**WildFly 38 đã có JSF built-in:**
- `jakarta.faces.api` module
- `com.sun.jsf-impl` module (Mojarra)

**Cần update classpath trong build script:**
```batch
set JSF_API=%WILDFLY_HOME%\modules\jakarta\faces\api\jakarta.faces-api-4.0.jar
set JSF_IMPL=%WILDFLY_HOME%\modules\com\sun\jsf-impl\jsf-impl-4.0.jar
```

---

### 5. EJB SERVICE LAYER

#### 5.1. `ejb-module/src/ejb/EmployeeService.java` & `EmployeeServiceBean.java` ✅ **KHÔNG CẦN SỬA**
**Lý do:** EJB layer không thay đổi, chỉ cách gọi từ web layer thay đổi

**Trước (Servlet):**
```java
EmployeeService service = getEmployeeService(); // JNDI lookup
service.createEmployee(employee);
```

**Sau (Managed Bean):**
```java
@Inject
private EmployeeService employeeService; // CDI injection
employeeService.createEmployee(employee);
```

---

### 6. MODEL & DAO LAYER

#### 6.1. Model classes ✅ **KHÔNG CẦN SỬA**
- `Employee.java`
- `EmployeePerformance.java`

#### 6.2. DAO classes ✅ **KHÔNG CẦN SỬA**
- `EmployeeDAO.java`
- `EmployeePerformanceDAO.java`

---

### 7. TEST FILES

#### 7.1. `tests/selenium/` ⚠️ **SỬA**
**Cần update:**
- XPath selectors (có thể thay đổi do JSF render HTML khác)
- Form submission methods
- Navigation flow

**Ví dụ:**
- JSP: `<a href="employee?action=view&id=1">`
- JSF: `<h:commandLink action="#{bean.view(1)}">` → render thành form với hidden fields

---

## 📊 TỔNG KẾT

### Files Cần SỬA (8-10 files):
1. ✅ `web-module/WEB-INF/web.xml` - **SỬA LỚN**
2. ✅ `web-module/src/controller/EmployeeServlet.java` → `EmployeeBean.java` - **SỬA LỚN (chuyển đổi hoàn toàn)**
3. ✅ `web-module/employee-list.jsp` → `employee-list.xhtml` - **SỬA LỚN**
4. ✅ `web-module/employee-form.jsp` → `employee-form.xhtml` - **SỬA LỚN**
5. ✅ `web-module/employee-detail.jsp` → `employee-detail.xhtml` - **SỬA LỚN**
6. ✅ `web-module/employee-performance-form.jsp` → `employee-performance-form.xhtml` - **SỬA LỚN**
7. ✅ `web-module/employee-performance-history.jsp` → `employee-performance-history.xhtml` - **SỬA LỚN**
8. ✅ `web-module/index.jsp` - **SỬA NHỎ**
9. ✅ `build-windows.bat` - **SỬA (thêm JSF dependencies)**
10. ✅ `tests/selenium/*.java` - **SỬA (update selectors)**

### Files Cần TẠO MỚI (1-2 files):
1. 🆕 `web-module/WEB-INF/faces-config.xml` - **TẠO MỚI**
2. 🆕 `web-module/WEB-INF/beans.xml` (nếu dùng CDI) - **TẠO MỚI (optional)**

### Files KHÔNG CẦN SỬA:
- ✅ `ejb-module/src/model/*.java` - Model classes
- ✅ `ejb-module/src/dao/*.java` - DAO classes
- ✅ `ejb-module/src/ejb/*.java` - EJB Service (chỉ cách inject thay đổi)
- ✅ `database/*.sql` - Database schema
- ✅ `tests/functional/functional-test-cases.md` - Test cases (logic giữ nguyên)

---

## 🔄 MIGRATION STEPS

### Step 1: Setup JSF Configuration
1. Update `web.xml` - thêm FacesServlet
2. Tạo `faces-config.xml`
3. Tạo `beans.xml` (nếu dùng CDI)

### Step 2: Convert Servlet to Managed Bean
1. Tạo `EmployeeBean.java` mới
2. Convert logic từ Servlet → Bean methods
3. Thay JNDI lookup → CDI @Inject
4. Thay navigation logic

### Step 3: Convert JSP to Facelets
1. Convert từng JSP file → XHTML
2. Thay JSTL → JSF tags
3. Update EL expressions
4. Test từng page

### Step 4: Update Build Script
1. Thêm JSF dependencies vào classpath
2. Test build

### Step 5: Update Tests
1. Update Selenium selectors
2. Test navigation flow

---

## ⚠️ LƯU Ý QUAN TRỌNG

### 1. JSF Version
- **WildFly 38** hỗ trợ **JSF 4.0** (Jakarta EE 10)
- Namespace: `jakarta.faces.*`
- Facelets: `jakarta.faces.view.facelets.*`

### 2. CDI vs Managed Bean
- **CDI (@Named)** - Modern, recommended
- **@ManagedBean** - Legacy, nhưng vẫn hoạt động
- **Khuyến nghị:** Dùng CDI @Named với @RequestScoped hoặc @ViewScoped

### 3. Navigation
- **Implicit Navigation:** Return String từ action method
- **Explicit Navigation:** faces-config.xml
- **Redirect:** `return "page?faces-redirect=true"`

### 4. Scope
- **@RequestScoped:** Mỗi request mới
- **@ViewScoped:** Giữ state trong cùng view
- **@SessionScoped:** Giữ state trong session
- **@ApplicationScoped:** Singleton

### 5. Form Submission
- JSF dùng POST-Redirect-GET pattern
- Form state được quản lý bởi JSF
- Validation tích hợp sẵn

---

## 📈 ĐỘ PHỨC TẠP

### Mức độ: **TRUNG BÌNH - CAO**

**Lý do:**
- ✅ EJB layer không đổi (dễ)
- ⚠️ Servlet → Managed Bean (trung bình)
- ⚠️ JSP → Facelets (trung bình - cao, nhiều thay đổi)
- ✅ Database không đổi (dễ)

**Ước tính thời gian:**
- **Người có kinh nghiệm:** 4-6 giờ
- **Người mới:** 8-12 giờ

---

## 🎯 KẾT LUẬN

**Tổng số files cần sửa:** ~10 files  
**Tổng số files cần tạo:** 1-2 files  
**Files không cần sửa:** Model, DAO, EJB Service (chỉ cách inject)

**Độ phức tạp:** Trung bình - Cao

**Khuyến nghị:**
1. ✅ Làm từng bước một
2. ✅ Test sau mỗi bước
3. ✅ Giữ backup code JSP cũ
4. ✅ Đọc JSF documentation

Bạn muốn tôi bắt đầu implement không? Tôi sẽ làm từng bước một cách có hệ thống.



