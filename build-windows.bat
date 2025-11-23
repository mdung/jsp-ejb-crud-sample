@echo off
REM Windows Build Script for JSP+EJB CRUD Application
REM This script helps build and package the application for deployment

echo ========================================
echo JSP+EJB CRUD Application - Windows Build
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 8 or later and add it to PATH
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

REM Set classpath for EJB compilation
set EJB_CP=.
set EJB_CP=%EJB_CP%;%WILDFLY_HOME%\modules\system\layers\base\javax\ejb\api\main\*.jar
set EJB_CP=%EJB_CP%;%WILDFLY_HOME%\modules\system\layers\base\javax\servlet\api\main\*.jar
set EJB_CP=%EJB_CP%;%WILDFLY_HOME%\modules\system\layers\base\javax\annotation\api\main\*.jar

echo [4/5] Compiling EJB module...
javac -source 1.8 -target 1.8 -cp "%EJB_CP%" -d build\ejb-classes ejb-module\src\model\*.java ejb-module\src\dao\*.java ejb-module\src\ejb\*.java
if errorlevel 1 (
    echo ERROR: EJB compilation failed
    pause
    exit /b 1
)
echo EJB module compiled successfully.
echo.

REM Set classpath for Web compilation
set WEB_CP=%EJB_CP%
set WEB_CP=%WEB_CP%;build\ejb-classes
set WEB_CP=%WEB_CP%;%WILDFLY_HOME%\modules\system\layers\base\javax\servlet\jsp\api\main\*.jar

echo [5/5] Compiling Web module...
javac -source 1.8 -target 1.8 -cp "%WEB_CP%" -d build\web-classes web-module\src\controller\*.java
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

REM Copy web.xml
copy /Y web-module\WEB-INF\web.xml build\war\WEB-INF\ >nul

REM Copy JSP files
copy /Y web-module\*.jsp build\war\ >nul

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
    echo 3. Access: http://localhost:8080/employee-demo/employee?action=list
    echo.
) else (
    echo ERROR: WAR file creation failed
    pause
    exit /b 1
)

pause

