package test;

import ejb.EmployeeService;
import ejb.EmployeeServiceBean;
import model.Employee;
import model.EmployeePerformance;
import java.math.BigDecimal;
import java.util.List;

/**
 * Unit Tests for EmployeeService
 * Note: These tests require database connection and WildFly environment
 * Run with: java -cp "..." test.EmployeeServiceTest
 */
public class EmployeeServiceTest {
    
    private EmployeeService service;
    private static final String TEST_EMAIL = "test.unit@example.com";
    private static final String TEST_EMAIL_2 = "test.unit2@example.com";
    
    public EmployeeServiceTest() {
        this.service = new EmployeeServiceBean();
    }
    
    /**
     * Test create employee
     */
    public void testCreateEmployee() throws Exception {
        System.out.println("\n=== Test: Create Employee ===");
        
        Employee emp = new Employee();
        emp.setName("Test Employee");
        emp.setEmail(TEST_EMAIL);
        emp.setDepartment("Testing");
        
        try {
            Long id = service.createEmployee(emp);
            System.out.println("PASS: Employee created with ID: " + id);
            assert id != null : "Employee ID should not be null";
            assert id > 0 : "Employee ID should be positive";
            
            // Cleanup
            service.deleteEmployee(id);
            System.out.println("Cleanup: Employee deleted");
        } catch (Exception e) {
            System.out.println("FAIL: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Test get employee by ID
     */
    public void testGetEmployeeById() throws Exception {
        System.out.println("\n=== Test: Get Employee By ID ===");
        
        // Create test employee
        Employee emp = new Employee();
        emp.setName("Test Get Employee");
        emp.setEmail(TEST_EMAIL);
        emp.setDepartment("Testing");
        Long id = service.createEmployee(emp);
        
        try {
            Employee found = service.getEmployeeById(id);
            System.out.println("PASS: Employee found: " + found.getName());
            assert found != null : "Employee should not be null";
            assert found.getId().equals(id) : "Employee ID should match";
            assert found.getEmail().equals(TEST_EMAIL) : "Email should match";
            
            // Cleanup
            service.deleteEmployee(id);
            System.out.println("Cleanup: Employee deleted");
        } catch (Exception e) {
            System.out.println("FAIL: " + e.getMessage());
            // Cleanup on error
            try { service.deleteEmployee(id); } catch (Exception ex) {}
            throw e;
        }
    }
    
    /**
     * Test get all employees
     */
    public void testGetAllEmployees() throws Exception {
        System.out.println("\n=== Test: Get All Employees ===");
        
        try {
            List<Employee> employees = service.getAllEmployees();
            System.out.println("PASS: Found " + employees.size() + " employees");
            assert employees != null : "Employees list should not be null";
        } catch (Exception e) {
            System.out.println("FAIL: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Test update employee
     */
    public void testUpdateEmployee() throws Exception {
        System.out.println("\n=== Test: Update Employee ===");
        
        // Create test employee
        Employee emp = new Employee();
        emp.setName("Test Update");
        emp.setEmail(TEST_EMAIL);
        emp.setDepartment("Testing");
        Long id = service.createEmployee(emp);
        
        try {
            // Update employee
            emp.setId(id);
            emp.setName("Updated Name");
            emp.setDepartment("Updated Department");
            service.updateEmployee(emp);
            
            // Verify update
            Employee updated = service.getEmployeeById(id);
            System.out.println("PASS: Employee updated");
            assert updated.getName().equals("Updated Name") : "Name should be updated";
            assert updated.getDepartment().equals("Updated Department") : "Department should be updated";
            
            // Cleanup
            service.deleteEmployee(id);
            System.out.println("Cleanup: Employee deleted");
        } catch (Exception e) {
            System.out.println("FAIL: " + e.getMessage());
            // Cleanup on error
            try { service.deleteEmployee(id); } catch (Exception ex) {}
            throw e;
        }
    }
    
    /**
     * Test delete employee
     */
    public void testDeleteEmployee() throws Exception {
        System.out.println("\n=== Test: Delete Employee ===");
        
        // Create test employee
        Employee emp = new Employee();
        emp.setName("Test Delete");
        emp.setEmail(TEST_EMAIL);
        emp.setDepartment("Testing");
        Long id = service.createEmployee(emp);
        
        try {
            service.deleteEmployee(id);
            System.out.println("PASS: Employee deleted");
            
            // Verify deletion
            Employee deleted = service.getEmployeeById(id);
            assert deleted == null : "Employee should be deleted";
        } catch (Exception e) {
            // Expected exception if employee not found
            if (e.getMessage().contains("not found")) {
                System.out.println("✅ PASS: Employee deleted (not found as expected)");
            } else {
                System.out.println("FAIL: " + e.getMessage());
                throw e;
            }
        }
    }
    
    /**
     * Test email uniqueness validation
     */
    public void testEmailUniqueness() throws Exception {
        System.out.println("\n=== Test: Email Uniqueness ===");
        
        // Create first employee
        Employee emp1 = new Employee();
        emp1.setName("Test Email 1");
        emp1.setEmail(TEST_EMAIL);
        emp1.setDepartment("Testing");
        Long id1 = service.createEmployee(emp1);
        
        try {
            // Try to create second employee with same email
            Employee emp2 = new Employee();
            emp2.setName("Test Email 2");
            emp2.setEmail(TEST_EMAIL);
            emp2.setDepartment("Testing");
            
            try {
                service.createEmployee(emp2);
                System.out.println("FAIL: Should not allow duplicate email");
                assert false : "Should throw exception for duplicate email";
            } catch (Exception e) {
                if (e.getMessage().contains("already exists")) {
                    System.out.println("PASS: Duplicate email rejected");
                } else {
                    throw e;
                }
            }
            
            // Cleanup
            service.deleteEmployee(id1);
            System.out.println("Cleanup: Employee deleted");
        } catch (Exception e) {
            System.out.println("FAIL: " + e.getMessage());
            // Cleanup on error
            try { service.deleteEmployee(id1); } catch (Exception ex) {}
            throw e;
        }
    }
    
    /**
     * Test save employee performance
     */
    public void testSaveEmployeePerformance() throws Exception {
        System.out.println("\n=== Test: Save Employee Performance ===");
        
        // Create test employee
        Employee emp = new Employee();
        emp.setName("Test Performance");
        emp.setEmail(TEST_EMAIL);
        emp.setDepartment("Testing");
        Long empId = service.createEmployee(emp);
        
        try {
            // Save performance
            service.saveEmployeePerformance(empId, "2025-11", 
                new BigDecimal("85.5"), "Good", "Test performance notes");
            
            // Get performance
            EmployeePerformance perf = service.getEmployeePerformance(empId, "2025-11");
            System.out.println("PASS: Performance saved");
            assert perf != null : "Performance should not be null";
            assert perf.getPerformanceScore().compareTo(new BigDecimal("85.5")) == 0 : "Score should match";
            assert perf.getRating().equals("Good") : "Rating should match";
            
            // Cleanup
            service.deleteEmployee(empId);
            System.out.println("Cleanup: Employee deleted");
        } catch (Exception e) {
            System.out.println("FAIL: " + e.getMessage());
            // Cleanup on error
            try { service.deleteEmployee(empId); } catch (Exception ex) {}
            throw e;
        }
    }
    
    /**
     * Test get performance history
     */
    public void testGetPerformanceHistory() throws Exception {
        System.out.println("\n=== Test: Get Performance History ===");
        
        // Create test employee
        Employee emp = new Employee();
        emp.setName("Test History");
        emp.setEmail(TEST_EMAIL);
        emp.setDepartment("Testing");
        Long empId = service.createEmployee(emp);
        
        try {
            // Save multiple performances
            service.saveEmployeePerformance(empId, "2025-10", 
                new BigDecimal("80"), "Good", "October");
            service.saveEmployeePerformance(empId, "2025-11", 
                new BigDecimal("85"), "Excellent", "November");
            
            // Get history
            List<EmployeePerformance> history = service.getEmployeePerformanceHistory(empId);
            System.out.println("PASS: Found " + history.size() + " performance records");
            assert history != null : "History should not be null";
            assert history.size() >= 2 : "Should have at least 2 records";
            
            // Cleanup
            service.deleteEmployee(empId);
            System.out.println("Cleanup: Employee deleted");
        } catch (Exception e) {
            System.out.println("FAIL: " + e.getMessage());
            // Cleanup on error
            try { service.deleteEmployee(empId); } catch (Exception ex) {}
            throw e;
        }
    }
    
    /**
     * Run all tests
     */
    public static void main(String[] args) {
        EmployeeServiceTest test = new EmployeeServiceTest();
        int passed = 0;
        int failed = 0;
        
        System.out.println("========================================");
        System.out.println("EmployeeService Unit Tests");
        System.out.println("========================================");
        
        // Test 1: Create
        try {
            test.testCreateEmployee();
            passed++;
        } catch (Exception e) {
            failed++;
        }
        
        // Test 2: Get By ID
        try {
            test.testGetEmployeeById();
            passed++;
        } catch (Exception e) {
            failed++;
        }
        
        // Test 3: Get All
        try {
            test.testGetAllEmployees();
            passed++;
        } catch (Exception e) {
            failed++;
        }
        
        // Test 4: Update
        try {
            test.testUpdateEmployee();
            passed++;
        } catch (Exception e) {
            failed++;
        }
        
        // Test 5: Delete
        try {
            test.testDeleteEmployee();
            passed++;
        } catch (Exception e) {
            failed++;
        }
        
        // Test 6: Email Uniqueness
        try {
            test.testEmailUniqueness();
            passed++;
        } catch (Exception e) {
            failed++;
        }
        
        // Test 7: Save Performance
        try {
            test.testSaveEmployeePerformance();
            passed++;
        } catch (Exception e) {
            failed++;
        }
        
        // Test 8: Get Performance History
        try {
            test.testGetPerformanceHistory();
            passed++;
        } catch (Exception e) {
            failed++;
        }
        
        // Summary
        System.out.println("\n========================================");
        System.out.println("Test Summary");
        System.out.println("========================================");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("📊 Total: " + (passed + failed));
        System.out.println("========================================");
        
        if (failed == 0) {
            System.out.println("All tests passed!");
        } else {
            System.out.println("Some tests failed!");
        }
    }
}

