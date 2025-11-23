package selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object for Employee Detail Page
 * Compatible with Selenium WebDriver
 */
public class EmployeeDetailPage {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Locators - using XPath to find labels and values
    private By editButton = By.xpath("//a[contains(@href, 'action=edit')]");
    private By backButton = By.xpath("//a[contains(@href, 'action=list')]");
    
    public EmployeeDetailPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
    }
    
    public void navigateTo(Long id) {
        driver.get("http://localhost:8080/employee-demo/employee?action=view&id=" + id);
        wait.until(ExpectedConditions.presenceOfElementLocated(editButton));
    }
    
    public String getEmployeeName() {
        // Find the detail value after "Name" label
        WebElement nameLabel = driver.findElement(By.xpath("//div[contains(text(), 'Name')]/following-sibling::div[1]"));
        return nameLabel.getText();
    }
    
    public String getEmployeeEmail() {
        WebElement emailLabel = driver.findElement(By.xpath("//div[contains(text(), 'Email')]/following-sibling::div[1]"));
        return emailLabel.getText();
    }
    
    public String getEmployeeDepartment() {
        WebElement deptLabel = driver.findElement(By.xpath("//div[contains(text(), 'Department')]/following-sibling::div[1]"));
        return deptLabel.getText();
    }
    
    public String getEmployeeId() {
        WebElement idLabel = driver.findElement(By.xpath("//div[contains(text(), 'ID')]/following-sibling::div[1]"));
        return idLabel.getText();
    }
    
    public void clickEdit() {
        wait.until(ExpectedConditions.elementToBeClickable(editButton));
        driver.findElement(editButton).click();
    }
    
    public void clickBack() {
        wait.until(ExpectedConditions.elementToBeClickable(backButton));
        driver.findElement(backButton).click();
    }
}

