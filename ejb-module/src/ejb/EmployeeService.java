package ejb;

import model.Employee;
import model.EmployeePerformance;
import java.util.List;
import jakarta.ejb.Remote;

/**
 * Remote Interface for Employee Service EJB
 * EJB 3.x style (compatible with EJB 2.x deployment)
 */
@Remote
public interface EmployeeService {
    
    /**
     * Create a new employee
     */
    Long createEmployee(Employee employee) throws Exception;
    
    /**
     * Get employee by ID
     */
    Employee getEmployeeById(Long id) throws Exception;
    
    /**
     * Get all employees
     */
    List<Employee> getAllEmployees() throws Exception;
    
    /**
     * Update employee
     */
    void updateEmployee(Employee employee) throws Exception;
    
    /**
     * Delete employee by ID
     */
    void deleteEmployee(Long id) throws Exception;
    
    /**
     * Validate email uniqueness
     */
    boolean isEmailUnique(String email, Long excludeId) throws Exception;
    
    // ========== Performance Methods ==========
    
    /**
     * Create or update employee performance for a month
     */
    Long saveEmployeePerformance(Long employeeId, String month, 
                                 java.math.BigDecimal performanceScore, 
                                 String rating, String notes) throws Exception;
    
    /**
     * Get performance by employee ID and month
     */
    EmployeePerformance getEmployeePerformance(Long employeeId, String month) throws Exception;
    
    /**
     * Get all performance records for an employee
     */
    List<EmployeePerformance> getEmployeePerformanceHistory(Long employeeId) throws Exception;
    
    /**
     * Get latest performance for an employee
     */
    EmployeePerformance getLatestEmployeePerformance(Long employeeId) throws Exception;
    
    /**
     * Delete performance record
     */
    void deleteEmployeePerformance(Long performanceId) throws Exception;
}

