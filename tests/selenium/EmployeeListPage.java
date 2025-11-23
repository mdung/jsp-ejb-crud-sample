package selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

/**
 * Page Object for Employee List Page
 * Compatible with Selenium WebDriver
 */
public class EmployeeListPage {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Locators
    private By addNewEmployeeButton = By.xpath("//a[contains(@href, 'action=new')]");
    private By employeeTable = By.xpath("//table");
    private By employeeRows = By.xpath("//table/tbody/tr");
    private By messageDiv = By.xpath("//div[contains(@class, 'message')]");
    private By errorDiv = By.xpath("//div[contains(@class, 'error')]");
    
    public EmployeeListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
    }
    
    public void navigateTo() {
        driver.get("http://localhost:8080/employee-demo/employee?action=list");
        wait.until(ExpectedConditions.presenceOfElementLocated(employeeTable));
    }
    
    public void clickAddNewEmployee() {
        wait.until(ExpectedConditions.elementToBeClickable(addNewEmployeeButton));
        driver.findElement(addNewEmployeeButton).click();
    }
    
    public void clickViewEmployee(Long id) {
        By viewButton = By.xpath("//a[contains(@href, 'action=view&id=" + id + "')]");
        wait.until(ExpectedConditions.elementToBeClickable(viewButton));
        driver.findElement(viewButton).click();
    }
    
    public void clickEditEmployee(Long id) {
        By editButton = By.xpath("//a[contains(@href, 'action=edit&id=" + id + "')]");
        wait.until(ExpectedConditions.elementToBeClickable(editButton));
        driver.findElement(editButton).click();
    }
    
    public void clickDeleteEmployee(Long id) {
        By deleteButton = By.xpath("//a[contains(@href, 'action=delete&id=" + id + "')]");
        wait.until(ExpectedConditions.elementToBeClickable(deleteButton));
        driver.findElement(deleteButton).click();
    }
    
    public void confirmDelete() {
        driver.switchTo().alert().accept();
    }
    
    public void cancelDelete() {
        driver.switchTo().alert().dismiss();
    }
    
    public boolean isEmployeeInList(String email) {
        List<WebElement> rows = driver.findElements(employeeRows);
        for (WebElement row : rows) {
            if (row.getText().contains(email)) {
                return true;
            }
        }
        return false;
    }
    
    public int getEmployeeCount() {
        List<WebElement> rows = driver.findElements(employeeRows);
        // Exclude the "No employees found" row
        if (rows.size() == 1 && rows.get(0).getText().contains("No employees found")) {
            return 0;
        }
        return rows.size();
    }
    
    public String getMessage() {
        try {
            WebElement messageElement = driver.findElement(messageDiv);
            return messageElement.getText();
        } catch (Exception e) {
            return null;
        }
    }
    
    public String getError() {
        try {
            WebElement errorElement = driver.findElement(errorDiv);
            return errorElement.getText();
        } catch (Exception e) {
            return null;
        }
    }
    
    public boolean isMessageDisplayed() {
        try {
            return driver.findElement(messageDiv).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isErrorDisplayed() {
        try {
            return driver.findElement(errorDiv).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}

