package dao;

import model.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Data Access Object for Employee
 * Uses plain JDBC (Java 6 compatible)
 */
public class EmployeeDAO {

    private static final String JNDI_NAME = "java:jboss/datasources/EmployeeDS";


    private DataSource getDataSource() throws NamingException {
        InitialContext ctx = new InitialContext();
        Object obj = ctx.lookup(JNDI_NAME);
        return (DataSource) obj;
    }
    
    /**
     * Get connection from DataSource
     */
    private Connection getConnection() throws SQLException, NamingException {
        return getDataSource().getConnection();
    }
    
    /**
     * Create new employee
     */
    public Long create(Employee employee) throws SQLException, NamingException {
        String sql = "INSERT INTO employees (firstname, name, email, department, active, date_of_birth) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getName());
            pstmt.setString(3, employee.getEmail());
            pstmt.setString(4, employee.getDepartment());
            pstmt.setBoolean(5, employee.getActive());
            if (employee.getDateOfBirth() != null) {
                pstmt.setDate(6, new java.sql.Date(employee.getDateOfBirth().getTime()));
            } else {
                pstmt.setDate(6, null);
            }
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating employee failed, no rows affected.");
            }
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new SQLException("Creating employee failed, no ID obtained.");
            }
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * Find employee by ID
     */
    public Employee findById(Long id) throws SQLException, NamingException {
        String sql = "SELECT id, firstname, name, email, department, active, date_of_birth FROM employees WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEmployee(rs);
            }
            return null;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * Find all employees
     */
    public List<Employee> findAll() throws SQLException, NamingException {
        String sql = "SELECT id, firstname, name, email, department, active, date_of_birth FROM employees ORDER BY id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Employee> employees = new ArrayList<Employee>();
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
            return employees;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * Search employees by keyword (name, email, or department)
     */
    public List<Employee> search(String keyword) throws SQLException, NamingException {
        String sql = "SELECT id, firstname, name, email, department, active, date_of_birth FROM employees " +
                     "WHERE LOWER(firstname) LIKE ? OR LOWER(name) LIKE ? OR LOWER(email) LIKE ? OR LOWER(department) LIKE ? " +
                     "ORDER BY id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Employee> employees = new ArrayList<Employee>();
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
            return employees;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * Find all employees with sorting
     */
    public List<Employee> findAllSorted(String sortBy, String sortOrder) throws SQLException, NamingException {
        // Validate sortBy to prevent SQL injection
        String validSortBy = "id";
        if ("name".equalsIgnoreCase(sortBy)) {
            validSortBy = "name";
        } else if ("email".equalsIgnoreCase(sortBy)) {
            validSortBy = "email";
        } else if ("department".equalsIgnoreCase(sortBy)) {
            validSortBy = "department";
        }
        
        // Validate sortOrder
        String validSortOrder = "ASC";
        if ("DESC".equalsIgnoreCase(sortOrder)) {
            validSortOrder = "DESC";
        }
        
        String sql = "SELECT id, firstname, name, email, department, active, date_of_birth FROM employees ORDER BY " + validSortBy + " " + validSortOrder;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Employee> employees = new ArrayList<Employee>();
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
            return employees;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * Update employee
     */
    public void update(Employee employee) throws SQLException, NamingException {
        String sql = "UPDATE employees SET firstname = ?, name = ?, email = ?, department = ?, active = ?, date_of_birth = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getName());
            pstmt.setString(3, employee.getEmail());
            pstmt.setString(4, employee.getDepartment());
            pstmt.setBoolean(5, employee.getActive());
            if (employee.getDateOfBirth() != null) {
                pstmt.setDate(6, new java.sql.Date(employee.getDateOfBirth().getTime()));
            } else {
                pstmt.setDate(6, null);
            }
            pstmt.setLong(7, employee.getId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating employee failed, no rows affected.");
            }
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    /**
     * Delete employee by ID
     */
    public void delete(Long id) throws SQLException, NamingException {
        String sql = "DELETE FROM employees WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting employee failed, no rows affected.");
            }
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    /**
     * Check if email exists (for validation)
     */
    public boolean emailExists(String email, Long excludeId) throws SQLException, NamingException {
        String sql = "SELECT COUNT(*) FROM employees WHERE email = ? AND id != ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setLong(2, excludeId != null ? excludeId : -1);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * Map ResultSet to Employee object
     */
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getLong("id"));
        try {
            employee.setFirstName(rs.getString("firstname"));
        } catch (SQLException e) {
            // Column may not exist in older schemas
            employee.setFirstName(null);
        }
        employee.setName(rs.getString("name"));
        employee.setEmail(rs.getString("email"));
        employee.setDepartment(rs.getString("department"));
        // Handle active column - may not exist in old databases
        try {
            employee.setActive(rs.getBoolean("active"));
        } catch (SQLException e) {
            // Column doesn't exist, default to true
            employee.setActive(true);
        }
        // Handle date_of_birth column - may not exist in old databases
        try {
            java.sql.Date sqlDate = rs.getDate("date_of_birth");
            if (sqlDate != null) {
                employee.setDateOfBirth(new java.util.Date(sqlDate.getTime()));
            } else {
                employee.setDateOfBirth(null);
            }
        } catch (SQLException e) {
            // Column doesn't exist, set to null
            employee.setDateOfBirth(null);
        }
        return employee;
    }
    
    /**
     * Activate employee
     */
    public void activate(Long id) throws SQLException, NamingException {
        String sql = "UPDATE employees SET active = true WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Activating employee failed, no rows affected.");
            }
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    /**
     * Deactivate employee
     */
    public void deactivate(Long id) throws SQLException, NamingException {
        String sql = "UPDATE employees SET active = false WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deactivating employee failed, no rows affected.");
            }
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    /**
     * Close database resources
     */
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // Log error
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                // Log error
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // Log error
            }
        }
    }
}

