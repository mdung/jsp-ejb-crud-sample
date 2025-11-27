package model;

import java.io.Serializable;
import java.util.Date;

/**
 * Employee Entity Model
 * Compatible with Java 6
 */
public class Employee implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String firstName;
    private String name;
    private String email;
    private String department;
    private Boolean active;
    private Date dateOfBirth;
    
    public Employee() {
        this.active = true; // Default to active
    }
    
    public Employee(Long id, String firstName, String name, String email, String department) {
        this.id = id;
        this.firstName = firstName;
        this.name = name;
        this.email = email;
        this.department = department;
        this.active = true; // Default to active
    }
    
    public Employee(Long id, String firstName, String name, String email, String department, Boolean active) {
        this.id = id;
        this.firstName = firstName;
        this.name = name;
        this.email = email;
        this.department = department;
        this.active = active != null ? active : true;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
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
    
    public Boolean getActive() {
        return active != null ? active : true;
    }
    
    public void setActive(Boolean active) {
        this.active = active != null ? active : true;
    }
    
    public boolean isActive() {
        return getActive();
    }
    
    // Added primitive setter to match JavaBeans convention with isActive()
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", active=" + active +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}

