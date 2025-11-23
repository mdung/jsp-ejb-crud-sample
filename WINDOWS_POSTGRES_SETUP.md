# Windows Setup Guide - PostgreSQL + WildFly 38.0.1

## Your Current Setup
- ✅ Java 8 installed
- ✅ WildFly 38.0.1.Final at `C:\wildfly-38.0.1.Final`
- ✅ PostgreSQL with pgAdmin 4

---

## Step 1: Create Database in PostgreSQL (using pgAdmin)

### 1.1 Open pgAdmin 4
- Launch pgAdmin 4 from Start Menu

### 1.2 Connect to PostgreSQL Server
1. In Object Explorer, expand **"Servers (1)"**
2. Expand **"PostgreSQL 18"** (or your version)
3. Right-click on **"PostgreSQL 18"** → **"Connect Server"**
4. Enter your PostgreSQL password if prompted

### 1.3 Create Database
1. Right-click on **"Databases"** → **"Create"** → **"Database..."**
2. In the **"General"** tab:
   - **Database name:** `employee_db`
   - **Owner:** Leave as default (usually `postgres`)
3. Click **"Save"**

### 1.4 Run Schema Script
1. In Object Explorer, expand **"Databases"**
2. Expand **"employee_db"**
3. Right-click on **"employee_db"** → **"Query Tool"**
4. In the Query Tool, click **"Open File"** icon (folder icon)
5. Navigate to: `D:\project\jsp-ejb-crud-sample\database\schema-postgresql.sql`
   (Use `schema-postgresql.sql` for PostgreSQL, or use `schema.sql` and uncomment PostgreSQL section)
6. Select the file and click **"Open"**
7. Click **"Execute"** (or press F5)
8. You should see: **"Query returned successfully"**

### 1.5 Verify Table Created
1. In Object Explorer, expand **"employee_db"** → **"Schemas"** → **"public"** → **"Tables"**
2. You should see **"employees"** table
3. Right-click **"employees"** → **"View/Edit Data"** → **"All Rows"**
4. You should see 3 sample employees

---

## Step 2: Install PostgreSQL JDBC Driver Module in WildFly

### 2.1 Download PostgreSQL JDBC Driver
1. Go to: https://jdbc.postgresql.org/download/
2. Download the latest version (e.g., `postgresql-42.7.1.jar`)
3. Save to a temporary location (e.g., `C:\temp\postgresql-42.7.1.jar`)

### 2.2 Create Module Directory
Open Command Prompt and run:
```cmd
mkdir C:\wildfly-38.0.1.Final\modules\org\postgresql\main
```

### 2.3 Copy JDBC Driver
```cmd
copy C:\temp\postgresql-42.7.1.jar C:\wildfly-38.0.1.Final\modules\org\postgresql\main\
```
(Adjust path if your JAR is in a different location)

### 2.4 Create module.xml
1. Open Notepad or any text editor
2. Create new file with this content:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.1" name="org.postgresql">
    <resources>
        <resource-root path="postgresql-42.7.1.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
    </dependencies>
</module>
```
3. Save as: `C:\wildfly-38.0.1.Final\modules\org\postgresql\main\module.xml`
   - **Important:** When saving, change "Save as type" to **"All Files (*.*)"** and name it exactly `module.xml` (not `module.xml.txt`)

---

## Step 3: Configure WildFly DataSource

### 3.1 Open standalone.xml
1. Navigate to: `C:\wildfly-38.0.1.Final\standalone\configuration\`
2. Open `standalone.xml` in a text editor (Notepad++, VS Code, or even Notepad)

### 3.2 Find Datasources Section
1. Press `Ctrl+F` to search
2. Search for: `<datasources>`
3. You'll find it around line 100-200

### 3.3 Add PostgreSQL DataSource
Inside the `<datasources>` section, add this configuration:

**Find this section:**
```xml
<datasources>
    <!-- Existing datasources may be here -->
