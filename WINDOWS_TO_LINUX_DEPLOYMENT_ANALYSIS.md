# Windows to Linux Deployment Migration Analysis

## Overview
This document analyzes all files and steps needed to deploy the application on Linux instead of Windows.

---

## Files That MUST Be Changed/Created

### 1. Build Scripts

#### 1.1 `build-windows.bat` → **CREATE `build-linux.sh`**
**Status:** ⚠️ **MUST CREATE NEW FILE**

**Windows-specific elements:**
- `@echo off` → `#!/bin/bash`
- `set VAR=value` → `export VAR=value`
- `if not exist` → `if [ ! -d ]` or `if [ ! -f ]`
- `mkdir` → `mkdir -p`
- `copy` → `cp`
- `xcopy` → `cp -r`
- `%VAR%` → `$VAR`
- `\` path separators → `/`
- `C:\wildfly-38.0.1.Final` → `/opt/wildfly` or `$WILDFLY_HOME`
- `pause` → `read -p "Press enter to continue"`
- `exit /b 1` → `exit 1`

**Key changes needed:**
```bash
# Windows:
set WILDFLY_HOME=C:\wildfly-38.0.1.Final
if not exist "%WILDFLY_HOME%" mkdir build

# Linux:
export WILDFLY_HOME=${WILDFLY_HOME:-/opt/wildfly}
if [ ! -d "build" ]; then mkdir -p build; fi
```

**Action:** Create `build-linux.sh` with Linux equivalents

---

#### 1.2 `run-tests.bat` → **CREATE `run-tests.sh`**
**Status:** ⚠️ **MUST CREATE NEW FILE**

**Windows-specific elements:**
- `set CLASSPATH=...` → `export CLASSPATH=...`
- `for %%f in (...)` → `for f in ...; do ...; done`
- `%CLASSPATH%` → `$CLASSPATH`
- `build\ejb-classes` → `build/ejb-classes`
- `C:\wildfly-38.0.1.Final` → `$WILDFLY_HOME` or `/opt/wildfly`

**Key changes:**
```bash
# Windows:
for %%f in ("%WILDFLY_HOME%\modules\*.jar") do set CLASSPATH=%CLASSPATH%;%%f

