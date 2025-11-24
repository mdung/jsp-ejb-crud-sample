# Deploy Script Explanation

## Where to Run

**Location**: Project root directory
```
D:\project\jsp-ejb-crud-sample
```

**Command**:
```powershell
powershell -ExecutionPolicy Bypass -File .\deploy.ps1
```

Or simply (if already in PowerShell):
```powershell
.\deploy.ps1
```

## Why Run This Script?

### Purpose
The `deploy.ps1` script automates the deployment of your WAR file to WildFly application server.

### What It Does

1. **Removes Old Deployment**
   - Deletes any existing `employee-demo.war` files from WildFly deployments folder
   - Ensures clean deployment

2. **Copies New WAR File**
   - Copies `dist\employee-demo.war` to `C:\wildfly-38.0.1.Final\standalone\deployments\`
   - WildFly automatically detects and deploys files in this folder

3. **Waits for Deployment**
   - Waits 6 seconds for WildFly to process the deployment
   - WildFly creates marker files (`.deployed` or `.failed`) to indicate status

4. **Checks Deployment Status**
   - Checks if `employee-demo.war.deployed` exists (success)
   - Checks if `employee-demo.war.failed` exists (failure)
   - Provides feedback on deployment status

5. **Tests Application**
   - Makes an HTTP request to verify the application is responding
   - Shows the access URL

## When to Run

### ✅ Run After:
1. **Building the application** (`.\build-windows.bat`)
2. **Restarting WildFly** (after configuration changes)
3. **Making code changes** (after rebuilding)

### ⚠️ Prerequisites:
- WildFly must be **running** (`standalone.bat`)
- WAR file must exist in `dist\employee-demo.war`
- WildFly must be at: `C:\wildfly-38.0.1.Final`

## Why `-ExecutionPolicy Bypass`?

PowerShell has execution policies that may block scripts. This flag:
- Temporarily bypasses the execution policy
- Allows the script to run without changing system settings
- Only applies to this single script execution

## Alternative: Manual Deployment

If you prefer manual deployment:

```cmd
copy dist\employee-demo.war C:\wildfly-38.0.1.Final\standalone\deployments\
```

Then check for:
- `employee-demo.war.deployed` ✅ (success)
- `employee-demo.war.failed` ❌ (check logs)

## Application URLs

### JSF Application (Current):
- **Employee List**: `http://localhost:8080/employee-demo/employee-list.xhtml`
- **Home**: `http://localhost:8080/employee-demo/index.xhtml`

### Old JSP URLs (if still available):
- `http://localhost:8080/employee-demo/employee?action=list`

## Troubleshooting

### If Deployment Fails:
1. Check WildFly logs: `C:\wildfly-38.0.1.Final\standalone\log\server.log`
2. Verify WildFly is running
3. Check database connection (PostgreSQL)
4. Verify datasources are configured

### If Script Doesn't Run:
- Make sure you're in the project root directory
- Check PowerShell execution policy: `Get-ExecutionPolicy`
- Try: `powershell -ExecutionPolicy Bypass -File .\deploy.ps1`

## Complete Deployment Workflow

```powershell
# 1. Build the application
.\build-windows.bat

# 2. Make sure WildFly is running
# (In separate window: cd C:\wildfly-38.0.1.Final\bin && standalone.bat)

# 3. Deploy
.\deploy.ps1

# 4. Access application
# http://localhost:8080/employee-demo/employee-list.xhtml
```

## Summary

**Where**: Project root (`D:\project\jsp-ejb-crud-sample`)  
**Why**: Automate WAR file deployment to WildFly  
**When**: After building or restarting WildFly  
**Result**: Application deployed and accessible via browser

