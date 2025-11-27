package ejb;

import model.Employee;
import model.EmployeePerformance;
import dao.EmployeeDAO;
import dao.EmployeePerformanceDAO;
import java.util.List;
import java.math.BigDecimal;
import java.util.logging.Logger;
import jakarta.ejb.Stateless;

/**
 * Stateless Session Bean Implementation
 * EJB 3.x style (compatible with Java 6)
 */
@Stateless
public class EmployeeServiceBean implements EmployeeService {
    
    private static final Logger LOGGER = Logger.getLogger(EmployeeServiceBean.class.getName());
    
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
        
        LOGGER.info("Creating new employee: name=" + employee.getName() 
                + ", email=" + employee.getEmail() 
                + ", department=" + employee.getDepartment());
        
        Long id = employeeDAO.create(employee);
        
        LOGGER.info("Employee created successfully with ID=" + id);
        
        return id;
    }
    
    @Override
    public Employee getEmployeeById(Long id) throws Exception {
        if (id == null) {
            throw new Exception("Employee ID cannot be null");
        }
        LOGGER.info("Fetching employee by ID=" + id);
        Employee emp = employeeDAO.findById(id);
        if (emp == null) {
            LOGGER.warning("Employee not found with ID=" + id);
        }
        return emp;
    }
    
    @Override
    public List<Employee> getAllEmployees() throws Exception {
        LOGGER.info("Fetching all employees");
        return employeeDAO.findAll();
    }
    
    @Override
    public List<Employee> searchEmployees(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            LOGGER.info("Search employees with empty keyword -> returning all");
            return getAllEmployees();
        }
        String kw = keyword.trim();
        LOGGER.info("Searching employees with keyword='" + kw + "'");
        return employeeDAO.search(kw);
    }
    
    @Override
    public List<Employee> getAllEmployeesSorted(String sortBy, String sortOrder) throws Exception {
        LOGGER.info("Fetching all employees sorted by sortBy=" + sortBy + ", sortOrder=" + sortOrder);
        return employeeDAO.findAllSorted(sortBy, sortOrder);
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
        
        LOGGER.info("Updating employee ID=" + employee.getId()
                + ", firstName=" + employee.getFirstName()
                + ", name=" + employee.getName()
                + ", email=" + employee.getEmail()
                + ", department=" + employee.getDepartment());
        employeeDAO.update(employee);
        LOGGER.info("Employee updated successfully, ID=" + employee.getId());
    }
    
    @Override
    public void deleteEmployee(Long id) throws Exception {
        if (id == null) {
            throw new Exception("Employee ID cannot be null");
        }
        
        LOGGER.info("Deleting employee ID=" + id);
        
        // Check if employee exists
        Employee employee = employeeDAO.findById(id);
        if (employee == null) {
            throw new Exception("Employee not found with ID: " + id);
        }
        
        // Delete all performance records for this employee first (avoid FK issues)
        List<EmployeePerformance> perfList = performanceDAO.findByEmployeeId(id);
        if (perfList != null && !perfList.isEmpty()) {
            for (EmployeePerformance perf : perfList) {
                performanceDAO.delete(perf.getId());
            }
            LOGGER.info("Deleted " + perfList.size() + " performance record(s) for employee ID=" + id);
        }
        
        employeeDAO.delete(id);
        LOGGER.info("Employee deleted successfully, ID=" + id);
    }
    
    @Override
    public boolean isEmailUnique(String email, Long excludeId) throws Exception {
        return !employeeDAO.emailExists(email, excludeId);
    }
    
    @Override
    public void activateEmployee(Long id) throws Exception {
        if (id == null) {
            throw new Exception("Employee ID cannot be null");
        }
        
        // Check if employee exists
        LOGGER.info("Activating employee ID=" + id);
        Employee employee = employeeDAO.findById(id);
        if (employee == null) {
            throw new Exception("Employee not found with ID: " + id);
        }
        
        employeeDAO.activate(id);
        LOGGER.info("Employee activated successfully, ID=" + id);
    }
    
    @Override
    public void deactivateEmployee(Long id) throws Exception {
        if (id == null) {
            throw new Exception("Employee ID cannot be null");
        }
        
        // Check if employee exists
        LOGGER.info("Deactivating employee ID=" + id);
        Employee employee = employeeDAO.findById(id);
        if (employee == null) {
            throw new Exception("Employee not found with ID: " + id);
        }
        
        employeeDAO.deactivate(id);
        LOGGER.info("Employee deactivated successfully, ID=" + id);
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
        LOGGER.info("Saving performance for employeeId=" + employeeId + ", month=" + month);
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
            LOGGER.info("Updating existing performance record ID=" + existing.getId()
                    + " for employeeId=" + employeeId + ", month=" + month);
            existing.setPerformanceScore(performanceScore);
            existing.setRating(rating);
            existing.setNotes(notes);
            performanceDAO.update(existing);
            return existing.getId();
        } else {
            // Create new
            EmployeePerformance performance = new EmployeePerformance(
                employeeId, month, performanceScore, rating, notes);
            Long perfId = performanceDAO.create(performance);
            LOGGER.info("Created new performance record ID=" + perfId
                    + " for employeeId=" + employeeId + ", month=" + month);
            return perfId;
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
        LOGGER.info("Fetching performance for employeeId=" + employeeId + ", month=" + month);
        return performanceDAO.findByEmployeeIdAndMonth(employeeId, month);
    }
    
    @Override
    public List<EmployeePerformance> getEmployeePerformanceHistory(Long employeeId) throws Exception {
        if (employeeId == null) {
            throw new Exception("Employee ID cannot be null");
        }
        LOGGER.info("Fetching performance history for employeeId=" + employeeId);
        return performanceDAO.findByEmployeeId(employeeId);
    }
    
    @Override
    public EmployeePerformance getLatestEmployeePerformance(Long employeeId) throws Exception {
        if (employeeId == null) {
            throw new Exception("Employee ID cannot be null");
        }
        LOGGER.info("Fetching latest performance for employeeId=" + employeeId);
        return performanceDAO.findLatestByEmployeeId(employeeId);
    }
    
    @Override
    public void deleteEmployeePerformance(Long performanceId) throws Exception {
        if (performanceId == null) {
            throw new Exception("Performance ID cannot be null");
        }
        LOGGER.info("Deleting performance record ID=" + performanceId);
        performanceDAO.delete(performanceId);
        LOGGER.info("Performance record deleted successfully, ID=" + performanceId);
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

