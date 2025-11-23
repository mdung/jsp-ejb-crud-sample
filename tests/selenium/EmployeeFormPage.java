package selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object for Employee Form Page (Create/Edit)
 * Compatible with Selenium WebDriver
 */
public class EmployeeFormPage {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Locators
    private By nameField = By.id("name");
    private By emailField = By.id("email");
    private By departmentField = By.id("department");
    private By submitButton = By.xpath("//button[@type='submit']");
    private By cancelButton = By.xpath("//a[contains(@href, 'action=list')]");
    private By errorDiv = By.xpath("//div[contains(@class, 'error')]");
    private By formTitle = By.xpath("//h1");
    
    public EmployeeFormPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
    }
    
    public void navigateToNew() {
        driver.get("http://localhost:8080/employee-demo/employee?action=new");
        wait.until(ExpectedConditions.presenceOfElementLocated(nameField));
    }
    
    public void navigateToEdit(Long id) {
        driver.get("http://localhost:8080/employee-demo/employee?action=edit&id=" + id);
        wait.until(ExpectedConditions.presenceOfElementLocated(nameField));
    }
    
    public void enterName(String name) {
        WebElement field = wait.until(ExpectedConditions.presenceOfElementLocated(nameField));
        field.clear();
        field.sendKeys(name);
    }
    
    public void enterEmail(String email) {
        WebElement field = wait.until(ExpectedConditions.presenceOfElementLocated(emailField));
        field.clear();
        field.sendKeys(email);
    }
    
    public void enterDepartment(String department) {
        WebElement field = wait.until(ExpectedConditions.presenceOfElementLocated(departmentField));
        field.clear();
        field.sendKeys(department);
    }
    
    public void fillForm(String name, String email, String department) {
        enterName(name);
        enterEmail(email);
        enterDepartment(department);
    }
    
    public void submitForm() {
        wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        driver.findElement(submitButton).click();
    }
    
    public void clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelButton));
        driver.findElement(cancelButton).click();
    }
    
    public String getNameValue() {
        return driver.findElement(nameField).getAttribute("value");
    }
    
    public String getEmailValue() {
        return driver.findElement(emailField).getAttribute("value");
    }
    
    public String getDepartmentValue() {
        return driver.findElement(departmentField).getAttribute("value");
    }
    
    public String getError() {
        try {
            WebElement errorElement = driver.findElement(errorDiv);
            return errorElement.getText();
        } catch (Exception e) {
            return null;
        }
    }
    
    public boolean isErrorDisplayed() {
        try {
            return driver.findElement(errorDiv).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isCreateMode() {
        try {
            String title = driver.findElement(formTitle).getText();
            return title.contains("Add New Employee");
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isEditMode() {
        try {
            String title = driver.findElement(formTitle).getText();
            return title.contains("Edit Employee");
        } catch (Exception e) {
            return false;
        }
    }
}

