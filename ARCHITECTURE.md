# Architecture and Code Flow Documentation

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        Client Browser                         │
└──────────────────────────┬──────────────────────────────────┘
                           │ HTTP Request
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    Web Module (WAR)                          │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  JSP Pages (View Layer)                              │   │
│  │  - employee-list.jsp                                 │   │
│  │  - employee-form.jsp                                 │   │
│  │  - employee-detail.jsp                               │   │
│  └──────────────────┬───────────────────────────────────┘   │
│                     │                                        │
│  ┌──────────────────▼───────────────────────────────────┐   │
│  │  EmployeeServlet (Controller)                        │   │
│  │  - doGet() / doPost()                                │   │
│  │  - Handles routing and request processing           │   │
│  └──────────────────┬───────────────────────────────────┘   │
└─────────────────────┼──────────────────────────────────────┘
                      │ JNDI Lookup
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                  EJB Module (JAR)                          │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  EmployeeService (Remote Interface)                  │   │
│  └──────────────────┬───────────────────────────────────┘   │
│                     │                                        │
│  ┌──────────────────▼───────────────────────────────────┐   │
│  │  EmployeeServiceBean (Stateless Session Bean)        │   │
│  │  - Business logic and validation                     │   │
│  │  - Transaction management                            │   │
│  └──────────────────┬───────────────────────────────────┘   │
│                     │                                        │
│  ┌──────────────────▼───────────────────────────────────┐   │
│  │  EmployeeDAO (Data Access Object)                    │   │
│  │  - JDBC operations                                   │   │
│  │  - SQL query execution                              │   │
│  └──────────────────┬───────────────────────────────────┘   │
└─────────────────────┼──────────────────────────────────────┘
                      │ JDBC
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                    Database (MySQL/PostgreSQL)              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  employees table                                     │   │
│  │  - id, name, email, department                       │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## Detailed Code Flow

### Flow 1: Create Employee (POST Request)

```
1. User fills form in employee-form.jsp
   └─> Submits POST to /employee?action=create

2. EmployeeServlet.doPost() receives request
   ├─> Extracts parameters: name, email, department
   ├─> Creates Employee object
   └─> Calls: service.createEmployee(employee)

3. EmployeeServiceBean.createEmployee()
   ├─> Validates employee data
   ├─> Checks email uniqueness via DAO
   └─> Calls: employeeDAO.create(employee)

4. EmployeeDAO.create()
   ├─> Gets DataSource via JNDI: java:jboss/datasources/EmployeeDS
   ├─> Gets Connection from DataSource
   ├─> Prepares INSERT statement
   ├─> Executes: INSERT INTO employees (name, email, department) VALUES (?, ?, ?)
   ├─> Retrieves generated ID
   └─> Returns employee ID

5. Flow returns to Servlet
   ├─> Redirects to list page with success message
   └─> User sees updated employee list
```

### Flow 2: List Employees (GET Request)

```
1. User navigates to /employee?action=list

2. EmployeeServlet.doGet()
   └─> Calls: service.getAllEmployees()

3. EmployeeServiceBean.getAllEmployees()
   └─> Calls: employeeDAO.findAll()

4. EmployeeDAO.findAll()
   ├─> Gets Connection from DataSource
   ├─> Executes: SELECT id, name, email, department FROM employees ORDER BY id
   ├─> Maps ResultSet to Employee objects
   └─> Returns List<Employee>

5. Servlet sets employees in request attribute
   └─> Forwards to employee-list.jsp

6. employee-list.jsp
   ├─> Uses JSTL <c:forEach> to iterate employees
   ├─> Displays table with employee data
   └─> Renders HTML response
```

### Flow 3: Update Employee (POST Request)

```
1. User edits employee in employee-form.jsp
   └─> Submits POST to /employee?action=update&id=X

2. EmployeeServlet.doPost()
   ├─> Extracts id, name, email, department
   ├─> Creates Employee object with ID
   └─> Calls: service.updateEmployee(employee)

3. EmployeeServiceBean.updateEmployee()
   ├─> Validates employee data
   ├─> Checks email uniqueness (excluding current employee)
   └─> Calls: employeeDAO.update(employee)

4. EmployeeDAO.update()
   ├─> Gets Connection from DataSource
   ├─> Executes: UPDATE employees SET name=?, email=?, department=? WHERE id=?
   └─> Returns void

5. Servlet redirects to list with success message
```

