@echo off
echo ========================================
echo EmployeeService Unit Tests Runner
echo ========================================
echo.

REM Set classpath - include WildFly modules and compiled classes
set WILDFLY_HOME=C:\wildfly-38.0.1.Final
set CLASSPATH=.

REM Add compiled classes
set CLASSPATH=%CLASSPATH%;build\ejb-classes
set CLASSPATH=%CLASSPATH%;build\web-classes

REM Add WildFly modules (Jakarta EE APIs)
for %%f in ("%WILDFLY_HOME%\modules\jakarta\ejb\api\main\*.jar") do set CLASSPATH=%CLASSPATH%;%%f
for %%f in ("%WILDFLY_HOME%\modules\jakarta\persistence\api\main\*.jar") do set CLASSPATH=%CLASSPATH%;%%f
for %%f in ("%WILDFLY_HOME%\modules\jakarta\transaction\api\main\*.jar") do set CLASSPATH=%CLASSPATH%;%%f

REM Add PostgreSQL driver
for %%f in ("%WILDFLY_HOME%\modules\org\postgresql\main\*.jar") do set CLASSPATH=%CLASSPATH%;%%f

echo Compiling test classes...
javac -encoding UTF-8 -cp "%CLASSPATH%" -d build\test-classes ejb-module\src\test\EmployeeServiceTest.java 2>test-compile-errors.txt

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Compilation failed! Check test-compile-errors.txt
    type test-compile-errors.txt
    pause
    exit /b 1
)

echo.
echo ✅ Compilation successful!
echo.
echo Running tests...
echo.

REM Run tests
java -cp "%CLASSPATH%;build\test-classes;build\ejb-classes" test.EmployeeServiceTest

echo.
echo ========================================
echo Tests completed!
echo ========================================
pause

