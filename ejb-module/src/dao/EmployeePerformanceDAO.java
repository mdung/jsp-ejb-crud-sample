package dao;

import model.EmployeePerformance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Data Access Object for Employee Performance
 * Uses plain JDBC (Java 6 compatible)
 */
public class EmployeePerformanceDAO {

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
     * Create new performance record
     */
    public Long create(EmployeePerformance performance) throws SQLException, NamingException {
        String sql = "INSERT INTO employee_performance (employee_id, month, performance_score, rating, notes) " +
                     "VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, performance.getEmployeeId());
            pstmt.setString(2, performance.getMonth());
            pstmt.setBigDecimal(3, performance.getPerformanceScore());
            pstmt.setString(4, performance.getRating());
            pstmt.setString(5, performance.getNotes());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating performance record failed, no rows affected.");
            }
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new SQLException("Creating performance record failed, no ID obtained.");
            }
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * Find performance by ID
     */
    public EmployeePerformance findById(Long id) throws SQLException, NamingException {
        String sql = "SELECT id, employee_id, month, performance_score, rating, notes, created_at " +
                     "FROM employee_performance WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPerformance(rs);
            }
            return null;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * Find performance by employee ID and month
     */
    public EmployeePerformance findByEmployeeIdAndMonth(Long employeeId, String month) 
            throws SQLException, NamingException {
        String sql = "SELECT id, employee_id, month, performance_score, rating, notes, created_at " +
                     "FROM employee_performance WHERE employee_id = ? AND month = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, employeeId);
            pstmt.setString(2, month);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPerformance(rs);
            }
            return null;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * Find all performance records for an employee
     */
    public List<EmployeePerformance> findByEmployeeId(Long employeeId) 
            throws SQLException, NamingException {
        String sql = "SELECT id, employee_id, month, performance_score, rating, notes, created_at " +
                     "FROM employee_performance WHERE employee_id = ? ORDER BY month DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<EmployeePerformance> performances = new ArrayList<EmployeePerformance>();
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, employeeId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                performances.add(mapResultSetToPerformance(rs));
            }
            return performances;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * Get latest performance for an employee
     */
    public EmployeePerformance findLatestByEmployeeId(Long employeeId) 
            throws SQLException, NamingException {
        String sql = "SELECT id, employee_id, month, performance_score, rating, notes, created_at " +
                     "FROM employee_performance WHERE employee_id = ? " +
                     "ORDER BY month DESC LIMIT 1";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, employeeId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPerformance(rs);
            }
            return null;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * Update performance record
     */
    public void update(EmployeePerformance performance) throws SQLException, NamingException {
        String sql = "UPDATE employee_performance SET performance_score = ?, rating = ?, notes = ? " +
                     "WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setBigDecimal(1, performance.getPerformanceScore());
            pstmt.setString(2, performance.getRating());
            pstmt.setString(3, performance.getNotes());
            pstmt.setLong(4, performance.getId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating performance record failed, no rows affected.");
            }
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    /**
     * Delete performance record
     */
    public void delete(Long id) throws SQLException, NamingException {
        String sql = "DELETE FROM employee_performance WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting performance record failed, no rows affected.");
            }
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    /**
     * Map ResultSet to EmployeePerformance object
     */
    private EmployeePerformance mapResultSetToPerformance(ResultSet rs) throws SQLException {
        EmployeePerformance performance = new EmployeePerformance();
        performance.setId(rs.getLong("id"));
        performance.setEmployeeId(rs.getLong("employee_id"));
        performance.setMonth(rs.getString("month"));
        performance.setPerformanceScore(rs.getBigDecimal("performance_score"));
        performance.setRating(rs.getString("rating"));
        performance.setNotes(rs.getString("notes"));
        performance.setCreatedAt(rs.getTimestamp("created_at"));
        return performance;
    }
    
    /**
     * Close database resources
     */
    private void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            // Log error if needed
            e.printStackTrace();
        }
    }
}

