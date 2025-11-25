package controller;

import ejb.EmployeeService;
import model.Employee;
import model.EmployeePerformance;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;
import java.math.BigDecimal;

/**
 * Employee Managed Bean for JSF
 * Replaces EmployeeServlet
 * Uses CDI @Named but JNDI lookup for EJB (EJB Remote cannot be injected directly)
 */
@Named(value = "employeeBean")
@RequestScoped
public class EmployeeBean {
    
    private static final String JNDI_NAME = "java:global/employee-demo/EmployeeServiceBean!ejb.EmployeeService";
    
    private EmployeeService getEmployeeService() {
        try {
            InitialContext ctx = new InitialContext();
            return (EmployeeService) ctx.lookup(JNDI_NAME);
        } catch (NamingException e) {
            throw new RuntimeException("Failed to lookup EmployeeService: " + e.getMessage(), e);
        }
    }
    
    // Employee properties
    private List<Employee> employees;
    private Employee employee;
    private Long employeeId;
    private String action;
    private String message;
    private String error;
    
    // Search and Sort properties
    private String searchKeyword;
    private String sortBy = "id";
    private String sortOrder = "ASC";
    
    // Performance properties
    private List<EmployeePerformance> performances;
    private EmployeePerformance performance;
    private String month;
    private BigDecimal performanceScore;
    private String rating;
    private String notes;
    
    /**
     * Initialize - load employees list
     */
    public void init() {
        try {
            if (employees == null) {
                loadEmployees();
            }
        } catch (Exception e) {
            addErrorMessage("Error loading employees: " + e.getMessage());
        }
    }
    
