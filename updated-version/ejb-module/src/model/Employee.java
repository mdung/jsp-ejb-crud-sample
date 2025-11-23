package model;

import java.io.Serializable;

/**
 * Employee Entity Model
 * Compatible with Java 6
 * UPDATED: Added phoneNumber field
 */
public class Employee implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String email;
    private String department;
    private String phoneNumber;  // NEW FIELD
    
    public Employee() {
        // Default constructor
    }
    
    public Employee(Long id, String name, String email, String department, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.phoneNumber = phoneNumber;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    // NEW GETTER AND SETTER
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}