</datasources>
```

**Add this BEFORE the closing `</datasources>` tag:**
```xml
<datasource jndi-name="java:jboss/datasources/EmployeeDS" pool-name="EmployeeDS">
    <connection-url>jdbc:postgresql://localhost:5432/employee_db</connection-url>
    <driver>postgresql</driver>
    <security>
        <user-name>postgres</user-name>
        <password>YOUR_POSTGRES_PASSWORD</password>
    </security>
    <validation>
        <valid-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker"/>
        <background-validation>true</background-validation>
    </validation>
</datasource>
```

**⚠️ IMPORTANT:** Replace `YOUR_POSTGRES_PASSWORD` with your actual PostgreSQL password!

### 3.4 Add Driver Configuration
Find the `<drivers>` section (usually right after `<datasources>`) and add:

```xml
<drivers>
    <!-- Existing drivers may be here -->
    <driver name="postgresql" module="org.postgresql">
        <driver-class>org.postgresql.Driver</driver-class>
    </driver>
</drivers>
```

### 3.5 Save the File
- Save `standalone.xml`
- Make sure you saved it correctly (not as `.txt`)

---

## Step 4: Build the Application

### 4.1 Set WILDFLY_HOME (if not already set)
Open Command Prompt as Administrator and run:
```cmd
setx WILDFLY_HOME "C:\wildfly-38.0.1.Final" /M
```
Close and reopen Command Prompt for the variable to take effect.

Or set it temporarily for this session:
```cmd
set WILDFLY_HOME=C:\wildfly-38.0.1.Final
```

### 4.2 Navigate to Project Directory
```cmd
cd D:\project\jsp-ejb-crud-sample
```

### 4.3 Run Build Script
```cmd
build-windows.bat
```

**OR** if the build script doesn't work, use manual compilation (see Step 4.4)

### 4.4 Manual Build (Alternative)

If the build script fails, compile manually:

**Compile EJB Module:**
```cmd
javac -source 1.8 -target 1.8 -cp "C:\wildfly-38.0.1.Final\modules\system\layers\base\javax\ejb\api\main\*;C:\wildfly-38.0.1.Final\modules\system\layers\base\javax\servlet\api\main\*" -d build\ejb-classes ejb-module\src\model\*.java ejb-module\src\dao\*.java ejb-module\src\ejb\*.java
```

**Compile Web Module:**
```cmd
javac -source 1.8 -target 1.8 -cp "C:\wildfly-38.0.1.Final\modules\system\layers\base\javax\servlet\api\main\*;build\ejb-classes" -d build\web-classes web-module\src\controller\*.java
```

**Create WAR Structure:**
```cmd
mkdir build\war\WEB-INF\classes
xcopy /E /Y build\web-classes\* build\war\WEB-INF\classes\
xcopy /E /Y build\ejb-classes\* build\war\WEB-INF\classes\
copy web-module\WEB-INF\web.xml build\war\WEB-INF\
copy web-module\*.jsp build\war\
```

**Package WAR:**
```cmd
cd build\war
jar cvf ..\..\dist\employee-demo.war *
cd ..\..
```

---

## Step 5: Start WildFly

### 5.1 Open Command Prompt
Open as Administrator (recommended)

### 5.2 Navigate to WildFly bin directory
```cmd
cd C:\wildfly-38.0.1.Final\bin
```

### 5.3 Start WildFly
```cmd
standalone.bat
```

### 5.4 Wait for Startup
Look for this message:
```
WFLYSRV0025: WildFly Full 38.0.1.Final (WildFly Core 30.0.1.Final) started in XXXXms
```

**⚠️ Keep this window open!** Don't close it - WildFly is running.

### 5.5 Check for Errors
- If you see errors about DataSource, check your `standalone.xml` configuration
- If you see errors about module not found, check the PostgreSQL driver module installation

---

## Step 6: Deploy the Application

### 6.1 Copy WAR to Deployments
While WildFly is running, copy the WAR file:

```cmd
copy D:\project\jsp-ejb-crud-sample\dist\employee-demo.war C:\wildfly-38.0.1.Final\standalone\deployments\
```

### 6.2 Verify Deployment
1. Check the WildFly console window
2. You should see: `"employee-demo.war" deployed successfully`
3. If there are errors, they will be shown in the console

### 6.3 Check Deployment Folder
- Go to: `C:\wildfly-38.0.1.Final\standalone\deployments\`
- You should see `employee-demo.war` and `employee-demo.war.deployed` file

---

## Step 7: Test the Application

### 7.1 Open Browser
Open your web browser (Chrome, Firefox, Edge)

### 7.2 Access Application
Navigate to:
```
http://localhost:8080/employee-demo/employee?action=list
```

### 7.3 Expected Result
You should see:
- **Employee Management System** page
- A table with 3 sample employees (from the database)
- Columns: ID, Name, Email, Department
- Buttons: Add New Employee, View, Edit, Delete

### 7.4 Test CRUD Operations

**Create Employee:**
1. Click **"Add New Employee"**
2. Fill in:
   - Name: `Test User`
   - Email: `test@example.com`
   - Department: `IT`
3. Click **"Create Employee"**
4. Should see success message and new employee in list

**View Employee:**
1. Click **"View"** on any employee
2. Should see employee details page

**Edit Employee:**
1. Click **"Edit"** on an employee
2. Change the name
3. Click **"Update Employee"**
4. Should see updated information

**Delete Employee:**
1. Click **"Delete"** on an employee
2. Confirm deletion
3. Employee should be removed from list

---

## Troubleshooting

### Problem: WildFly won't start
**Solution:**
- Check if port 8080 is in use:
  ```cmd
  netstat -ano | findstr :8080
  ```
- If port is busy, stop the application using it or change WildFly port

### Problem: DataSource error in WildFly console
**Solution:**
- Check PostgreSQL password in `standalone.xml`
- Verify PostgreSQL service is running:
  ```cmd
  sc query postgresql-x64-18
  ```
- Test connection manually:
  ```cmd
  psql -U postgres -d employee_db
  ```

### Problem: Module not found error
**Solution:**
- Verify `module.xml` exists at: `C:\wildfly-38.0.1.Final\modules\org\postgresql\main\module.xml`
- Verify JAR file name in `module.xml` matches actual file name
- Check file paths are correct

### Problem: Application not accessible (404 error)
**Solution:**
- Check WildFly logs: `C:\wildfly-38.0.1.Final\standalone\log\server.log`
- Verify deployment: Check `standalone\deployments\` folder
- Try: `http://localhost:8080/employee-demo/` (without action parameter)

