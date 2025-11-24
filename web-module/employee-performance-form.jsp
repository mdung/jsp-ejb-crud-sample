<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Employee Performance</title>
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
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #333;
        }
        input[type="text"],
        input[type="number"],
        select,
        textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 14px;
        }
        textarea {
            height: 100px;
            resize: vertical;
        }
        .btn {
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 4px;
            display: inline-block;
            margin: 5px 5px 5px 0;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }
        .btn-submit {
            background-color: #4CAF50;
            color: white;
        }
        .btn-submit:hover {
            background-color: #45a049;
        }
        .btn-cancel {
            background-color: #6c757d;
            color: white;
        }
        .btn-cancel:hover {
            background-color: #5a6268;
        }
        .employee-info {
            background-color: #e9ecef;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .employee-info strong {
            color: #333;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Employee Performance</h1>
        
        <c:if test="${not empty employee}">
            <div class="employee-info">
                <strong>Employee:</strong> ${employee.name} (${employee.email})<br>
                <strong>Department:</strong> ${employee.department}
            </div>
        </c:if>
        
        <c:if test="${not empty param.error}">
            <div class="error">
                ${param.error}
            </div>
        </c:if>
        
        <form method="post" action="employee">
            <input type="hidden" name="action" value="updatePerformance" />
            <input type="hidden" name="employeeId" value="${employee.id}" />
            
            <div class="form-group">
                <label for="month">Month (YYYY-MM):</label>
                <input type="text" 
                       id="month" 
                       name="month" 
                       value="${not empty param.month ? param.month : (not empty performance ? performance.month : '')}" 
                       pattern="\d{4}-\d{2}" 
                       placeholder="2025-11" 
                       required />
                <small style="color: #666;">Format: YYYY-MM (e.g., 2025-11)</small>
            </div>
            
            <div class="form-group">
                <label for="performanceScore">Performance Score (0-100):</label>
                <input type="number" 
                       id="performanceScore" 
                       name="performanceScore" 
                       min="0" 
                       max="100" 
                       step="0.01"
                       value="${not empty performance ? performance.performanceScore : ''}" 
                       required />
            </div>
            
            <div class="form-group">
                <label for="rating">Rating:</label>
                <select id="rating" name="rating" required>
                    <option value="">-- Select Rating --</option>
                    <option value="Excellent" ${performance.rating == 'Excellent' ? 'selected' : ''}>Excellent</option>
                    <option value="Good" ${performance.rating == 'Good' ? 'selected' : ''}>Good</option>
                    <option value="Average" ${performance.rating == 'Average' ? 'selected' : ''}>Average</option>
                    <option value="Poor" ${performance.rating == 'Poor' ? 'selected' : ''}>Poor</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="notes">Notes:</label>
                <textarea id="notes" name="notes" placeholder="Enter performance notes...">${not empty performance ? performance.notes : ''}</textarea>
            </div>
            
            <div>
                <button type="submit" class="btn btn-submit">Save Performance</button>
                <a href="employee?action=view&id=${employee.id}" class="btn btn-cancel">Cancel</a>
            </div>
        </form>
    </div>
</body>
</html>

