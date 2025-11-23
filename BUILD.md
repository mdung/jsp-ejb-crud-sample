# Build and Deployment Instructions

## Prerequisites

- **Java 6** JDK installed
- **WildFly** (or JBoss AS) application server
- **MySQL** or **PostgreSQL** database
- **Maven** or **Ant** (for building) - OR manual compilation
- **Eclipse** or **IntelliJ IDEA** (optional, for IDE-based build)

---

## Environment Setup

### 1. Java 6 Installation

```bash
# Verify Java version
java -version
# Should show: java version "1.6.0_XX"
```

### 2. WildFly Setup

Download WildFly compatible with Java 6:
- WildFly 8.x or 9.x supports Java 6
- Extract to a directory (e.g., `/opt/wildfly`)

### 3. Database Setup

#### MySQL:
```bash
# Create database
mysql -u root -p
CREATE DATABASE employee_db;
USE employee_db;

# Run schema
source database/schema.sql
```

#### PostgreSQL:
```bash
# Create database
createdb employee_db
psql -d employee_db -f database/schema.sql
```

---

## Manual Build Process (Without Maven)

### Step 1: Compile EJB Module

```bash
# Set classpath for EJB dependencies
export EJB_CP=".:$WILDFLY_HOME/modules/system/layers/base/javax/ejb/api/main/jboss-ejb-api_3.2_spec-1.0.0.Final.jar"
export EJB_CP="$EJB_CP:$WILDFLY_HOME/modules/system/layers/base/javax/servlet/api/main/jboss-servlet-api_3.1_spec-1.0.0.Final.jar"
export EJB_CP="$EJB_CP:$WILDFLY_HOME/modules/system/layers/base/javax/annotation/api/main/jboss-annotation-api_1.2_spec-1.0.0.Final.jar"

# Compile EJB module
cd ejb-module/src
javac -cp "$EJB_CP" -d ../classes model/*.java dao/*.java ejb/*.java

# Create JAR
cd ..
mkdir -p META-INF
jar cvf ../ejb-module.jar -C classes . META-INF/ejb-jar.xml
```

### Step 2: Compile Web Module

```bash
# Set classpath for web dependencies
export WEB_CP="$EJB_CP:../ejb-module.jar"
export WEB_CP="$WEB_CP:$WILDFLY_HOME/modules/system/layers/base/javax/servlet/jsp/api/main/jboss-jsp-api_2.3_spec-1.0.2.Final.jar"
export WEB_CP="$WEB_CP:$WILDFLY_HOME/modules/system/layers/base/org/jboss/logging/main/jboss-logging-3.1.4.GA.jar"

# Compile web module
cd web-module/src
javac -cp "$WEB_CP" -d ../classes controller/*.java

# Create WAR structure
cd ..
mkdir -p WEB-INF/classes
cp -r classes/* WEB-INF/classes/
cp -r ../web-module/*.jsp .
cp WEB-INF/web.xml WEB-INF/

# Create WAR
jar cvf ../web-module.war -C . .
```

### Step 3: Create EAR (Enterprise Archive)

```bash
# Create application.xml
cat > META-INF/application.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<application xmlns="http://java.sun.com/xml/ns/javaee"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
             http://java.sun.com/xml/ns/javaee/application_5.xsd"
             version="5">
    <display-name>Employee Demo Application</display-name>
    <module>
        <ejb>ejb-module.jar</ejb>
    </module>
    <module>
        <web>
            <web-uri>web-module.war</web-uri>
            <context-root>employee-demo</context-root>
        </web>
    </module>
</application>
EOF

# Create EAR
jar cvf employee-demo.ear META-INF/application.xml ejb-module.jar web-module.war
```

---

## Using Ant Build Script

Create `build.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project name="employee-demo" default="build" basedir=".">
    
    <property name="wildfly.home" value="/opt/wildfly"/>
    <property name="build.dir" value="build"/>
    <property name="dist.dir" value="dist"/>
    
    <path id="ejb.classpath">
        <fileset dir="${wildfly.home}/modules/system/layers/base">
            <include name="javax/ejb/api/main/*.jar"/>
            <include name="javax/servlet/api/main/*.jar"/>
        </fileset>
    </path>
    
    <target name="clean">
        <delete dir="${build.dir}"/>
        <delete dir="${dist.dir}"/>
    </target>
    
    <target name="compile-ejb">
        <mkdir dir="${build.dir}/ejb-classes"/>
        <javac srcdir="ejb-module/src" 
               destdir="${build.dir}/ejb-classes"
               classpathref="ejb.classpath"
               source="1.6"
               target="1.6"/>
        <copy todir="${build.dir}/ejb-classes">
            <fileset dir="ejb-module/META-INF"/>
        </copy>
    </target>
    
    <target name="compile-web" depends="compile-ejb">
        <mkdir dir="${build.dir}/web-classes"/>
        <path id="web.classpath">
            <path refid="ejb.classpath"/>
            <pathelement location="${build.dir}/ejb-classes"/>
        </path>
        <javac srcdir="web-module/src" 
               destdir="${build.dir}/web-classes"
               classpathref="web.classpath"
               source="1.6"
               target="1.6"/>
    </target>
    
    <target name="package" depends="compile-web">
        <mkdir dir="${dist.dir}"/>
        
        <!-- EJB JAR -->
        <jar destfile="${dist.dir}/ejb-module.jar" basedir="${build.dir}/ejb-classes"/>
        
        <!-- WAR -->
        <war destfile="${dist.dir}/web-module.war" webxml="web-module/WEB-INF/web.xml">
            <classes dir="${build.dir}/web-classes"/>
            <fileset dir="web-module">
                <include name="*.jsp"/>
            </fileset>
        </war>
        
        <!-- EAR -->
        <ear destfile="${dist.dir}/employee-demo.ear" appxml="META-INF/application.xml">
            <fileset dir="${dist.dir}">
                <include name="ejb-module.jar"/>
                <include name="web-module.war"/>
            </fileset>
        </ear>
    </target>
    
    <target name="build" depends="package"/>
    
</project>
```

