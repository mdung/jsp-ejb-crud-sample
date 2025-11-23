<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Employee Details</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
        }
        .detail-group {
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 1px solid #eee;
        }
        .detail-label {
            font-weight: bold;
            color: #666;
            margin-bottom: 5px;
        }
        .detail-value {
            color: #333;
            font-size: 16px;
        }
        .btn {
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 4px;
            display: inline-block;
            margin: 5px 5px 5px 0;
        }
        .btn-edit {
            background-color: #2196F3;
            color: white;
        }
        .btn-edit:hover {
            background-color: #0b7dda;
        }
        .btn-back {
            background-color: #6c757d;
            color: white;
        }
        .btn-back:hover {
            background-color: #5a6268;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Employee Details</h1>
        
        <c:if test="${not empty employee}">
            <div class="detail-group">
                <div class="detail-label">ID</div>
                <div class="detail-value">${employee.id}</div>
            </div>
            
            <div class="detail-group">
                <div class="detail-label">Name</div>
                <div class="detail-value">${employee.name}</div>
            </div>
            
            <div class="detail-group">
                <div class="detail-label">Email</div>
                <div class="detail-value">${employee.email}</div>
            </div>
            
            <div class="detail-group">
                <div class="detail-label">Department</div>
                <div class="detail-value">${employee.department}</div>
            </div>
            
            <!-- UPDATED: Added Phone Number display -->
            <div class="detail-group">
                <div class="detail-label">Phone Number</div>
                <div class="detail-value">${employee.phoneNumber != null ? employee.phoneNumber : 'N/A'}</div>
            </div>
            
            <div>
                <a href="employee?action=edit&id=${employee.id}" class="btn btn-edit">Edit</a>
                <a href="employee?action=list" class="btn btn-back">Back to List</a>
            </div>
        </c:if>
        
        <c:if test="${empty employee}">
            <p>Employee not found.</p>
            <a href="employee?action=list" class="btn btn-back">Back to List</a>
        </c:if>
    </div>
</body>
</html>