# Linux:
for f in $WILDFLY_HOME/modules/*.jar; do
    CLASSPATH=$CLASSPATH:$f
done
```

**Action:** Create `run-tests.sh` with Linux equivalents

---

### 2. Deployment Scripts

#### 2.1 `deploy.ps1` → **CREATE `deploy.sh`**
**Status:** ⚠️ **MUST CREATE NEW FILE**

**Windows PowerShell → Linux Bash conversion:**

**Windows:**
```powershell
Remove-Item "C:\wildfly-38.0.1.Final\standalone\deployments\employee-demo.war*"
Copy-Item "dist\employee-demo.war" "C:\wildfly-38.0.1.Final\standalone\deployments\"
Start-Sleep -Seconds 6
Test-Path "C:\wildfly-38.0.1.Final\standalone\deployments\employee-demo.war.deployed"
```

**Linux:**
```bash
rm -f $WILDFLY_HOME/standalone/deployments/employee-demo.war*
cp dist/employee-demo.war $WILDFLY_HOME/standalone/deployments/
sleep 6
test -f $WILDFLY_HOME/standalone/deployments/employee-demo.war.deployed
```

**Action:** Create `deploy.sh` with bash equivalents

---

#### 2.2 `redeploy.ps1` → **CREATE `redeploy.sh`**
**Status:** ⚠️ **MUST CREATE NEW FILE**

**Similar conversion as deploy.ps1**

**Action:** Create `redeploy.sh`

---

#### 2.3 `fix-datasource.ps1` → **CREATE `fix-datasource.sh`**
**Status:** ⚠️ **MUST CREATE NEW FILE**

**Windows:**
```powershell
$standaloneXml = "C:\wildfly-38.0.1.Final\standalone\configuration\standalone.xml"
$content = Get-Content $standaloneXml -Raw
```

**Linux:**
```bash
STANDALONE_XML="$WILDFLY_HOME/standalone/configuration/standalone.xml"
CONTENT=$(cat "$STANDALONE_XML")
```

**Action:** Create `fix-datasource.sh` using `sed` or `awk` for XML manipulation

---

#### 2.4 `test-all-functionalities.ps1` → **CREATE `test-all-functionalities.sh`**
**Status:** ⚠️ **MUST CREATE NEW FILE**

**PowerShell → Bash conversion for HTTP testing:**
- `Invoke-WebRequest` → `curl` or `wget`
- PowerShell variables → Bash variables
- PowerShell conditionals → Bash conditionals

**Action:** Create `test-all-functionalities.sh`

---

### 3. WildFly Configuration (Outside Project)

#### 3.1 `$WILDFLY_HOME/standalone/configuration/standalone.xml`
**Status:** ⚠️ **MUST MODIFY** (but same changes as Windows)

**Changes needed:**
- Path separators: `C:\wildfly-38.0.1.Final` → `/opt/wildfly` or `$WILDFLY_HOME`
- Connection URLs: Same (localhost, ports same)
- Driver paths: Same module structure

**Note:** The XML content is the same, just different installation path

---

#### 3.2 `$WILDFLY_HOME/modules/org/postgresql/main/module.xml`
**Status:** ✅ **NO CHANGES** (same structure)

**Note:** Module structure is OS-independent

---

### 4. Environment Variables

#### 4.1 WILDFLY_HOME
**Status:** ⚠️ **MUST SET** (different method)

**Windows:**
```cmd
setx WILDFLY_HOME "C:\wildfly-38.0.1.Final" /M
set WILDFLY_HOME=C:\wildfly-38.0.1.Final
```

**Linux:**
```bash
# System-wide (in /etc/environment or /etc/profile)
export WILDFLY_HOME=/opt/wildfly

# User-specific (in ~/.bashrc or ~/.profile)
export WILDFLY_HOME=/opt/wildfly

# Or set in script
export WILDFLY_HOME=${WILDFLY_HOME:-/opt/wildfly}
```

**Action:** Set environment variable using Linux method

---

### 5. WildFly Startup

#### 5.1 Startup Script
**Status:** ⚠️ **DIFFERENT COMMAND**

**Windows:**
```cmd
cd C:\wildfly-38.0.1.Final\bin
standalone.bat
```

**Linux:**
```bash
cd $WILDFLY_HOME/bin
./standalone.sh
# OR
$WILDFLY_HOME/bin/standalone.sh
```

**Action:** Use `standalone.sh` instead of `standalone.bat`

---

### 6. File Permissions

#### 6.1 Script Execution Permissions
**Status:** ⚠️ **MUST SET**

**Linux requires execute permission:**
```bash
chmod +x build-linux.sh
chmod +x deploy.sh
chmod +x redeploy.sh
chmod +x run-tests.sh
chmod +x test-all-functionalities.sh
```

**Action:** Set execute permissions on all shell scripts

---

## Files That DON'T Need Changes

### ✅ Application Code (No Changes)
1. **All Java files** (`*.java`)
   - Platform-independent
   - Same code works on both OS

2. **All XHTML/JSF files** (`*.xhtml`)
   - Platform-independent
   - Same files work on both OS

3. **Configuration files:**
   - `web.xml` - Same
   - `faces-config.xml` - Same
   - `beans.xml` - Same
   - `ejb-jar.xml` - Same

4. **Database schema files:**
   - `schema-postgresql.sql` - Same SQL (just different execution method)
   - `schema.sql` - Same

5. **WAR file structure:**
   - Same structure on both platforms

---

## Step-by-Step Migration Process

### Step 1: Prepare Linux Environment

1. **Install Java 11+**
   ```bash
   sudo apt-get update
   sudo apt-get install openjdk-11-jdk
   # OR
   sudo yum install java-11-openjdk-devel
   ```

2. **Install WildFly**
   ```bash
   cd /opt
   sudo wget https://download.jboss.org/wildfly/38.0.1.Final/wildfly-38.0.1.Final.tar.gz
   sudo tar -xzf wildfly-38.0.1.Final.tar.gz
   sudo mv wildfly-38.0.1.Final wildfly
   sudo chown -R $USER:$USER /opt/wildfly
   ```

3. **Set Environment Variable**
   ```bash
   echo 'export WILDFLY_HOME=/opt/wildfly' >> ~/.bashrc
   source ~/.bashrc
   ```

4. **Install Database** (PostgreSQL or MySQL)
   ```bash
   # PostgreSQL
   sudo apt-get install postgresql postgresql-contrib
   
   # OR MySQL
   sudo apt-get install mysql-server
   ```

---

### Step 2: Create Linux Scripts

**Files to create:**
1. `build-linux.sh` - Convert from `build-windows.bat`
2. `deploy.sh` - Convert from `deploy.ps1`
3. `redeploy.sh` - Convert from `redeploy.ps1`
4. `run-tests.sh` - Convert from `run-tests.bat`
5. `test-all-functionalities.sh` - Convert from `test-all-functionalities.ps1`
6. `fix-datasource.sh` - Convert from `fix-datasource.ps1` (optional)

**Set permissions:**
```bash
chmod +x *.sh
```

---

### Step 3: Update Paths in Scripts

**Key path changes:**
- `C:\wildfly-38.0.1.Final` → `$WILDFLY_HOME` or `/opt/wildfly`
- `D:\project\jsp-ejb-crud-sample` → `/home/user/projects/jsp-ejb-crud-sample` or `$PROJECT_HOME`
- `build\ejb-classes` → `build/ejb-classes`
- `dist\employee-demo.war` → `dist/employee-demo.war` (same, just different separator)

---

### Step 4: Configure WildFly on Linux

1. **Install Database Driver Module**
   ```bash
   mkdir -p $WILDFLY_HOME/modules/org/postgresql/main
   cp postgresql-42.7.1.jar $WILDFLY_HOME/modules/org/postgresql/main/
   # Create module.xml (same content as Windows)
   ```

2. **Configure DataSource in standalone.xml**
   ```bash
   vi $WILDFLY_HOME/standalone/configuration/standalone.xml
   # Same XML content, just different file path
   ```

---

### Step 5: Database Setup on Linux

1. **Create Database**
   ```bash
   # PostgreSQL
   sudo -u postgres createdb employee_db
   sudo -u postgres psql -d employee_db -f database/schema-postgresql.sql
   
   # OR MySQL
   mysql -u root -p
   CREATE DATABASE employee_db;
   USE employee_db;
   SOURCE database/schema.sql;
   ```

---

### Step 6: Build and Deploy

1. **Build Application**
   ```bash
   ./build-linux.sh
   ```

2. **Start WildFly**
   ```bash
   $WILDFLY_HOME/bin/standalone.sh
   # OR run in background:
   nohup $WILDFLY_HOME/bin/standalone.sh > /dev/null 2>&1 &
   ```

3. **Deploy Application**
   ```bash
   ./deploy.sh
   # OR manually:
   cp dist/employee-demo.war $WILDFLY_HOME/standalone/deployments/
   ```

---

## Command Equivalents Table

| Windows Command | Linux Equivalent | Notes |
|----------------|------------------|-------|
| `mkdir` | `mkdir -p` | `-p` creates parent dirs |
| `copy` | `cp` | Copy file |
| `xcopy /E` | `cp -r` | Recursive copy |
| `del` | `rm` | Delete file |
| `rmdir /S` | `rm -rf` | Remove directory |
| `if exist` | `test -f` or `[ -f ]` | Check if file exists |
| `if not exist` | `[ ! -f ]` | Check if file doesn't exist |
| `set VAR=value` | `export VAR=value` | Set environment variable |
| `%VAR%` | `$VAR` | Use variable |
| `echo text` | `echo "text"` | Output text |
| `pause` | `read -p "..."` | Wait for input |
| `type file.txt` | `cat file.txt` | Display file content |
| `dir` | `ls -la` | List files |
| `cd \` | `cd /` | Root directory |
| `C:\path` | `/opt/path` | Absolute path |
| `.\script.bat` | `./script.sh` | Run script |
| `standalone.bat` | `./standalone.sh` | WildFly startup |

---

## Path Separator Changes

### Windows Paths:
```
C:\wildfly-38.0.1.Final\standalone\deployments\
D:\project\jsp-ejb-crud-sample\dist\
build\ejb-classes\
```

### Linux Paths:
```
/opt/wildfly/standalone/deployments/
/home/user/projects/jsp-ejb-crud-sample/dist/
build/ejb-classes/
```

**Or use environment variables:**
```
$WILDFLY_HOME/standalone/deployments/
$PROJECT_HOME/dist/
build/ejb-classes/
```

---

## Script Conversion Examples

### Example 1: Build Script Header

**Windows (`build-windows.bat`):**
```bat
@echo off
REM Windows Build Script
set WILDFLY_HOME=C:\wildfly-38.0.1.Final
if not exist "build" mkdir build
```

**Linux (`build-linux.sh`):**
```bash
#!/bin/bash
# Linux Build Script
export WILDFLY_HOME=${WILDFLY_HOME:-/opt/wildfly}
if [ ! -d "build" ]; then mkdir -p build; fi
```

---

### Example 2: Classpath Building

**Windows:**
```bat
set EJB_CP=.
set EJB_CP=%EJB_CP%;%WILDFLY_HOME%\modules\jakarta\ejb\api\main\*.jar
```

**Linux:**
```bash
EJB_CP="."
EJB_CP="$EJB_CP:$WILDFLY_HOME/modules/system/layers/base/jakarta/ejb/api/main/jakarta.ejb-api-4.0.1.jar"
```

---

### Example 3: File Operations

**Windows:**
```bat
if exist "dist\employee-demo.war" (
    copy dist\employee-demo.war C:\wildfly-38.0.1.Final\standalone\deployments\
)
```

**Linux:**
```bash
if [ -f "dist/employee-demo.war" ]; then
    cp dist/employee-demo.war $WILDFLY_HOME/standalone/deployments/
fi
```

---

## Environment-Specific Considerations

### Linux-Specific Items

1. **File Permissions:**
   - Scripts need `chmod +x`
   - WildFly user may need specific permissions
   - Database user permissions

2. **Service Management:**
   - Can run WildFly as systemd service
   - Can run PostgreSQL/MySQL as services
   - Better process management

3. **Path Case Sensitivity:**
   - Linux is case-sensitive
   - `Build` ≠ `build`
   - Windows is case-insensitive

4. **Line Endings:**
   - Windows: CRLF (`\r\n`)
   - Linux: LF (`\n`)
   - Use `dos2unix` to convert if needed

5. **User Management:**
   - May need to run as specific user
   - `sudo` for system operations
   - User/group permissions

---

## Migration Checklist

### Pre-Migration
- [ ] Linux server prepared
- [ ] Java 11+ installed
- [ ] WildFly downloaded and extracted
- [ ] Database installed (PostgreSQL/MySQL)
- [ ] Environment variables set

### Script Creation
- [ ] Create `build-linux.sh`
- [ ] Create `deploy.sh`
- [ ] Create `redeploy.sh`
- [ ] Create `run-tests.sh`
- [ ] Create `test-all-functionalities.sh`
- [ ] Set execute permissions (`chmod +x *.sh`)

### Configuration
- [ ] Configure WildFly datasource in `standalone.xml`
- [ ] Install database driver module
- [ ] Create database and run schema
- [ ] Set `WILDFLY_HOME` environment variable

### Testing
- [ ] Build application (`./build-linux.sh`)
- [ ] Start WildFly (`./standalone.sh`)
- [ ] Deploy application (`./deploy.sh`)
- [ ] Test application (browser or `curl`)
- [ ] Run tests (`./run-tests.sh`)

---

## Files Summary

### Must Create (Linux Equivalents)
1. ✅ `build-linux.sh` (from `build-windows.bat`)
2. ✅ `deploy.sh` (from `deploy.ps1`)
3. ✅ `redeploy.sh` (from `redeploy.ps1`)
4. ✅ `run-tests.sh` (from `run-tests.bat`)
5. ✅ `test-all-functionalities.sh` (from `test-all-functionalities.ps1`)
6. ✅ `fix-datasource.sh` (from `fix-datasource.ps1`) - Optional

### Must Modify (Configuration)
1. ⚠️ `$WILDFLY_HOME/standalone/configuration/standalone.xml` - Update paths
2. ⚠️ Environment variables - Set `WILDFLY_HOME` using Linux method

### No Changes Needed
1. ✅ All Java source files (`.java`)
2. ✅ All XHTML/JSF files (`.xhtml`)
3. ✅ All configuration XML files (`web.xml`, `faces-config.xml`, etc.)
4. ✅ Database schema SQL files (same SQL, different execution)
5. ✅ WAR file structure (same on both platforms)

---

## Estimated Effort

- **Script creation:** 2-3 hours
- **Testing and debugging:** 1-2 hours
- **Documentation:** 30 minutes
- **Total:** ~4-6 hours

---

## Key Advantages

✅ **Application code is platform-independent:**
- No Java code changes needed
- No XHTML/JSF changes needed
- Only build/deployment scripts need conversion

✅ **Same WAR file works on both:**
- Build once, deploy anywhere
- WAR structure is OS-independent

✅ **Database configuration is same:**
- Same SQL, same connection strings
- Only paths differ

---

## Conclusion

**Total files to create:** ~6 shell scripts
**Total files to modify:** 1-2 configuration files
**Code changes:** ✅ **ZERO** - Application is platform-independent!

The migration is primarily about:
1. Creating Linux shell script equivalents
2. Updating paths and environment variables
3. Setting file permissions
4. Using Linux commands instead of Windows commands

**The application itself requires NO code changes!**