Run:
```bash
ant build
```

---

## WildFly Configuration

### 1. Configure DataSource

Edit `$WILDFLY_HOME/standalone/configuration/standalone.xml`:

**For MySQL:**
```xml
<datasources>
    <datasource jndi-name="java:jboss/datasources/EmployeeDS" pool-name="EmployeeDS">
        <connection-url>jdbc:mysql://localhost:3306/employee_db</connection-url>
        <driver>mysql</driver>
        <security>
            <user-name>root</user-name>
            <password>yourpassword</password>
        </security>
    </datasource>
    <drivers>
        <driver name="mysql" module="com.mysql">
            <driver-class>com.mysql.jdbc.Driver</driver-class>
        </driver>
    </drivers>
</datasources>
```

**For PostgreSQL:**
```xml
<datasources>
    <datasource jndi-name="java:jboss/datasources/EmployeeDS" pool-name="EmployeeDS">
        <connection-url>jdbc:postgresql://localhost:5432/employee_db</connection-url>
        <driver>postgresql</driver>
        <security>
            <user-name>postgres</user-name>
            <password>yourpassword</password>
        </security>
    </datasource>
    <drivers>
        <driver name="postgresql" module="org.postgresql">
            <driver-class>org.postgresql.Driver</driver-class>
        </driver>
    </drivers>
</datasources>
```

### 2. Install Database Driver Module

**MySQL:**
```bash
# Copy MySQL JDBC driver to WildFly modules
mkdir -p $WILDFLY_HOME/modules/com/mysql/main
cp mysql-connector-java-5.1.xx.jar $WILDFLY_HOME/modules/com/mysql/main/
```

Create `$WILDFLY_HOME/modules/com/mysql/main/module.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.1" name="com.mysql">
    <resources>
        <resource-root path="mysql-connector-java-5.1.xx.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
    </dependencies>
</module>
```

**PostgreSQL:**
```bash
mkdir -p $WILDFLY_HOME/modules/org/postgresql/main
cp postgresql-9.x-xxx.jdbc4.jar $WILDFLY_HOME/modules/org/postgresql/main/
```

Create `$WILDFLY_HOME/modules/org/postgresql/main/module.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.1" name="org.postgresql">
    <resources>
        <resource-root path="postgresql-9.x-xxx.jdbc4.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
    </dependencies>
</module>
```

---

## Deployment

### Option 1: Copy to Deployment Directory

```bash
# Copy EAR to WildFly deployment directory
cp employee-demo.ear $WILDFLY_HOME/standalone/deployments/
```

WildFly will automatically deploy the application.

### Option 2: Using Management Console

1. Start WildFly: `$WILDFLY_HOME/bin/standalone.sh`
2. Access Management Console: `http://localhost:9990`
3. Navigate to Deployments → Add → Upload
4. Select `employee-demo.ear`
5. Click Deploy

### Option 3: Using CLI

```bash
$WILDFLY_HOME/bin/jboss-cli.sh --connect
deploy /path/to/employee-demo.ear
```

---

## Verify Deployment

1. **Check Server Logs:**
   ```bash
   tail -f $WILDFLY_HOME/standalone/log/server.log
   ```
   Look for: `Deployed "employee-demo.ear"`

2. **Access Application:**
   - List Page: `http://localhost:8080/employee-demo/employee?action=list`
   - Or: `http://localhost:8080/employee-demo/`

3. **Check EJB JNDI:**
   ```bash
$WILDFLY_HOME/bin/jboss-cli.sh --connect
/subsystem=naming:read-resource(recursive=true)
```

---

## Troubleshooting

### ClassNotFoundException
- Verify all JARs are in EAR
- Check module dependencies in WildFly

### DataSource Not Found
- Verify DataSource JNDI name matches `ejb-jar.xml` and `EmployeeDAO.java`
- Check database connection in WildFly admin console

### EJB Not Found
- Verify EJB JNDI name in `EmployeeServlet.java`
- Check EJB deployment in server logs
- Use `jboss-cli.sh` to list deployed EJBs

### JSP Compilation Errors
- Ensure JSTL libraries are available
- Check JSP syntax (no scriptlets)

---

## Build Output

After successful build:
```
dist/
  ├── ejb-module.jar
  ├── web-module.war
  └── employee-demo.ear
```

Deploy `employee-demo.ear` to WildFly.

