<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Employee Performance History</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1000px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
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
            background-color: #9c27b0;
            color: white;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        .rating-excellent {
            color: #4CAF50;
            font-weight: bold;
        }
        .rating-good {
            color: #2196F3;
            font-weight: bold;
        }
        .rating-average {
            color: #ff9800;
            font-weight: bold;
        }
        .rating-poor {
            color: #f44336;
            font-weight: bold;
        }
        .btn {
            padding: 8px 16px;
            text-decoration: none;
            border-radius: 4px;
            display: inline-block;
            margin: 5px 5px 5px 0;
        }
        .btn-primary {
            background-color: #4CAF50;
            color: white;
        }
        .btn-primary:hover {
            background-color: #45a049;
        }
        .btn-back {
            background-color: #6c757d;
            color: white;
        }
        .btn-back:hover {
            background-color: #5a6268;
        }
        .no-data {
            text-align: center;
            padding: 40px;
            color: #666;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Employee Performance History</h1>
        
        <c:if test="${not empty employee}">
            <div class="employee-info">
                <strong>Employee:</strong> ${employee.name} (${employee.email})<br>
                <strong>Department:</strong> ${employee.department}
            </div>
        </c:if>
        
        <a href="employee?action=performance&id=${employee.id}" class="btn btn-primary">Add New Performance</a>
        <a href="employee?action=view&id=${employee.id}" class="btn btn-back">Back to Employee Details</a>
        
        <table>
            <thead>
                <tr>
                    <th>Month</th>
                    <th>Performance Score</th>
                    <th>Rating</th>
                    <th>Notes</th>
                    <th>Created At</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty performances}">
                        <tr>
                            <td colspan="5" class="no-data">No performance records found.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="perf" items="${performances}">
                            <tr>
                                <td><strong>${perf.month}</strong></td>
                                <td>${perf.performanceScore}</td>
                                <td>
                                    <span class="rating-${fn:toLowerCase(perf.rating)}">
                                        ${perf.rating}
                                    </span>
                                </td>
                                <td>${not empty perf.notes ? perf.notes : '-'}</td>
                                <td>
                                    <fmt:formatDate value="${perf.createdAt}" pattern="yyyy-MM-dd HH:mm" />
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

