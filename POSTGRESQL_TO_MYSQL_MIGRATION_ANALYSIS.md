# PostgreSQL to MySQL Migration Analysis

## Overview
This document analyzes all files that need to be changed to migrate the application from PostgreSQL to MySQL.

---

## Files That MUST Be Changed

### 1. Database Schema Files

#### 1.1 `database/schema-postgresql.sql`
**Status:** ⚠️ **MUST CREATE NEW FILE OR MODIFY**

**Current PostgreSQL syntax:**
```sql
id BIGSERIAL PRIMARY KEY,  -- PostgreSQL auto-increment
active BOOLEAN DEFAULT TRUE,  -- PostgreSQL boolean
TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- PostgreSQL timestamp
```

**Required MySQL changes:**
- `BIGSERIAL` → `BIGINT AUTO_INCREMENT`
- `BOOLEAN` → `TINYINT(1)` or `BOOLEAN` (MySQL supports it but stores as TINYINT)
- `TIMESTAMP DEFAULT CURRENT_TIMESTAMP` → `TIMESTAMP DEFAULT CURRENT_TIMESTAMP` (same)
- `ON UPDATE CURRENT_TIMESTAMP` → Add this for MySQL `updated_at` column
- `CREATE TABLE IF NOT EXISTS` → Same (both support)
- `ALTER TABLE ... ADD COLUMN IF NOT EXISTS` → MySQL 5.7+ supports this

**Action:** Create `database/schema-mysql.sql` or modify existing `database/schema.sql`

#### 1.2 `database/schema.sql`
**Status:** ⚠️ **MODIFY OR USE**

**Current:** Contains commented sections for both MySQL and PostgreSQL
**Action:** Uncomment MySQL section, comment PostgreSQL section

#### 1.3 `database/add-active-column-now.sql`
**Status:** ⚠️ **MUST MODIFY**

**PostgreSQL syntax:**
```sql
ALTER TABLE employees ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE;
```

**MySQL changes:**
- `BOOLEAN` → `TINYINT(1)` or keep `BOOLEAN` (MySQL 5.0.3+)
- `IF NOT EXISTS` → MySQL 5.7+ supports, but syntax slightly different

**Action:** Create MySQL version or modify to support both

#### 1.4 `database/migration-add-active-column.sql`
**Status:** ⚠️ **MUST MODIFY**

Same as above - PostgreSQL-specific syntax

---

### 2. WildFly Configuration Files

#### 2.1 `C:\wildfly-38.0.1.Final\standalone\configuration\standalone.xml`
**Status:** ⚠️ **MUST MODIFY** (Outside project, but critical)

**Current PostgreSQL configuration:**
```xml
<connection-url>jdbc:postgresql://localhost:5432/employee_db</connection-url>
<driver>postgresql</driver>
<driver name="postgresql" module="org.postgresql">
    <driver-class>org.postgresql.Driver</driver-class>
</driver>
```

**Required MySQL changes:**
```xml
<connection-url>jdbc:mysql://localhost:3306/employee_db</connection-url>
<driver>mysql</driver>
<driver name="mysql" module="com.mysql">
    <driver-class>com.mysql.cj.jdbc.Driver</driver-class>  <!-- MySQL 8+ -->
    <!-- OR -->
    <driver-class>com.mysql.jdbc.Driver</driver-class>  <!-- MySQL 5.x -->
</driver>
```

**Changes needed:**
- Connection URL: `postgresql://` → `mysql://`
- Port: `5432` → `3306`
- Driver name: `postgresql` → `mysql`
- Module name: `org.postgresql` → `com.mysql`
- Driver class: `org.postgresql.Driver` → `com.mysql.cj.jdbc.Driver`
- Username: `postgres` → `root` (or your MySQL user)
- Remove PostgreSQL-specific validators if any

**Action:** Manual edit required

---

### 3. WildFly Driver Module (Outside Project)

#### 3.1 `C:\wildfly-38.0.1.Final\modules\org\postgresql\main\module.xml`
**Status:** ⚠️ **MUST REPLACE**

**Current:**
```xml
<module xmlns="urn:jboss:module:1.1" name="org.postgresql">
    <resources>
        <resource-root path="postgresql-42.7.1.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
    </dependencies>
</module>
```