### Problem: Database connection error
**Solution:**
- Verify database exists in pgAdmin
- Check connection URL in `standalone.xml`: `jdbc:postgresql://localhost:5432/employee_db`
- Verify PostgreSQL is listening on port 5432:
  ```cmd
  netstat -ano | findstr :5432
  ```

### Problem: Compilation errors
**Solution:**
- Verify Java 8 is in PATH: `java -version`
- Check classpath includes WildFly modules
- Ensure all source files are in correct directories

---

## Quick Verification Checklist

Before testing, verify:

- [ ] PostgreSQL service is running
- [ ] Database `employee_db` exists in pgAdmin
- [ ] Table `employees` exists with sample data
- [ ] PostgreSQL JDBC driver module installed
- [ ] DataSource configured in `standalone.xml` with correct password
- [ ] WildFly started successfully (no errors in console)
- [ ] WAR file deployed (check deployments folder)
- [ ] Browser can access: `http://localhost:8080/employee-demo/employee?action=list`

---

## Next Steps

Once everything works:

1. **Test all CRUD operations** (Create, Read, Update, Delete)
2. **Test validation** (duplicate email, missing fields)
3. **Apply phoneNumber update** (see `updated-version/` folder)
4. **Run Selenium tests** (if you set up Selenium)

---

## Need Help?

If you encounter issues:
1. Check WildFly console for error messages
2. Check WildFly logs: `C:\wildfly-38.0.1.Final\standalone\log\server.log`
3. Verify each step was completed correctly
4. Test database connection separately using pgAdmin Query Tool

Good luck! 🚀

