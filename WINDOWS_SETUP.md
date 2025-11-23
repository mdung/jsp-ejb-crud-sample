# Windows Setup Guide - JSP+EJB CRUD Application

## Overview

This guide will help you set up and test the JSP+EJB CRUD application on Windows. There are two approaches:

1. **Legacy Approach** - Using Java 6 + WildFly 8/9 (as originally designed)
2. **Modern Approach** - Using Java 8/11 + WildFly 10+ (easier, still demonstrates legacy patterns)

**Recommendation:** Use the Modern Approach for easier setup on Windows.

---

## Prerequisites Check

Before starting, verify you have:
- [ ] Windows 10/11 (64-bit recommended)
- [ ] Administrator access (for installing software)
- [ ] At least 4GB RAM free
- [ ] Internet connection

---

## OPTION 1: Modern Approach (Recommended for Windows)

### Step 1: Install Java JDK

**Option A: Java 8 (Recommended - Still compatible with legacy code)**
1. Download Java 8 JDK from: https://adoptium.net/temurin/releases/?version=8
2. Download "Windows x64" installer
3. Run installer, follow prompts
4. Verify installation:
   ```cmd
   java -version
   ```
   Should show: `java version "1.8.0_xxx"`

**Option B: Java 11 (Also works)**
1. Download from: https://adoptium.net/temurin/releases/?version=11
2. Same installation process

### Step 2: Install WildFly

1. Download WildFly 10.1.0 or later from: https://www.wildfly.org/downloads/
   - Choose "WildFly 10.1.0 Final" or newer
   - Download ZIP file (not installer)

2. Extract to a location without spaces:
   ```
   C:\wildfly-10.1.0.Final
   ```
   ⚠️ **Important:** Avoid paths with spaces like `C:\Program Files\`

3. Set environment variable (optional but recommended):
   - Right-click "This PC" → Properties → Advanced System Settings
   - Click "Environment Variables"
   - Under "System variables", click "New"
   - Variable name: `WILDFLY_HOME`
   - Variable value: `C:\wildfly-10.1.0.Final`
   - Click OK

### Step 3: Install Database

**Option A: MySQL (Recommended)**

1. Download MySQL Community Server from: https://dev.mysql.com/downloads/mysql/
   - Choose "Windows (x86, 64-bit), ZIP Archive" or Installer
   - If using installer, choose "Developer Default" setup type

2. During installation:
   - Set root password (remember it!)
   - Note the port (default: 3306)

3. Start MySQL service:
   ```cmd
   net start MySQL80
   ```
   (Service name may vary: MySQL80, MySQL, etc.)

4. Verify MySQL is running:
   ```cmd
   mysql -u root -p
   ```
   Enter your root password, then type `exit`

**Option B: PostgreSQL**

1. Download from: https://www.postgresql.org/download/windows/
2. Run installer, use default options
3. Remember the postgres user password
4. PostgreSQL service starts automatically

### Step 4: Create Database

**For MySQL:**
```cmd
mysql -u root -p
```

Then in MySQL prompt:
```sql
CREATE DATABASE employee_db;
USE employee_db;
SOURCE database/schema.sql
exit
```

**For PostgreSQL:**
```cmd
psql -U postgres
```

Then in PostgreSQL prompt:
```sql
CREATE DATABASE employee_db;
\c employee_db
\i database/schema.sql
\q
```

### Step 5: Configure WildFly DataSource

1. Navigate to WildFly directory:
   ```cmd
   cd C:\wildfly-10.1.0.Final\standalone\configuration
   ```

2. Open `standalone.xml` in a text editor (Notepad++ or VS Code)

3. Find the `<datasources>` section (around line 100-200)

4. **For MySQL**, add this inside `<datasources>`:
   ```xml
   <datasource jndi-name="java:jboss/datasources/EmployeeDS" pool-name="EmployeeDS">
       <connection-url>jdbc:mysql://localhost:3306/employee_db</connection-url>
       <driver>mysql</driver>
       <security>
           <user-name>root</user-name>
           <password>YOUR_MYSQL_PASSWORD</password>
       </security>
   </datasource>
   <drivers>
       <driver name="mysql" module="com.mysql">
           <driver-class>com.mysql.jdbc.Driver</driver-class>
       </driver>
   </drivers>
   ```

5. **For PostgreSQL**, add this:
   ```xml
   <datasource jndi-name="java:jboss/datasources/EmployeeDS" pool-name="EmployeeDS">
       <connection-url>jdbc:postgresql://localhost:5432/employee_db</connection-url>
       <driver>postgresql</driver>
       <security>
           <user-name>postgres</user-name>
           <password>YOUR_POSTGRES_PASSWORD</password>
       </security>
   </datasource>
   <drivers>
       <driver name="postgresql" module="org.postgresql">
           <driver-class>org.postgresql.Driver</driver-class>
       </driver>
   </drivers>
   ```

6. Save the file

### Step 6: Install Database Driver Module

**For MySQL:**

1. Download MySQL JDBC driver: https://dev.mysql.com/downloads/connector/j/
   - Choose "Platform Independent" ZIP
   - Extract and find `mysql-connector-java-x.x.xx.jar`

2. Create module directory:
   ```cmd
   mkdir C:\wildfly-10.1.0.Final\modules\com\mysql\main
   ```

3. Copy JAR file:
   ```cmd
   copy "path\to\mysql-connector-java-x.x.xx.jar" C:\wildfly-10.1.0.Final\modules\com\mysql\main\
   ```

4. Create `module.xml` in `C:\wildfly-10.1.0.Final\modules\com\mysql\main\`:
   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <module xmlns="urn:jboss:module:1.1" name="com.mysql">
       <resources>
           <resource-root path="mysql-connector-java-x.x.xx.jar"/>
       </resources>
       <dependencies>
           <module name="javax.api"/>
       </dependencies>
   </module>
   ```
   (Replace `x.x.xx` with actual version)

