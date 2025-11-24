<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Employee List</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
        }
        .message {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 4px;
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #4CAF50;
            color: white;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        .btn {
            padding: 8px 16px;
            text-decoration: none;
            border-radius: 4px;
            display: inline-block;
            margin: 5px;
        }
        .btn-primary {
            background-color: #4CAF50;
            color: white;
        }
        .btn-primary:hover {
            background-color: #45a049;
        }
        .btn-edit {
            background-color: #2196F3;
            color: white;
        }
        .btn-edit:hover {
            background-color: #0b7dda;
        }
        .btn-delete {
            background-color: #f44336;
            color: white;
        }
        .btn-delete:hover {
            background-color: #da190b;
        }
        .btn-view {
            background-color: #ff9800;
            color: white;
        }
        .btn-view:hover {
            background-color: #e68900;
        }
        .actions {
            white-space: nowrap;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Employee Management System</h1>
        
        <c:if test="${not empty param.message}">
            <div class="message">
                ${param.message}
            </div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="message error">
                ${error}
            </div>
        </c:if>
        
        <a href="employee?action=new" class="btn btn-primary">Add New Employee</a>
        
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Department</th>
                    <th>Performance</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty employees}">
                        <tr>
                            <td colspan="6" style="text-align: center;">No employees found.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="employee" items="${employees}">
                            <tr>
                                <td>${employee.id}</td>
                                <td>${employee.name}</td>
                                <td>${employee.email}</td>
                                <td>${employee.department}</td>
                                <td>
                                    <a href="employee?action=performanceHistory&id=${employee.id}" 
                                       class="btn btn-view" 
                                       style="background-color: #9c27b0;">View Performance</a>
                                </td>
                                <td class="actions">
                                    <a href="employee?action=view&id=${employee.id}" class="btn btn-view">View</a>
                                    <a href="employee?action=edit&id=${employee.id}" class="btn btn-edit">Edit</a>
                                    <a href="employee?action=delete&id=${employee.id}" 
                                       class="btn btn-delete" 
                                       onclick="return confirm('Are you sure you want to delete this employee?')">Delete</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</body>
</html>

