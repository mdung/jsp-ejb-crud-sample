# Quick Fix - Complete Deployment

## The Problem

Deployment fails because `ExampleDS` datasource is not available in the running WildFly instance.

**Error:** `Required services that are not installed: ["jboss.naming.context.java.jboss.datasources.ExampleDS"]`

## The Solution

WildFly needs to be restarted to load the datasource configuration from `standalone.xml`.

## Quick Steps (2 minutes)

### Step 1: Restart WildFly

1. **Find the WildFly console window** (where you ran `standalone.bat`)
2. **Press `Ctrl+C`** to stop WildFly
3. **Wait for it to fully stop**
4. **Start WildFly again:**
   ```cmd
   cd C:\wildfly-38.0.1.Final\bin
   standalone.bat
   ```
5. **Wait for:** `"WildFly Full 38.0.1.Final started"`

### Step 2: Deploy Application

**In PowerShell (or cmd with powershell command):**
```powershell
cd D:\project\jsp-ejb-crud-sample
powershell -ExecutionPolicy Bypass -File .\deploy.ps1
```

**Or manually:**
```cmd
copy dist\employee-demo.war C:\wildfly-38.0.1.Final\standalone\deployments\
```

### Step 3: Verify

1. Check for `employee-demo.war.deployed` (NOT `.failed`)
2. Open browser: `http://localhost:8080/employee-demo/employee?action=list`
3. You should see the Employee List page!

## Why This Works

- ✅ `standalone.xml` has both datasources configured correctly
- ✅ PostgreSQL driver module is installed
- ✅ WAR file is built and ready
- ✅ After restart, WildFly will load ExampleDS from XML
- ✅ Application will deploy successfully

## Expected Result

After restart and deployment:
- ✅ Application accessible at the URL above
- ✅ Employee list shows 3 sample employees
- ✅ All CRUD operations work

---

**Everything is ready! Just restart WildFly! 🚀**


