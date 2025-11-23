package ejb;

import model.Employee;
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
}

