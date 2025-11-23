package selenium;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Test Runner for Employee CRUD Selenium Tests
 * Compatible with Java 6
 */
public class TestRunner {
    
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(EmployeeCRUDTest.class);
        
        System.out.println("=========================================");
        System.out.println("Selenium Test Execution Report");
        System.out.println("=========================================");
        System.out.println("Total Tests: " + result.getRunCount());
        System.out.println("Passed: " + (result.getRunCount() - result.getFailureCount()));
        System.out.println("Failed: " + result.getFailureCount());
        System.out.println("Execution Time: " + result.getRunTime() + " ms");
        System.out.println("=========================================");
        
        if (result.getFailureCount() > 0) {
            System.out.println("\nFailures:");
            for (Failure failure : result.getFailures()) {
                System.out.println("Test: " + failure.getTestHeader());
                System.out.println("Message: " + failure.getMessage());
                System.out.println("Trace: " + failure.getTrace());
                System.out.println("---");
            }
        }
        
        System.exit(result.wasSuccessful() ? 0 : 1);
    }
}