    /**
     * Load all employees
     */
    public void loadEmployees() throws Exception {
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            employees = getEmployeeService().searchEmployees(searchKeyword);
        } else {
            employees = getEmployeeService().getAllEmployeesSorted(sortBy, sortOrder);
        }
    }
    
    /**
     * Search employees
     */
    public String searchEmployees() {
        try {
            employees = null; // Force reload
            loadEmployees();
            return null; // Stay on same page
        } catch (Exception e) {
            addErrorMessage("Error searching employees: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Clear search
     */
    public String clearSearch() {
        searchKeyword = null;
        employees = null; // Force reload
        try {
            loadEmployees();
        } catch (Exception e) {
            addErrorMessage("Error loading employees: " + e.getMessage());
        }
        return null; // Stay on same page
    }
    
    /**
     * Sort employees
     */
    public String sortEmployees(String column) {
        try {
            // Get column from request if not provided
            if (column == null) {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                String columnParam = facesContext.getExternalContext().getRequestParameterMap().get("column");
                if (columnParam != null) {
                    column = columnParam;
                }
            }
            
            if (column == null) {
                return null;
            }
            
            // Toggle sort order if same column
            if (sortBy != null && sortBy.equalsIgnoreCase(column)) {
                sortOrder = "ASC".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";
            } else {
                sortBy = column;
                sortOrder = "ASC";
            }
            employees = null; // Force reload
            loadEmployees();
            return null; // Stay on same page
        } catch (Exception e) {
            addErrorMessage("Error sorting employees: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Sort employees (without parameter - gets from request)
     */
    public String sortEmployees() {
        return sortEmployees(null);
    }
    
    /**
     * Get employee ID from request parameter (for action methods without parameters)
     */
    private Long getEmployeeIdFromRequest() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String idParam = facesContext.getExternalContext().getRequestParameterMap().get("id");
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                return Long.parseLong(idParam);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return employeeId; // Fallback to bean property
    }
    
    /**
     * Navigate to employee list
     */
    public String listEmployees() {
        try {
            loadEmployees();
            return "employee-list?faces-redirect=true";
        } catch (Exception e) {
            addErrorMessage("Error loading employees: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Show new employee form
     */
    public String showNewForm() {
        try {
            employee = new Employee();
            action = "create";
            employeeId = null;
            return "employee-form";
        } catch (Exception e) {
            addErrorMessage("Error initializing form: " + e.getMessage());
            return "employee-list?faces-redirect=true";
        }
    }
    
    /**
     * Show edit form (with parameter)
     */
    public String showEditForm(Long id) {
        try {
            if (id == null) {
                addErrorMessage("Employee ID is required");
                return "employee-list?faces-redirect=true";
            }
            employee = getEmployeeService().getEmployeeById(id);
            if (employee == null) {
                addErrorMessage("Employee not found with ID: " + id);
                return "employee-list?faces-redirect=true";
            }
            employeeId = id;
            action = "update";
            return "employee-form";
        } catch (Exception e) {
            addErrorMessage("Error loading employee: " + e.getMessage());
            return "employee-list?faces-redirect=true";
        }
    }
    
    /**
     * Show edit form (without parameter - gets ID from request)
     */
    public String showEditForm() {
        Long id = getEmployeeIdFromRequest();
        if (id == null) {
            addErrorMessage("Employee ID is required");
            return "employee-list?faces-redirect=true";
        }
        return showEditForm(id);
    }
    
    /**
     * View employee details (with parameter)
     */
    public String viewEmployee(Long id) {
        try {
            if (id == null) {
                addErrorMessage("Employee ID is required");
                return "employee-list?faces-redirect=true";
            }
            employeeId = id; // Set employeeId first
            employee = getEmployeeService().getEmployeeById(id);
            if (employee == null) {
                addErrorMessage("Employee not found with ID: " + id);
                return "employee-list?faces-redirect=true";
            }
            // Ensure employeeId matches employee.id
            if (employee.getId() != null) {
                employeeId = employee.getId();
            }
            return "employee-detail";
        } catch (Exception e) {
            addErrorMessage("Error loading employee: " + e.getMessage());
            return "employee-list?faces-redirect=true";
        }
    }
    
    /**
     * View employee details (without parameter - gets ID from request)
     */
    public String viewEmployee() {
        Long id = getEmployeeIdFromRequest();
        if (id == null) {
            addErrorMessage("Employee ID is required");
            return "employee-list?faces-redirect=true";
        }
        return viewEmployee(id);
    }
    
    /**
     * Submit form - handles both create and update
     */
    public String submitForm() {
        try {
            // Ensure employee is not null
            if (employee == null) {
                addErrorMessage("Employee data is missing. Please try again.");
                return null;
            }
            
            // For update, ensure ID is set
            if ("update".equals(action)) {
                // If employee.id is null, try to get from employeeId
                if (employee.getId() == null && employeeId != null) {
                    employee.setId(employeeId);
                }
                return updateEmployee();
            } else {
                // For create, ensure ID is null
                employee.setId(null);
                return createEmployee();
            }
        } catch (Exception e) {
            addErrorMessage("Error submitting form: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Create new employee
     */
    public String createEmployee() {
        try {
            Long id = getEmployeeService().createEmployee(employee);
            addSuccessMessage("Employee created successfully with ID: " + id);
            return "employee-list?faces-redirect=true";
        } catch (Exception e) {
            addErrorMessage("Error creating employee: " + e.getMessage());
            return null; // Stay on form
        }
    }
    
    /**
     * Update employee
     */
    public String updateEmployee() {
        try {
            if (employee == null) {
                addErrorMessage("Employee data is missing");
                return null;
            }
            if (employee.getId() == null) {
                addErrorMessage("Employee ID is required for update");
                return null;
            }
            getEmployeeService().updateEmployee(employee);
            addSuccessMessage("Employee updated successfully");
            // Clear employee to force reload
            employee = null;
            employees = null; // Clear list to force reload
            return "employee-list?faces-redirect=true";
        } catch (Exception e) {
            addErrorMessage("Error updating employee: " + e.getMessage());
            return null; // Stay on form
        }
    }
    
    /**
     * Delete employee (with parameter)
     */
    public String deleteEmployee(Long id) {
        try {
            getEmployeeService().deleteEmployee(id);
            addSuccessMessage("Employee deleted successfully");
            return "employee-list?faces-redirect=true";
        } catch (Exception e) {
            addErrorMessage("Error deleting employee: " + e.getMessage());
            return "employee-list?faces-redirect=true";
        }
    }
    
    /**
     * Delete employee (without parameter - gets ID from request)
     */
    public String deleteEmployee() {
        Long id = getEmployeeIdFromRequest();
        if (id == null) {
            addErrorMessage("Employee ID is required");
            return "employee-list?faces-redirect=true";
        }
        return deleteEmployee(id);
    }
    
    /**
     * Show performance form (with parameter)
     */
    public String showPerformanceForm(Long id) {
        try {
            employeeId = id; // Set employeeId first
            employee = getEmployeeService().getEmployeeById(id);
            if (employee == null) {
                addErrorMessage("Employee not found with ID: " + id);
                return "employee-list?faces-redirect=true";
            }
            
            // Reset form fields
            month = null;
            performanceScore = null;
            rating = null;
            notes = null;
            performance = null;
            
            return "employee-performance-form";
        } catch (Exception e) {
            addErrorMessage("Error loading performance form: " + e.getMessage());
            return "employee-list?faces-redirect=true";
        }
    }
    
    /**
     * Show performance form (without parameter - gets ID from request)
     */
    public String showPerformanceForm() {
        Long id = getEmployeeIdFromRequest();
        if (id == null) {
            addErrorMessage("Employee ID is required");
            return "employee-list?faces-redirect=true";
        }
        return showPerformanceForm(id);
    }
    
    /**
     * View performance history (with parameter)
     */
    public String viewPerformanceHistory(Long id) {
        try {
            employeeId = id; // Set employeeId first
            employee = getEmployeeService().getEmployeeById(id);
            if (employee == null) {
                addErrorMessage("Employee not found with ID: " + id);
                return "employee-list?faces-redirect=true";
            }
            performances = getEmployeeService().getEmployeePerformanceHistory(id);
            return "employee-performance-history";
        } catch (Exception e) {
            addErrorMessage("Error loading performance history: " + e.getMessage());
            return "employee-list?faces-redirect=true";
        }
    }
    
    /**
     * View performance history (without parameter - gets ID from request)
     */
    public String viewPerformanceHistory() {
        Long id = getEmployeeIdFromRequest();
        if (id == null) {
            addErrorMessage("Employee ID is required");
            return "employee-list?faces-redirect=true";
        }
        return viewPerformanceHistory(id);
    }
    
    /**
     * Save/Update performance
     */
    public String savePerformance() {
        try {
            if (employeeId == null) {
                addErrorMessage("Employee ID is required");
                return null;
            }
            if (month == null || month.trim().isEmpty()) {
                addErrorMessage("Month is required");
                return null;
            }
            if (performanceScore == null) {
                addErrorMessage("Performance score is required");
                return null;
            }
            if (rating == null || rating.trim().isEmpty()) {
                addErrorMessage("Rating is required");
                return null;
            }
            
            getEmployeeService().saveEmployeePerformance(employeeId, month, performanceScore, rating, notes);
            addSuccessMessage("Performance saved successfully");
            // Set employee for detail view
            employee = getEmployeeService().getEmployeeById(employeeId);
            return "employee-detail?faces-redirect=true";
        } catch (Exception e) {
            addErrorMessage("Error saving performance: " + e.getMessage());
            return null; // Stay on form
        }
    }
    
    /**
     * Cancel performance form - return to employee detail
     */
    public String cancelPerformanceForm() {
        if (employeeId != null) {
            try {
                employee = getEmployeeService().getEmployeeById(employeeId);
                return "employee-detail";
            } catch (Exception e) {
                addErrorMessage("Error loading employee: " + e.getMessage());
            }
        }
        return "employee-list?faces-redirect=true";
    }
    
    /**
     * Activate employee
     */
    public String activateEmployee() {
        Long id = getEmployeeIdFromRequest();
        if (id == null) {
            addErrorMessage("Employee ID is required");
            return null;
        }
        try {
            getEmployeeService().activateEmployee(id);
            addSuccessMessage("Employee activated successfully");
            employees = null; // Force reload
            loadEmployees(); // Reload to refresh the list
            return null; // Stay on same page
        } catch (Exception e) {
            addErrorMessage("Error activating employee: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Deactivate employee
     */
    public String deactivateEmployee() {
        Long id = getEmployeeIdFromRequest();
        if (id == null) {
            addErrorMessage("Employee ID is required");
            return null;
        }
        try {
            getEmployeeService().deactivateEmployee(id);
            addSuccessMessage("Employee deactivated successfully");
            employees = null; // Force reload
            loadEmployees(); // Reload to refresh the list
            return null; // Stay on same page
        } catch (Exception e) {
            addErrorMessage("Error deactivating employee: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Toggle employee active status (activate if inactive, deactivate if active)
     */
    public String toggleEmployeeStatus() {
        Long id = getEmployeeIdFromRequest();
        if (id == null) {
            addErrorMessage("Employee ID is required");
            return null;
        }
        try {
            Employee emp = getEmployeeService().getEmployeeById(id);
            if (emp == null) {
                addErrorMessage("Employee not found");
                return null;
            }
            
            if (emp.getActive()) {
                getEmployeeService().deactivateEmployee(id);
                addSuccessMessage("Employee deactivated successfully");
            } else {
                getEmployeeService().activateEmployee(id);
                addSuccessMessage("Employee activated successfully");
            }
            
            employees = null; // Force reload
            loadEmployees(); // Reload to refresh the list
            return null; // Stay on same page
        } catch (Exception e) {
            addErrorMessage("Error toggling employee status: " + e.getMessage());
            return null;
        }
    }
    
    // Helper methods for messages
    private void addSuccessMessage(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", msg));
    }
    
    private void addErrorMessage(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", msg));
    }
    
    // Getters and Setters
    public List<Employee> getEmployees() {
        if (employees == null) {
            try {
                loadEmployees();
            } catch (Exception e) {
                // Log error but don't show multiple error messages
                System.err.println("Error loading employees: " + e.getMessage());
                e.printStackTrace();
                // Only add error message if not already added
                if (employees == null) {
                    employees = new java.util.ArrayList<Employee>(); // Return empty list instead of null
                    addErrorMessage("Error loading employees: " + e.getMessage() + ". Please check database connection.");
                }
            }
        }
        return employees != null ? employees : new java.util.ArrayList<Employee>();
    }
    
    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
    
    public Employee getEmployee() {
        return employee;
    }
    
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    
    public Long getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public List<EmployeePerformance> getPerformances() {
        return performances;
    }
    
    public void setPerformances(List<EmployeePerformance> performances) {
        this.performances = performances;
    }
    
    public EmployeePerformance getPerformance() {
        return performance;
    }
    
    public void setPerformance(EmployeePerformance performance) {
        this.performance = performance;
    }
    
    public String getMonth() {
        return month;
    }
    
    public void setMonth(String month) {
        this.month = month;
    }
    
    public BigDecimal getPerformanceScore() {
        return performanceScore;
    }
    
    public void setPerformanceScore(BigDecimal performanceScore) {
        this.performanceScore = performanceScore;
    }
    
    public String getRating() {
        return rating;
    }
    
    public void setRating(String rating) {
        this.rating = rating;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // Search and Sort getters and setters
    public String getSearchKeyword() {
        return searchKeyword;
    }
    
    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
