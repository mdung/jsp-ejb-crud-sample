@echo off
REM Windows Build Script for JSP+EJB CRUD Application
REM This script helps build and package the application for deployment

echo ========================================
echo JSF+EJB CRUD Application - Windows Build
echo Migrated from JSP to JSF (Facelets)
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 11 or later and add it to PATH
    echo WildFly 38 requires Java 11+
    pause
    exit /b 1
)

echo [1/5] Checking Java installation...
java -version
echo.

REM Set paths
set WILDFLY_HOME=%WILDFLY_HOME%
if "%WILDFLY_HOME%"=="" (
    echo WARNING: WILDFLY_HOME not set. Please set it or update this script.
    set WILDFLY_HOME=C:\wildfly-38.0.1.Final
    echo Using default: %WILDFLY_HOME%
)

if not exist "%WILDFLY_HOME%" (
    echo ERROR: WildFly not found at %WILDFLY_HOME%
    echo Please install WildFly and set WILDFLY_HOME environment variable
    pause
    exit /b 1
)

echo [2/5] WildFly found at: %WILDFLY_HOME%
echo.

REM Create build directories
echo [3/5] Creating build directories...
if not exist "build" mkdir build
if not exist "build\ejb-classes" mkdir build\ejb-classes
if not exist "build\web-classes" mkdir build\web-classes
if not exist "dist" mkdir dist

REM Set classpath for EJB compilation (Jakarta EE for WildFly 38)
set EJB_CP=.
set EJB_CP=%EJB_CP%;%WILDFLY_HOME%\modules\system\layers\base\jakarta\ejb\api\main\jakarta.ejb-api-4.0.1.jar
set EJB_CP=%EJB_CP%;%WILDFLY_HOME%\modules\system\layers\base\jakarta\servlet\api\main\jakarta.servlet-api-6.0.0.jar
set EJB_CP=%EJB_CP%;%WILDFLY_HOME%\modules\system\layers\base\jakarta\annotation\api\main\jakarta.annotation-api-2.1.1.jar

echo [4/5] Compiling EJB module...
REM Note: WildFly 38 JARs are Java 11+, but we compile with Java 8 for compatibility
REM If you have Java 11+, change -source and -target to 11
javac -source 1.8 -target 1.8 -Xlint:-options -cp "%EJB_CP%" -d build\ejb-classes ejb-module\src\model\*.java ejb-module\src\dao\*.java ejb-module\src\ejb\*.java
if errorlevel 1 (
    echo ERROR: EJB compilation failed
    pause
    exit /b 1
)
echo EJB module compiled successfully.
echo.

REM Set classpath for Web compilation (JSF support)
set WEB_CP=%EJB_CP%
set WEB_CP=%WEB_CP%;build\ejb-classes

REM Add CDI and Inject APIs - prefer local lib folder (Java 8 compatible), then WildFly modules
if exist "lib\jakarta.enterprise.cdi-api-4.0.1.jar" (
    set WEB_CP=%WEB_CP%;lib\jakarta.enterprise.cdi-api-4.0.1.jar
) else (
    set WEB_CP=%WEB_CP%;%WILDFLY_HOME%\modules\system\layers\base\jakarta\enterprise\api\main\jakarta.enterprise.cdi-api-4.0.1.jar
)

if exist "lib\jakarta.inject-api-2.0.1.jar" (
    set WEB_CP=%WEB_CP%;lib\jakarta.inject-api-2.0.1.jar
) else (
    set WEB_CP=%WEB_CP%;%WILDFLY_HOME%\modules\system\layers\base\jakarta\inject\api\main\jakarta.inject-api-2.0.1.jar
)

REM Add JSF API - prefer local lib folder, then WildFly modules
if exist "lib\jakarta.faces-api-4.0.jar" (
    set WEB_CP=%WEB_CP%;lib\jakarta.faces-api-4.0.jar
) else (
    REM Try explicit path in WildFly
    if exist "%WILDFLY_HOME%\modules\system\layers\base\jakarta\faces\api\main\jakarta.faces-api-4.0.jar" (
        set WEB_CP=%WEB_CP%;%WILDFLY_HOME%\modules\system\layers\base\jakarta\faces\api\main\jakarta.faces-api-4.0.jar
    )
)

echo [5/5] Compiling Web module...
REM Note: WildFly 38 JARs are Java 11+, but we compile with Java 8 for compatibility
REM If you have Java 11+, change -source and -target to 11
javac -source 1.8 -target 1.8 -Xlint:-options -cp "%WEB_CP%" -d build\web-classes web-module\src\controller\*.java
if errorlevel 1 (
    echo ERROR: Web module compilation failed
    pause
    exit /b 1
)
echo Web module compiled successfully.
echo.

REM Create WAR file structure
echo Creating WAR file...
if not exist "build\war" mkdir build\war
if not exist "build\war\WEB-INF" mkdir build\war\WEB-INF
if not exist "build\war\WEB-INF\classes" mkdir build\war\WEB-INF\classes

REM Copy compiled classes
xcopy /E /Y build\web-classes\* build\war\WEB-INF\classes\ >nul
xcopy /E /Y build\ejb-classes\* build\war\WEB-INF\classes\ >nul

REM Copy web.xml and JSF config files
copy /Y web-module\WEB-INF\web.xml build\war\WEB-INF\ >nul
copy /Y web-module\WEB-INF\faces-config.xml build\war\WEB-INF\ >nul
copy /Y web-module\WEB-INF\beans.xml build\war\WEB-INF\ >nul

REM Copy XHTML files (JSF Facelets)
copy /Y web-module\*.xhtml build\war\ >nul 2>&1
if errorlevel 1 (
    echo WARNING: Some XHTML files may not have been copied
)

REM Create WAR file using jar command
echo Packaging WAR file...
cd build\war
jar cvf ..\..\dist\employee-demo.war * >nul
cd ..\..

if exist "dist\employee-demo.war" (
    echo.
    echo ========================================
    echo BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo WAR file created: dist\employee-demo.war
    echo.
    echo To deploy:
    echo 1. Copy dist\employee-demo.war to %WILDFLY_HOME%\standalone\deployments\
    echo 2. WildFly will auto-deploy it
    echo 3. Access: http://localhost:8080/employee-demo/employee-list.xhtml
    echo.
) else (
    echo ERROR: WAR file creation failed
    pause
    exit /b 1
)

pause