**For PostgreSQL:**

1. Download PostgreSQL JDBC driver: https://jdbc.postgresql.org/download/
   - Download `postgresql-x.x-xxx.jdbc4.jar`

2. Create module directory:
   ```cmd
   mkdir C:\wildfly-10.1.0.Final\modules\org\postgresql\main
   ```

3. Copy JAR and create `module.xml` (similar to MySQL)

### Step 7: Build the Application

**Option A: Using Ant (if you have Ant installed)**

1. Install Apache Ant:
   - Download from: https://ant.apache.org/bindownload.cgi
   - Extract to `C:\apache-ant`
   - Add `C:\apache-ant\bin` to PATH

2. Create `build.xml` in project root (see BUILD.md for content)

3. Build:
   ```cmd
   cd D:\project\jsp-ejb-crud-sample
   ant build
   ```

**Option B: Manual Build (Simpler for testing)**

Since manual compilation is complex, I'll create a simplified build script for Windows.

### Step 8: Start WildFly

1. Open Command Prompt as Administrator

2. Navigate to WildFly:
   ```cmd
   cd C:\wildfly-10.1.0.Final\bin
   ```

3. Start WildFly:
   ```cmd
   standalone.bat
   ```

4. Wait for message: `"WildFly 10.1.0.Final started in XXXXms"`

5. Keep this window open (don't close it)

### Step 9: Deploy Application

**Quick Deploy Method:**

1. Copy the entire project structure to WildFly deployments:
   ```cmd
   xcopy /E /I "D:\project\jsp-ejb-crud-sample\web-module" "C:\wildfly-10.1.0.Final\standalone\deployments\employee-demo.war"
   ```

   Actually, for proper deployment, you need to create a WAR/EAR file. Let me create a simple deployment script.

### Step 10: Access Application

1. Open browser: http://localhost:8080/employee-demo/employee?action=list

2. You should see the Employee List page!

---

## OPTION 2: Legacy Approach (Java 6 + WildFly 8)

⚠️ **Warning:** This is more complex and may have compatibility issues on modern Windows.

### Challenges:
- Java 6 is very old (EOL 2013)
- May not work well on Windows 10/11
- Security concerns
- Harder to find/download

### If you still want to try:

1. **Java 6 JDK:**
   - Oracle no longer provides Java 6 downloads
   - You'd need to find archived versions (not recommended)
   - Consider using Java 8 instead (still compatible)

2. **WildFly 8.2.0:**
   - Download from: https://www.wildfly.org/downloads/ (archived versions)
   - Follow similar setup steps

**Recommendation:** Stick with Option 1 (Modern Approach).

---

## Troubleshooting

### WildFly won't start
- Check if port 8080 is already in use:
  ```cmd
  netstat -ano | findstr :8080
  ```
- Check Java version: `java -version`
- Check `standalone.xml` for syntax errors

### Database connection errors
- Verify database is running:
  - MySQL: `net start MySQL80`
  - PostgreSQL: Check Services (services.msc)
- Verify credentials in `standalone.xml`
- Test connection manually:
  ```cmd
  mysql -u root -p employee_db
  ```

### Application not accessible
- Check WildFly logs: `C:\wildfly-10.1.0.Final\standalone\log\server.log`
- Verify deployment: Check `standalone\deployments\` folder
- Check for deployment errors in logs

### Port conflicts
- Change WildFly port in `standalone.xml`:
  ```xml
  <socket-binding name="http" port="${jboss.http.port:8081}"/>
  ```
- Then access: http://localhost:8081/employee-demo/

---

## Quick Test Checklist

- [ ] Java installed and verified
- [ ] WildFly downloaded and extracted
- [ ] Database installed and running
- [ ] Database created with schema
- [ ] DataSource configured in WildFly
- [ ] Database driver module installed
- [ ] WildFly started successfully
- [ ] Application deployed
- [ ] Browser can access application

---

## Next Steps After Setup

1. **Test CRUD Operations:**
   - Create an employee
   - View employee list
   - Edit an employee
   - Delete an employee

2. **Test Validation:**
   - Try creating with duplicate email
   - Try creating with missing required fields

3. **Test Updated Version:**
   - Apply database migration (phoneNumber field)
   - Deploy updated code
   - Test phone number field

---

## Need Help?

If you encounter issues:
1. Check WildFly server logs
2. Check database connection
3. Verify all paths are correct
4. Ensure no firewall blocking ports
5. Try running as Administrator

---

## Alternative: Docker Approach (Advanced)

If you want to avoid manual setup, you could use Docker, but that requires Docker Desktop installation. Let me know if you want Docker instructions.

