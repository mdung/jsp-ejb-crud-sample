# Fix PostgreSQL Driver Module Error

## Error
```
WFLYJCA0115: Module for driver [org.postgresql] or one of it dependencies is missing: [org.postgresql]
```

## Solution: Install PostgreSQL Driver Module

### Step 1: Download PostgreSQL JDBC Driver
Download from: https://jdbc.postgresql.org/download/
- Version: PostgreSQL JDBC Driver 42.x.x (for Java 8+)
- File: `postgresql-42.x.x.jar`

### Step 2: Create Module Directory
```powershell
mkdir C:\wildfly-38.0.1.Final\modules\org\postgresql\main
```

### Step 3: Copy JAR File
```powershell
Copy-Item postgresql-42.x.x.jar C:\wildfly-38.0.1.Final\modules\org\postgresql\main\
```

### Step 4: Create module.xml
Create file: `C:\wildfly-38.0.1.Final\modules\org\postgresql\main\module.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.9" name="org.postgresql">
    <resources>
        <resource-root path="postgresql-42.x.x.jar"/>
    </resources>
    <dependencies>
        <module name="java.sql"/>
        <module name="jakarta.api"/>
    </dependencies>
</module>
```

**Important:** Replace `postgresql-42.x.x.jar` with your actual JAR filename.

### Step 5: Restart WildFly
After installing the module, restart WildFly.

## Quick Check
Verify the module is installed:
```powershell
Test-Path C:\wildfly-38.0.1.Final\modules\org\postgresql\main\module.xml
```




