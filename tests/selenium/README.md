# Selenium Automation Tests

## Prerequisites

1. **Java 6** installed
2. **Selenium WebDriver** (compatible version for Java 6)
3. **JUnit 4** (compatible with Java 6)
4. **ChromeDriver** downloaded and in PATH or specified in system property
5. **Chrome Browser** installed

## Setup

### 1. Download Dependencies

Download the following JAR files and add to classpath:

- `selenium-java-2.x.x.jar` (Selenium 2.x is compatible with Java 6)
- `junit-4.x.jar`
- `hamcrest-core-1.3.jar`
- ChromeDriver executable for your OS

### 2. Configure ChromeDriver

Option 1: Set system property in code:
```java
System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
```

Option 2: Add ChromeDriver to system PATH

### 3. Update Base URL

In `EmployeeCRUDTest.java`, update the `BASE_URL` constant if your application runs on a different port or context.

## Running Tests

### Compile

```bash
javac -cp ".:selenium-java-2.x.x.jar:junit-4.x.jar:hamcrest-core-1.3.jar" tests/selenium/*.java
```

### Run Tests

**Option 1: Using TestRunner**
```bash
java -cp ".:selenium-java-2.x.x.jar:junit-4.x.jar:hamcrest-core-1.3.jar" selenium.TestRunner
```

**Option 2: Using JUnit directly**
```bash
java -cp ".:selenium-java-2.x.x.jar:junit-4.x.jar:hamcrest-core-1.3.jar" org.junit.runner.JUnitCore selenium.EmployeeCRUDTest
```

**Option 3: Using IDE**
- Import project into Eclipse/IntelliJ
- Right-click on `EmployeeCRUDTest.java` → Run As → JUnit Test

## Test Structure

### Page Objects
- `EmployeeListPage.java` - Handles list page interactions
- `EmployeeFormPage.java` - Handles create/edit form interactions
- `EmployeeDetailPage.java` - Handles detail page interactions

### Test Class
- `EmployeeCRUDTest.java` - Contains all test methods

### Test Runner
- `TestRunner.java` - Standalone test execution with reporting

## Test Coverage

The test suite covers:
- ✅ Create employee with valid data
- ✅ Create employee with duplicate email (validation)
- ✅ Create employee with missing fields (validation)
- ✅ View employee details
- ✅ Update employee
- ✅ Delete employee
- ✅ List all employees
- ✅ Cancel operations

## Notes

- Tests assume application is running on `http://localhost:8080/employee-demo`
- Some tests may need adjustment based on actual employee IDs in database
- Headless mode is enabled by default (remove `--headless` flag to see browser)
- Tests use XPath selectors for element location

## Troubleshooting

**ChromeDriver not found:**
- Ensure ChromeDriver is in PATH or system property is set correctly
- Download ChromeDriver matching your Chrome browser version

**Connection refused:**
- Ensure WildFly is running
- Check application is deployed correctly
- Verify URL and port in test code

**Element not found:**
- Check if application UI matches expected structure
- Verify XPath selectors are correct
- Increase wait time if page loads slowly

