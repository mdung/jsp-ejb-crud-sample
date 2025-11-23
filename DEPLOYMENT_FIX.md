# Deployment Fix - Manual Steps Required

## Current Status

The application WAR file has been built successfully, but deployment is failing because:
1. ✅ PostgreSQL driver module is installed
2. ✅ EmployeeDS datasource is configured (but may have XML issues)
3. ❌ ExampleDS datasource is missing (required by WildFly default bindings)

## Manual Fix Required

Since automated XML editing has caused structure issues, please manually edit `C:\wildfly-38.0.1.Final\standalone\configuration\standalone.xml`:

### Step 1: Open standalone.xml
Open in a text editor (Notepad++, VS Code, or even Notepad):
```
C:\wildfly-38.0.1.Final\standalone\configuration\standalone.xml
```

### Step 2: Find the `<datasources>` section
Search for: `<datasources>`

### Step 3: Ensure proper structure

The `<datasources>` section should look like this:

```xml
<datasources>
    <!-- Drivers section FIRST -->
    <drivers>
        <driver name="postgresql" module="org.postgresql">
            <driver-class>org.postgresql.Driver</driver-class>
        </driver>
    </drivers>
    
    <!-- Then datasources -->
    <datasource jndi-name="java:jboss/datasources/ExampleDS" pool-name="ExampleDS" enabled="true">
        <connection-url>jdbc:postgresql://localhost:5432/employee_db</connection-url>
        <driver>postgresql</driver>
        <security>
            <user-name>postgres</user-name>
            <password>YOUR_POSTGRES_PASSWORD</password>
        </security>
    </datasource>
    
    <datasource jndi-name="java:jboss/datasources/EmployeeDS" pool-name="EmployeeDS" enabled="true">
        <connection-url>jdbc:postgresql://localhost:5432/employee_db</connection-url>
        <driver>postgresql</driver>
        <security>
            <user-name>postgres</user-name>
            <password>YOUR_POSTGRES_PASSWORD</password>
        </security>
    </datasource>
</datasources>
```

**⚠️ IMPORTANT:**
- Replace `YOUR_POSTGRES_PASSWORD` with your actual PostgreSQL password (appears twice)
- Make sure XML is properly formatted (indentation doesn't matter, but tags must be closed)
- Drivers section must come BEFORE datasources

### Step 4: Save and Restart WildFly

1. Save `standalone.xml`
2. **Restart WildFly** (stop and start again) - this is important for XML changes
3. Wait for WildFly to fully start
4. Deploy the WAR file again

### Step 5: Deploy Application

```cmd
copy D:\project\jsp-ejb-crud-sample\dist\employee-demo.war C:\wildfly-38.0.1.Final\standalone\deployments\
```

### Step 6: Verify Deployment

Check for `employee-demo.war.deployed` file (not `.failed`):
```cmd
dir C:\wildfly-38.0.1.Final\standalone\deployments\employee-demo.war*
```

### Step 7: Test Application

Open browser:
```
http://localhost:8080/employee-demo/employee?action=list
```

## Quick Checklist

- [ ] PostgreSQL database `employee_db` exists
- [ ] Table `employees` exists with sample data
- [ ] PostgreSQL driver module installed at `C:\wildfly-38.0.1.Final\modules\org\postgresql\main\`
- [ ] `module.xml` uses `jakarta.api` (not `javax.api`)
- [ ] `standalone.xml` has both `ExampleDS` and `EmployeeDS` datasources
- [ ] Both datasources have correct password
- [ ] WildFly restarted after XML changes
- [ ] WAR file deployed successfully
- [ ] Application accessible in browser

## If Still Failing

Check WildFly logs:
```
C:\wildfly-38.0.1.Final\standalone\log\server.log
```

Look for ERROR messages related to:
- DataSource connection
- Module not found
- Deployment errors