### Flow 4: Delete Employee (GET Request)

```
1. User clicks Delete button
   └─> GET /employee?action=delete&id=X

2. EmployeeServlet.doGet()
   └─> Calls: service.deleteEmployee(id)

3. EmployeeServiceBean.deleteEmployee()
   ├─> Verifies employee exists
   └─> Calls: employeeDAO.delete(id)

4. EmployeeDAO.delete()
   ├─> Gets Connection from DataSource
   ├─> Executes: DELETE FROM employees WHERE id=?
   └─> Returns void

5. Servlet redirects to list with success message
```

---

## Component Details

### 1. JSP Layer (View)

**Purpose:** Presentation layer, renders HTML

**Files:**
- `employee-list.jsp` - Displays all employees in a table
- `employee-form.jsp` - Create/edit form
- `employee-detail.jsp` - View single employee details

**Technologies:**
- JSP 2.1 (Java 6 compatible)
- JSTL 1.2 (Core tag library)
- No scriptlets (pure JSTL)

**Key Features:**
- Uses `<c:forEach>` for iteration
- Uses `<c:if>` for conditional rendering
- Uses `${}` EL expressions for data binding

### 2. Servlet Layer (Controller)

**Purpose:** Request routing and coordination

**File:** `EmployeeServlet.java`

**Responsibilities:**
- Receives HTTP requests (GET/POST)
- Extracts request parameters
- Calls EJB service methods
- Sets request attributes for JSP
- Forwards to appropriate JSP or redirects

**Key Methods:**
- `doGet()` - Handles GET requests (list, view, edit, delete)
- `doPost()` - Handles POST requests (create, update)
- `getEmployeeService()` - JNDI lookup for EJB

**JNDI Lookup:**
```java
InitialContext ctx = new InitialContext();
EmployeeService service = (EmployeeService) ctx.lookup(
    "java:global/employee-demo/ejb-module/EmployeeServiceBean!ejb.EmployeeService"
);
```

### 3. EJB Layer (Business Logic)

**Purpose:** Business logic, validation, transaction management

**Files:**
- `EmployeeService.java` - Remote interface
- `EmployeeServiceBean.java` - Stateless session bean implementation

**Responsibilities:**
- Business rule validation
- Email uniqueness checking
- Transaction management (container-managed)
- Exception handling

**Key Features:**
- `@Stateless` - Stateless session bean
- `@Remote` - Remote interface for cross-module access
- Container-managed transactions
- Automatic transaction boundaries

**Validation Rules:**
- Name: Required, not empty
- Email: Required, must contain "@", unique
- Department: Required, not empty

### 4. DAO Layer (Data Access)

**Purpose:** Database operations using JDBC

**File:** `EmployeeDAO.java`

**Responsibilities:**
- Database connection management
- SQL query execution
- ResultSet mapping to Employee objects
- Resource cleanup (Connection, Statement, ResultSet)

**Key Methods:**
- `create()` - INSERT with generated keys
- `findById()` - SELECT by ID
- `findAll()` - SELECT all
- `update()` - UPDATE by ID
- `delete()` - DELETE by ID
- `emailExists()` - Check email uniqueness

**JDBC Pattern:**
```java
Connection conn = getDataSource().getConnection();
PreparedStatement pstmt = conn.prepareStatement(sql);
// Set parameters
pstmt.executeUpdate();
// Close resources
```

**DataSource Lookup:**
```java
InitialContext ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("java:jboss/datasources/EmployeeDS");
```

### 5. Model Layer

**Purpose:** Data transfer object

**File:** `Employee.java`

**Properties:**
- `id` (Long) - Primary key
- `name` (String) - Employee name
- `email` (String) - Email address
- `department` (String) - Department name

