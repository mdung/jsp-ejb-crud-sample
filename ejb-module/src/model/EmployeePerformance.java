package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Employee Performance Model
 * Represents monthly performance data for employees
 */
public class EmployeePerformance implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long employeeId;
    private String month;  // Format: '2025-11'
    private BigDecimal performanceScore;  // 0-100
    private String rating;  // 'Excellent', 'Good', 'Average', 'Poor'
    private String notes;
    private Timestamp createdAt;
    
    // Default constructor
    public EmployeePerformance() {
    }
    
    // Constructor with all fields
    public EmployeePerformance(Long id, Long employeeId, String month, 
                              BigDecimal performanceScore, String rating, 
                              String notes, Timestamp createdAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.month = month;
        this.performanceScore = performanceScore;
        this.rating = rating;
        this.notes = notes;
        this.createdAt = createdAt;
    }
    
    // Constructor without id (for creation)
    public EmployeePerformance(Long employeeId, String month, 
                              BigDecimal performanceScore, String rating, String notes) {
        this.employeeId = employeeId;
        this.month = month;
        this.performanceScore = performanceScore;
        this.rating = rating;
        this.notes = notes;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "EmployeePerformance{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", month='" + month + '\'' +
                ", performanceScore=" + performanceScore +
                ", rating='" + rating + '\'' +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

