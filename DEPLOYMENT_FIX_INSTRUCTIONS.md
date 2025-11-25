# Deployment Fix Instructions

## Problem
The deployed WAR file still had the old code with ternary operator in action attribute, causing:
```
Not a Valid Method Expression: #{employee.active ? employeeBean.deactivateEmployee : employeeBean.activateEmployee}
```

## Solution Applied
✅ Updated XHTML files in `build\war\` folder
✅ Rebuilt WAR file: `dist\employee-demo.war`
✅ WAR now uses `toggleEmployeeStatus()` method instead of ternary operator

## Next Steps

### 1. Redeploy to WildFly

**Option A - Use deploy script:**
```powershell
.\deploy.ps1
```

**Option B - Manual deployment:**
```cmd
copy dist\employee-demo.war C:\wildfly-38.0.1.Final\standalone\deployments\
```

### 2. Verify Deployment

Check for:
- `employee-demo.war.deployed` ✅ (success)
- NOT `employee-demo.war.failed` ❌

### 3. Test Application

Open browser:
```
http://localhost:8080/employee-demo/employee-list.xhtml
```

The error should be gone and activate/deactivate buttons should work!

## What Was Fixed

**Before (Error):**
```xhtml
action="#{employee.active ? employeeBean.deactivateEmployee : employeeBean.activateEmployee}"
```

**After (Fixed):**
```xhtml
action="#{employeeBean.toggleEmployeeStatus}"
```

## Files Updated in WAR

- `employee-list.xhtml` - Status column button
- `employee-detail.xhtml` - Status toggle button
- `EmployeeBean.class` - Contains `toggleEmployeeStatus()` method

## Status

✅ Source code: Fixed
✅ WAR file: Rebuilt with fixes
✅ Git: Committed and pushed
⏳ Deployment: Ready (run deploy.ps1)

---

**The WAR file is ready to deploy! Run `.\deploy.ps1` to update WildFly.**

