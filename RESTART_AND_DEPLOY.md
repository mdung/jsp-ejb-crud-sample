# Restart WildFly and Deploy - Final Steps

## Current Status

✅ **WAR file built successfully**  
✅ **XML configuration fixed**  
⚠️ **WildFly needs restart** to load new datasource configuration

## Action Required: Restart WildFly

WildFly must be restarted for datasource changes to take effect.

### Step 1: Stop WildFly

1. Find the WildFly console window (where you ran `standalone.bat`)
2. Press `Ctrl+C` to stop WildFly
3. Wait for it to fully stop

### Step 2: Start WildFly Again

```cmd
cd C:\wildfly-38.0.1.Final\bin
standalone.bat
```

Wait for this message:
```
WFLYSRV0025: WildFly Full 38.0.1.Final started in XXXXms
```

### Step 3: Deploy Application

In a **new** Command Prompt window:

```cmd
cd D:\project\jsp-ejb-crud-sample
copy dist\employee-demo.war C:\wildfly-38.0.1.Final\standalone\deployments\
```

### Step 4: Verify Deployment

Check for successful deployment:
```cmd
dir C:\wildfly-38.0.1.Final\standalone\deployments\employee-demo.war*
```

You should see:
- `employee-demo.war` ✅
- `employee-demo.war.deployed` ✅ (NOT `.failed`)

### Step 5: Test Application

Open browser:
```
http://localhost:8080/employee-demo/employee?action=list
```

You should see the Employee List page with 3 sample employees!

## If Deployment Still Fails

Check WildFly logs:
```
C:\wildfly-38.0.1.Final\standalone\log\server.log
```

Look for errors about:
- DataSource connection
- Database connection
- Module not found

## Quick Test Commands

After restart and deployment:

```powershell
# Check deployment status
Get-ChildItem C:\wildfly-38.0.1.Final\standalone\deployments\employee-demo.war*

# Test application
Invoke-WebRequest -Uri "http://localhost:8080/employee-demo/employee?action=list" -UseBasicParsing
```

## Expected Result

✅ Application accessible at: `http://localhost:8080/employee-demo/employee?action=list`  
✅ Employee list page displays  
✅ Can create, view, edit, delete employees


