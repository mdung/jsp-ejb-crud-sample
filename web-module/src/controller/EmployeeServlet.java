package controller;

import ejb.EmployeeService;
import model.Employee;
import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Employee Servlet Controller
 * Handles all CRUD operations
 * Compatible with Java 6
 */
public class EmployeeServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    private static final String JNDI_NAME = "java:global/employee-demo/ejb-module/EmployeeServiceBean!ejb.EmployeeService";
    
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
    
    private void handleError(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws ServletException, IOException {
        e.printStackTrace();
        request.setAttribute("error", "An error occurred: " + e.getMessage());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/employee-list.jsp");
        dispatcher.forward(request, response);
    }
}

