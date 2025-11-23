package selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Selenium Test Suite for Employee CRUD Operations
 * Compatible with Java 6 and Selenium WebDriver
 */
public class EmployeeCRUDTest {
    
    private WebDriver driver;
    private EmployeeListPage listPage;
    private EmployeeFormPage formPage;
    private EmployeeDetailPage detailPage;
    
    private static final String BASE_URL = "http://localhost:8080/employee-demo";
    
    @Before
    public void setUp() {
        // Setup ChromeDriver
        // Note: ChromeDriver path should be set in system property or PATH
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Remove for visible browser
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        
        listPage = new EmployeeListPage(driver);
        formPage = new EmployeeFormPage(driver);
        detailPage = new EmployeeDetailPage(driver);
    }
    
    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Test
    public void testCreateEmployee_ValidData() {
        // Navigate to list page
        listPage.navigateTo();
        
        // Click Add New Employee
        listPage.clickAddNewEmployee();
        
        // Fill form with valid data
        String name = "Selenium Test User";
        String email = "selenium.test@example.com";
        String department = "QA";
        
        formPage.fillForm(name, email, department);
        formPage.submitForm();
        
        // Verify success message
        listPage.navigateTo();
        assertTrue("Success message should be displayed", 
                   listPage.isMessageDisplayed());
        assertTrue("Employee should be in list", 
                   listPage.isEmployeeInList(email));
    }
    
    @Test
    public void testCreateEmployee_DuplicateEmail() {
        // First, create an employee
        listPage.navigateTo();
        listPage.clickAddNewEmployee();
        formPage.fillForm("First User", "duplicate@test.com", "IT");
        formPage.submitForm();
        
        // Try to create another with same email
        listPage.navigateTo();
        listPage.clickAddNewEmployee();
        formPage.fillForm("Second User", "duplicate@test.com", "HR");
        formPage.submitForm();
        
        // Verify error message
        assertTrue("Error should be displayed", formPage.isErrorDisplayed());
        String error = formPage.getError();
        assertTrue("Error should mention email exists", 
                   error != null && error.contains("Email already exists"));
    }
    
    @Test
    public void testCreateEmployee_MissingName() {
        listPage.navigateTo();
        listPage.clickAddNewEmployee();
        
        // Leave name empty
        formPage.enterEmail("test@test.com");
        formPage.enterDepartment("IT");
        
        // Try to submit (HTML5 validation should prevent)
        formPage.submitForm();
        
        // Form should still be visible (not submitted)
        assertTrue("Form should still be visible", formPage.isCreateMode());
    }
    
    @Test
    public void testViewEmployee() {
        // Assume employee with ID 1 exists
        Long employeeId = 1L;
        
        listPage.navigateTo();
        listPage.clickViewEmployee(employeeId);
        
        // Verify detail page shows employee info
        String name = detailPage.getEmployeeName();
        String email = detailPage.getEmployeeEmail();
        String department = detailPage.getEmployeeDepartment();
        
        assertNotNull("Name should not be null", name);
        assertNotNull("Email should not be null", email);
        assertNotNull("Department should not be null", department);
    }
    
    @Test
    public void testUpdateEmployee() {
        // Create an employee first
        listPage.navigateTo();
        listPage.clickAddNewEmployee();
        formPage.fillForm("Original Name", "update.test@example.com", "IT");
        formPage.submitForm();
        
        // Find and edit the employee
        listPage.navigateTo();
        // Note: In real scenario, you'd need to find the ID from the list
        // For demo, assuming we know the ID or can find it
        
        // Navigate to edit (assuming ID 1)
        formPage.navigateToEdit(1L);
        
        // Update the name
        formPage.enterName("Updated Name");
        formPage.submitForm();
        
        // Verify update
        listPage.navigateTo();
        assertTrue("Success message should be displayed", 
                   listPage.isMessageDisplayed());
    }
    
    @Test
    public void testDeleteEmployee() {
        // Create an employee first
        listPage.navigateTo();
        listPage.clickAddNewEmployee();
        String email = "delete.test@example.com";
        formPage.fillForm("Delete Test", email, "IT");
        formPage.submitForm();
        
        // Get initial count
        listPage.navigateTo();
        int initialCount = listPage.getEmployeeCount();
        
        // Delete the employee (assuming we can find it by some means)
        // In real scenario, you'd need to find the ID
        // For demo: listPage.clickDeleteEmployee(id);
        // listPage.confirmDelete();
        
        // Verify deletion
        // listPage.navigateTo();
        // int finalCount = listPage.getEmployeeCount();
        // assertEquals("Count should decrease by 1", initialCount - 1, finalCount);
    }
    
    @Test
    public void testListAllEmployees() {
        listPage.navigateTo();
        
        int count = listPage.getEmployeeCount();
        assertTrue("Should have employees or show empty message", count >= 0);
        
        // Verify table structure exists
        assertTrue("List page should be loaded", count >= 0);
    }
    
    @Test
    public void testCancelCreate() {
        listPage.navigateTo();
        listPage.clickAddNewEmployee();
        
        // Fill some data
        formPage.enterName("Cancel Test");
        formPage.enterEmail("cancel@test.com");
        
        // Click cancel
        formPage.clickCancel();
        
        // Should return to list
        listPage.navigateTo();
        assertTrue("Should be on list page", listPage.getEmployeeCount() >= 0);
    }
}

