package controller;

import ejb.EmployeeService;
import model.Employee;
import model.EmployeePerformance;
import java.io.IOException;
import java.util.List;
import java.math.BigDecimal;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Employee Servlet Controller
 * Handles all CRUD operations
 * Compatible with Java 6
 */
public class EmployeeServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    private static final String JNDI_NAME = "java:global/employee-demo/EmployeeServiceBean!ejb.EmployeeService";
    
    private EmployeeService getEmployeeService() throws NamingException {
        InitialContext ctx = new InitialContext();
        return (EmployeeService) ctx.lookup(JNDI_NAME);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        
        try {
            EmployeeService service = getEmployeeService();
            
            if ("new".equals(action)) {
                showNewForm(request, response);
            } else if ("edit".equals(action)) {
                showEditForm(request, response, service);
            } else if ("delete".equals(action)) {
                deleteEmployee(request, response, service);
            } else if ("view".equals(action)) {
                viewEmployee(request, response, service);
            } else if ("performance".equals(action)) {
                showPerformanceForm(request, response, service);
            } else if ("performanceHistory".equals(action)) {
                viewPerformanceHistory(request, response, service);
            } else {
                listEmployees(request, response, service);
            }
        } catch (Exception e) {
            handleError(request, response, e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        
        try {
            EmployeeService service = getEmployeeService();
            
            if ("create".equals(action)) {
                createEmployee(request, response, service);
            } else if ("update".equals(action)) {
                updateEmployee(request, response, service);
            } else if ("updatePerformance".equals(action)) {
                updatePerformance(request, response, service);
            } else {
                listEmployees(request, response, service);
            }
        } catch (Exception e) {
            handleError(request, response, e);
        }
    }
    
    private void listEmployees(HttpServletRequest request, HttpServletResponse response, EmployeeService service)
            throws Exception, ServletException, IOException {
        List<Employee> employees = service.getAllEmployees();
        request.setAttribute("employees", employees);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/employee-list.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/employee-form.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response, EmployeeService service)
            throws Exception, ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Employee employee = service.getEmployeeById(id);
        request.setAttribute("employee", employee);
        request.setAttribute("action", "update");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/employee-form.jsp");
        dispatcher.forward(request, response);
    }
    
    private void viewEmployee(HttpServletRequest request, HttpServletResponse response, EmployeeService service)
            throws Exception, ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Employee employee = service.getEmployeeById(id);
        request.setAttribute("employee", employee);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/employee-detail.jsp");
        dispatcher.forward(request, response);
    }
    
    private void createEmployee(HttpServletRequest request, HttpServletResponse response, EmployeeService service)
            throws Exception, IOException {
        Employee employee = new Employee();
        employee.setName(request.getParameter("name"));
        employee.setEmail(request.getParameter("email"));
        employee.setDepartment(request.getParameter("department"));
        
        try {
            Long id = service.createEmployee(employee);
            response.sendRedirect("employee?action=list&message=Employee created successfully with ID: " + id);
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("employee", employee);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/employee-form.jsp");
            try {
                dispatcher.forward(request, response);
            } catch (ServletException se) {
                throw new IOException(se);
            }
        }
    }
    
    private void updateEmployee(HttpServletRequest request, HttpServletResponse response, EmployeeService service)
            throws Exception, IOException {
        Employee employee = new Employee();
        employee.setId(Long.parseLong(request.getParameter("id")));
        employee.setName(request.getParameter("name"));
        employee.setEmail(request.getParameter("email"));
        employee.setDepartment(request.getParameter("department"));
        
        try {
            service.updateEmployee(employee);
            response.sendRedirect("employee?action=list&message=Employee updated successfully");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("employee", employee);
            request.setAttribute("action", "update");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/employee-form.jsp");
            try {
                dispatcher.forward(request, response);
            } catch (ServletException se) {
                throw new IOException(se);
            }
        }
    }
    
    private void deleteEmployee(HttpServletRequest request, HttpServletResponse response, EmployeeService service)
            throws Exception, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        service.deleteEmployee(id);
        response.sendRedirect("employee?action=list&message=Employee deleted successfully");
    }
    
    private void showPerformanceForm(HttpServletRequest request, HttpServletResponse response, EmployeeService service)
            throws Exception, ServletException, IOException {
        Long employeeId = Long.parseLong(request.getParameter("id"));
        Employee employee = service.getEmployeeById(employeeId);
        if (employee == null) {
            throw new Exception("Employee not found with ID: " + employeeId);
        }
        
        String month = request.getParameter("month");
        EmployeePerformance performance = null;
        if (month != null && !month.trim().isEmpty()) {
            performance = service.getEmployeePerformance(employeeId, month);
        }
        
        request.setAttribute("employee", employee);
        request.setAttribute("performance", performance);
        request.setAttribute("month", month);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/employee-performance-form.jsp");
        dispatcher.forward(request, response);
    }
    
    private void viewPerformanceHistory(HttpServletRequest request, HttpServletResponse response, EmployeeService service)
            throws Exception, ServletException, IOException {
        Long employeeId = Long.parseLong(request.getParameter("id"));
        Employee employee = service.getEmployeeById(employeeId);
        if (employee == null) {
            throw new Exception("Employee not found with ID: " + employeeId);
        }
        
        List<EmployeePerformance> performances = service.getEmployeePerformanceHistory(employeeId);
        request.setAttribute("employee", employee);
        request.setAttribute("performances", performances);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/employee-performance-history.jsp");
        dispatcher.forward(request, response);
    }
    
    private void updatePerformance(HttpServletRequest request, HttpServletResponse response, EmployeeService service)
            throws Exception, IOException {
        Long employeeId = Long.parseLong(request.getParameter("employeeId"));
        String month = request.getParameter("month");
        String scoreStr = request.getParameter("performanceScore");
        String rating = request.getParameter("rating");
        String notes = request.getParameter("notes");
        
        BigDecimal performanceScore = null;
        if (scoreStr != null && !scoreStr.trim().isEmpty()) {
            try {
                performanceScore = new BigDecimal(scoreStr);
            } catch (NumberFormatException e) {
                response.sendRedirect("employee?action=performance&id=" + employeeId + 
                    "&month=" + month + "&error=Invalid performance score");
                return;
            }
        }
        
        try {
            service.saveEmployeePerformance(employeeId, month, performanceScore, rating, notes);
            response.sendRedirect("employee?action=view&id=" + employeeId + 
                "&message=Performance updated successfully");
        } catch (Exception e) {
            response.sendRedirect("employee?action=performance&id=" + employeeId + 
                "&month=" + month + "&error=" + e.getMessage());
        }
    }
    
    private void handleError(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws ServletException, IOException {
        e.printStackTrace();
        request.setAttribute("error", "An error occurred: " + e.getMessage());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/employee-list.jsp");
        dispatcher.forward(request, response);
    }
}

