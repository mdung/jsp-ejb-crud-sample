package ejb;

import model.Employee;
import dao.EmployeeDAO;
import java.util.List;
import jakarta.ejb.Stateless;

/**
 * Stateless Session Bean Implementation
 * EJB 3.x style (compatible with Java 6)
 */
@Stateless
public class EmployeeServiceBean implements EmployeeService {
    
    private EmployeeDAO employeeDAO;
    
    public EmployeeServiceBean() {
        this.employeeDAO = new EmployeeDAO();
    }
    
    @Override
    public Long createEmployee(Employee employee) throws Exception {
        // Validation
        validateEmployee(employee);
        
        // Check email uniqueness
        if (employeeDAO.emailExists(employee.getEmail(), null)) {
            throw new Exception("Email already exists: " + employee.getEmail());
        }
        
        return employeeDAO.create(employee);
    }
    
    @Override
    public Employee getEmployeeById(Long id) throws Exception {
        if (id == null) {
            throw new Exception("Employee ID cannot be null");
        }
        return employeeDAO.findById(id);
    }
    
    @Override
    public List<Employee> getAllEmployees() throws Exception {
        return employeeDAO.findAll();
    }
    
    @Override
    public void updateEmployee(Employee employee) throws Exception {
        // Validation
        validateEmployee(employee);
        
        if (employee.getId() == null) {
            throw new Exception("Employee ID cannot be null for update");
        }
        
        // Check email uniqueness (excluding current employee)
        if (employeeDAO.emailExists(employee.getEmail(), employee.getId())) {
            throw new Exception("Email already exists: " + employee.getEmail());
        }
        
        employeeDAO.update(employee);
    }
    
    @Override
    public void deleteEmployee(Long id) throws Exception {
        if (id == null) {
            throw new Exception("Employee ID cannot be null");
        }
        
        // Check if employee exists
        Employee employee = employeeDAO.findById(id);
        if (employee == null) {
            throw new Exception("Employee not found with ID: " + id);
        }
        
        employeeDAO.delete(id);
    }
    
    @Override
    public boolean isEmailUnique(String email, Long excludeId) throws Exception {
        return !employeeDAO.emailExists(email, excludeId);
    }
    
    /**
     * Validate employee data
     */
    private void validateEmployee(Employee employee) throws Exception {
        if (employee == null) {
            throw new Exception("Employee cannot be null");
        }
        
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new Exception("Employee name is required");
        }
        
        if (employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            throw new Exception("Employee email is required");
        }
        
        // Basic email validation
        if (!employee.getEmail().contains("@")) {
            throw new Exception("Invalid email format");
        }
        
        if (employee.getDepartment() == null || employee.getDepartment().trim().isEmpty()) {
            throw new Exception("Employee department is required");
        }
    }
}

