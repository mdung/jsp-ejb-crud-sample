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
        String sql = "INSERT INTO employees (name, email, department) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getEmail());
            pstmt.setString(3, employee.getDepartment());
            
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
        String sql = "SELECT id, name, email, department FROM employees WHERE id = ?";
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
        String sql = "SELECT id, name, email, department FROM employees ORDER BY id";
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
        String sql = "UPDATE employees SET name = ?, email = ?, department = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getEmail());
            pstmt.setString(3, employee.getDepartment());
            pstmt.setLong(4, employee.getId());
            
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
        employee.setName(rs.getString("name"));
        employee.setEmail(rs.getString("email"));
        employee.setDepartment(rs.getString("department"));
        return employee;
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

