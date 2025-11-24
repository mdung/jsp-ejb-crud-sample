package ejb;

import model.Employee;
import model.EmployeePerformance;
import dao.EmployeeDAO;
import dao.EmployeePerformanceDAO;
import java.util.List;
import java.math.BigDecimal;
import jakarta.ejb.Stateless;

/**
 * Stateless Session Bean Implementation
 * EJB 3.x style (compatible with Java 6)
 */
@Stateless
public class EmployeeServiceBean implements EmployeeService {
    
    private EmployeeDAO employeeDAO;
    private EmployeePerformanceDAO performanceDAO;
    
    public EmployeeServiceBean() {
        this.employeeDAO = new EmployeeDAO();
        this.performanceDAO = new EmployeePerformanceDAO();
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
    
    // ========== Performance Methods ==========
    
    @Override
    public Long saveEmployeePerformance(Long employeeId, String month, 
                                       BigDecimal performanceScore, 
                                       String rating, String notes) throws Exception {
        // Validate employee exists
        Employee employee = employeeDAO.findById(employeeId);
        if (employee == null) {
            throw new Exception("Employee not found with ID: " + employeeId);
        }
        
        // Validate performance data
        validatePerformance(performanceScore, rating, month);
        
        // Check if performance for this month already exists
        EmployeePerformance existing = performanceDAO.findByEmployeeIdAndMonth(employeeId, month);
        
        if (existing != null) {
            // Update existing
            existing.setPerformanceScore(performanceScore);
            existing.setRating(rating);
            existing.setNotes(notes);
            performanceDAO.update(existing);
            return existing.getId();
        } else {
            // Create new
            EmployeePerformance performance = new EmployeePerformance(
                employeeId, month, performanceScore, rating, notes);
            return performanceDAO.create(performance);
        }
    }
    
    @Override
    public EmployeePerformance getEmployeePerformance(Long employeeId, String month) throws Exception {
        if (employeeId == null) {
            throw new Exception("Employee ID cannot be null");
        }
        if (month == null || month.trim().isEmpty()) {
            throw new Exception("Month cannot be null or empty");
        }
        return performanceDAO.findByEmployeeIdAndMonth(employeeId, month);
    }
    
    @Override
    public List<EmployeePerformance> getEmployeePerformanceHistory(Long employeeId) throws Exception {
        if (employeeId == null) {
            throw new Exception("Employee ID cannot be null");
        }
        return performanceDAO.findByEmployeeId(employeeId);
    }
    
    @Override
    public EmployeePerformance getLatestEmployeePerformance(Long employeeId) throws Exception {
        if (employeeId == null) {
            throw new Exception("Employee ID cannot be null");
        }
        return performanceDAO.findLatestByEmployeeId(employeeId);
    }
    
    @Override
    public void deleteEmployeePerformance(Long performanceId) throws Exception {
        if (performanceId == null) {
            throw new Exception("Performance ID cannot be null");
        }
        performanceDAO.delete(performanceId);
    }
    
    /**
     * Validate performance data
     */
    private void validatePerformance(BigDecimal performanceScore, String rating, String month) throws Exception {
        if (performanceScore == null) {
            throw new Exception("Performance score is required");
        }
        
        // Score must be between 0 and 100
        if (performanceScore.compareTo(BigDecimal.ZERO) < 0 || 
            performanceScore.compareTo(new BigDecimal("100")) > 0) {
            throw new Exception("Performance score must be between 0 and 100");
        }
        
        if (rating == null || rating.trim().isEmpty()) {
            throw new Exception("Rating is required");
        }
        
        // Validate rating values
        String[] validRatings = {"Excellent", "Good", "Average", "Poor"};
        boolean valid = false;
        for (String validRating : validRatings) {
            if (validRating.equalsIgnoreCase(rating)) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            throw new Exception("Invalid rating. Must be: Excellent, Good, Average, or Poor");
        }
        
        if (month == null || month.trim().isEmpty()) {
            throw new Exception("Month is required");
        }
        
        // Validate month format (YYYY-MM)
        if (!month.matches("\\d{4}-\\d{2}")) {
            throw new Exception("Invalid month format. Must be YYYY-MM (e.g., 2025-11)");
        }
    }
}

