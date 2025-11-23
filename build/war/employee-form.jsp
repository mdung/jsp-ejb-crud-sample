<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><c:choose><c:when test="${action == 'update'}">Edit Employee</c:when><c:otherwise>Add New Employee</c:otherwise></c:choose></title>
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
        .error {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 4px;
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            color: #333;
            font-weight: bold;
        }
        input[type="text"],
        input[type="email"] {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 14px;
        }
        input[type="text"]:focus,
        input[type="email"]:focus {
            border-color: #4CAF50;
            outline: none;
        }
        .btn {
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 4px;
            border: none;
            cursor: pointer;
            font-size: 14px;
            margin-right: 10px;
        }
        .btn-primary {
            background-color: #4CAF50;
            color: white;
        }
        .btn-primary:hover {
            background-color: #45a049;
        }
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        .btn-secondary:hover {
            background-color: #5a6268;
        }
        .required {
            color: red;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1><c:choose><c:when test="${action == 'update'}">Edit Employee</c:when><c:otherwise>Add New Employee</c:otherwise></c:choose></h1>
        
        <c:if test="${not empty error}">
            <div class="error">
                ${error}
            </div>
        </c:if>
        
        <form method="post" action="employee">
            <c:if test="${action == 'update'}">
                <input type="hidden" name="action" value="update" />
                <input type="hidden" name="id" value="${employee.id}" />
            </c:if>
            <c:if test="${action != 'update'}">
                <input type="hidden" name="action" value="create" />
            </c:if>
            
            <div class="form-group">
                <label for="name">Name <span class="required">*</span></label>
                <input type="text" id="name" name="name" 
                       value="${employee.name}" 
                       required 
                       maxlength="100" />
            </div>
            
            <div class="form-group">
                <label for="email">Email <span class="required">*</span></label>
                <input type="email" id="email" name="email" 
                       value="${employee.email}" 
                       required 
                       maxlength="100" />
            </div>
            
            <div class="form-group">
                <label for="department">Department <span class="required">*</span></label>
                <input type="text" id="department" name="department" 
                       value="${employee.department}" 
                       required 
                       maxlength="50" />
            </div>
            
            <div class="form-group">
                <button type="submit" class="btn btn-primary">
                    <c:choose>
                        <c:when test="${action == 'update'}">Update Employee</c:when>
                        <c:otherwise>Create Employee</c:otherwise>
                    </c:choose>
                </button>
                <a href="employee?action=list" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</body>
</html>