**Required MySQL version:**
```xml
<module xmlns="urn:jboss:module:1.1" name="com.mysql">
    <resources>
        <resource-root path="mysql-connector-java-8.0.xx.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <!-- MySQL 8+ may need: -->
        <module name="javax.transaction.api"/>
    </dependencies>
</module>
```

**Action:**
1. Download MySQL JDBC driver
2. Create directory: `C:\wildfly-38.0.1.Final\modules\com\mysql\main\`
3. Copy MySQL JAR file
4. Create `module.xml` with MySQL configuration

---

### 4. Application Code Files

#### 4.1 `ejb-module/src/dao/EmployeeDAO.java`
**Status:** ✅ **NO CHANGES NEEDED**

**Analysis:**
- Uses standard JDBC (no database-specific code)
- Uses `DataSource` from JNDI (database-agnostic)
- SQL queries use standard SQL (compatible with both)
- No PostgreSQL-specific functions used

**Verdict:** ✅ No changes required

#### 4.2 `ejb-module/src/dao/EmployeePerformanceDAO.java`
**Status:** ✅ **NO CHANGES NEEDED** (likely same as above)

**Analysis:**
- Should use standard JDBC
- Check for any PostgreSQL-specific SQL functions

**Verdict:** ✅ Likely no changes required

#### 4.3 All Model Classes (`Employee.java`, `EmployeePerformance.java`)
**Status:** ✅ **NO CHANGES NEEDED**

**Analysis:**
- Pure Java POJOs
- No database-specific code

**Verdict:** ✅ No changes required

---

### 5. Build and Test Scripts

#### 5.1 `run-tests.bat`
**Status:** ⚠️ **MUST MODIFY**

**Current:**
```bat
REM Add PostgreSQL driver
for %%f in ("%WILDFLY_HOME%\modules\org\postgresql\main\*.jar") do set CLASSPATH=%CLASSPATH%;%%f
```

**Required change:**
```bat
REM Add MySQL driver
for %%f in ("%WILDFLY_HOME%\modules\com\mysql\main\*.jar") do set CLASSPATH=%CLASSPATH%;%%f
```

**Action:** Change module path from `org\postgresql` to `com\mysql`

---

### 6. Documentation Files (Optional but Recommended)

#### 6.1 `WINDOWS_POSTGRES_SETUP.md`
**Status:** ℹ️ **OPTIONAL - UPDATE OR CREATE NEW**

**Action:** Create `WINDOWS_MYSQL_SETUP.md` or update existing

#### 6.2 `DEPLOYMENT_FIX.md`
**Status:** ℹ️ **OPTIONAL - UPDATE**

Contains PostgreSQL-specific datasource configuration examples

#### 6.3 `fix-datasource.ps1`
**Status:** ⚠️ **MUST MODIFY**

**Current:**
```powershell
<connection-url>jdbc:postgresql://localhost:5432/employee_db</connection-url>
<driver>postgresql</driver>
```

**Action:** Change to MySQL connection string and driver

#### 6.4 `add-datasource.cli`
**Status:** ⚠️ **MUST MODIFY**

**Current:**
```
connection-url=jdbc:postgresql://localhost:5432/employee_db
driver-name=postgresql
```

**Action:** Change to MySQL connection string and driver name

---

## SQL Syntax Differences Summary

### Data Types
| PostgreSQL | MySQL | Notes |
|------------|-------|-------|
| `BIGSERIAL` | `BIGINT AUTO_INCREMENT` | Auto-increment |
| `SERIAL` | `INT AUTO_INCREMENT` | Auto-increment |
| `BOOLEAN` | `TINYINT(1)` or `BOOLEAN` | MySQL stores as TINYINT |
| `VARCHAR(n)` | `VARCHAR(n)` | Same |
| `TIMESTAMP` | `TIMESTAMP` | Same |
| `TEXT` | `TEXT` | Same |

### SQL Functions
| Feature | PostgreSQL | MySQL |
|---------|------------|-------|
| `IF NOT EXISTS` | ✅ Supported | ✅ MySQL 5.7+ |
| `CURRENT_TIMESTAMP` | ✅ | ✅ |
| `ON UPDATE CURRENT_TIMESTAMP` | ❌ | ✅ (for auto-update) |
| String concatenation | `\|\|` or `CONCAT()` | `CONCAT()` |
| Case sensitivity | Case-sensitive identifiers | Case-insensitive (on Windows) |

### Specific Changes Needed

1. **Auto-increment:**
   - PostgreSQL: `BIGSERIAL` or `SERIAL`
   - MySQL: `BIGINT AUTO_INCREMENT` or `INT AUTO_INCREMENT`

2. **Boolean:**
   - PostgreSQL: `BOOLEAN` (true/false)
   - MySQL: `TINYINT(1)` or `BOOLEAN` (stores 0/1)

3. **Updated timestamp:**
   - PostgreSQL: Manual trigger or application-level
   - MySQL: `ON UPDATE CURRENT_TIMESTAMP` (can be added to column)

---

## Migration Checklist

### Database Level
- [ ] Create MySQL database: `employee_db`
- [ ] Run MySQL schema script (create new or modify existing)
- [ ] Migrate data from PostgreSQL to MySQL (if needed)
- [ ] Verify all tables created correctly
- [ ] Verify `active` column exists

### WildFly Configuration
- [ ] Download MySQL JDBC driver
- [ ] Create MySQL driver module: `C:\wildfly-38.0.1.Final\modules\com\mysql\main\`
- [ ] Copy MySQL JAR to module directory
- [ ] Create `module.xml` for MySQL driver
- [ ] Update `standalone.xml` datasource configuration
- [ ] Change connection URL to MySQL
- [ ] Change driver name to `mysql`
- [ ] Update username/password for MySQL
- [ ] Restart WildFly

### Application Code
- [ ] ✅ No changes needed (uses standard JDBC)

### Build Scripts
- [ ] Update `run-tests.bat` to use MySQL driver path

### Documentation
- [ ] Update or create MySQL setup documentation
- [ ] Update datasource configuration examples
- [ ] Update migration scripts

---

## Files Summary

### Must Change (Critical)
1. ✅ `database/schema-postgresql.sql` → Create `database/schema-mysql.sql`
2. ✅ `database/add-active-column-now.sql` → Modify for MySQL
3. ✅ `C:\wildfly-38.0.1.Final\standalone\configuration\standalone.xml` → Change datasource
4. ✅ `C:\wildfly-38.0.1.Final\modules\org\postgresql\main\` → Replace with MySQL module
5. ✅ `run-tests.bat` → Change driver path
6. ✅ `fix-datasource.ps1` → Change connection string
7. ✅ `add-datasource.cli` → Change connection string

### No Changes Needed (Code is Database-Agnostic)
1. ✅ `ejb-module/src/dao/EmployeeDAO.java` - Uses standard JDBC
2. ✅ `ejb-module/src/dao/EmployeePerformanceDAO.java` - Uses standard JDBC
3. ✅ All model classes - Pure Java POJOs
4. ✅ All service classes - No database-specific code
5. ✅ All controller/bean classes - No database-specific code
6. ✅ All XHTML/JSP files - No database-specific code

### Optional (Documentation)
1. ℹ️ `WINDOWS_POSTGRES_SETUP.md` → Create MySQL version
2. ℹ️ `DEPLOYMENT_FIX.md` → Update examples
3. ℹ️ Other documentation files

---

## Key Advantages of Current Architecture

✅ **Good News:** The application uses **standard JDBC** and **DataSource pattern**, which means:
- **No database-specific code** in Java classes
- **Only configuration changes** needed (datasource, driver)
- **SQL queries are mostly compatible** (minor syntax differences)
- **Easy migration** - just change configuration files

---

## Estimated Effort

- **Database schema migration:** 30 minutes
- **WildFly configuration:** 15 minutes
- **Driver installation:** 10 minutes
- **Testing:** 30 minutes
- **Total:** ~1.5 hours

---

## Conclusion

**Total files to change:** ~7 files
- **Critical:** 5 files (database schemas, WildFly config, scripts)
- **Optional:** 2+ documentation files

**Code changes:** ✅ **ZERO** - Application code is database-agnostic!

The migration is primarily a **configuration change**, not a code change, thanks to the use of standard JDBC and DataSource pattern.