**Features:**
- Implements `Serializable` (for EJB remote calls)
- Standard getters/setters
- Default constructor (required for JSP EL)

---

## JDBC Inside EJB

### Why JDBC in EJB?

- Legacy system requirement (no JPA/Hibernate)
- Direct control over SQL queries
- Compatible with Java 6

### Connection Management

1. **DataSource via JNDI:**
   - Configured in WildFly `standalone.xml`
   - JNDI name: `java:jboss/datasources/EmployeeDS`
   - Connection pooling handled by WildFly

2. **Connection Lifecycle:**
   ```
   Get Connection → Execute Query → Close Connection
   ```
   - Connection obtained from pool
   - Automatically returned to pool on close()
   - No manual connection management needed

3. **Transaction Management:**
   - EJB container manages transactions
   - `@Stateless` beans have container-managed transactions
   - All DAO operations participate in same transaction
   - Automatic rollback on exceptions

### SQL Query Examples

**INSERT with Generated Keys:**
```java
PreparedStatement pstmt = conn.prepareStatement(
    "INSERT INTO employees (name, email, department) VALUES (?, ?, ?)",
    Statement.RETURN_GENERATED_KEYS
);
pstmt.setString(1, employee.getName());
pstmt.setString(2, employee.getEmail());
pstmt.setString(3, employee.getDepartment());
pstmt.executeUpdate();
ResultSet rs = pstmt.getGeneratedKeys();
if (rs.next()) {
    Long id = rs.getLong(1);
}
```

**SELECT with ResultSet Mapping:**
```java
PreparedStatement pstmt = conn.prepareStatement(
    "SELECT id, name, email, department FROM employees WHERE id = ?"
);
pstmt.setLong(1, id);
ResultSet rs = pstmt.executeQuery();
if (rs.next()) {
    Employee emp = new Employee();
    emp.setId(rs.getLong("id"));
    emp.setName(rs.getString("name"));
    emp.setEmail(rs.getString("email"));
    emp.setDepartment(rs.getString("department"));
}
```

---

## Deployment on WildFly

### Module Structure

```
employee-demo.ear
├── META-INF/
│   └── application.xml
├── ejb-module.jar
│   ├── META-INF/
│   │   └── ejb-jar.xml
│   └── (compiled classes)
└── web-module.war
    ├── WEB-INF/
    │   ├── web.xml
    │   └── classes/
    │       └── controller/
    └── *.jsp
```

### JNDI Naming

**EJB JNDI Name:**
```
java:global/employee-demo/ejb-module/EmployeeServiceBean!ejb.EmployeeService
```

**DataSource JNDI Name:**
```
java:jboss/datasources/EmployeeDS
```

### Context Root

Web application accessible at:
```
http://localhost:8080/employee-demo/
```

---

## Security Considerations

1. **SQL Injection Prevention:**
   - All queries use `PreparedStatement` with parameters
   - No string concatenation in SQL

2. **Input Validation:**
   - Client-side (HTML5 validation)
   - Server-side (EJB validation)

3. **Error Handling:**
   - Exceptions caught and converted to user-friendly messages
   - No stack traces exposed to users

---

## Performance Considerations

1. **Connection Pooling:**
   - WildFly manages connection pool
   - Connections reused efficiently

2. **Stateless EJBs:**
   - No state maintained between calls
   - Can be pooled and reused

3. **JDBC Best Practices:**
   - PreparedStatements for efficiency
   - Proper resource cleanup (try-finally)
   - Connection obtained per operation (stateless)

---

## Extension Points

To add new functionality:

1. **New Field:**
   - Update `Employee.java` model
   - Update `EmployeeDAO.java` SQL queries
   - Update JSP forms and displays
   - Update validation in `EmployeeServiceBean`

2. **New Operation:**
   - Add method to `EmployeeService` interface
   - Implement in `EmployeeServiceBean`
   - Add DAO method if needed
   - Add Servlet handler
   - Add JSP page if needed

3. **New Validation:**
   - Add validation logic in `EmployeeServiceBean.validateEmployee()`
   - Add DAO method for uniqueness checks if needed

