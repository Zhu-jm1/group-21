<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>TA Recruitment System</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #f5f5f5; }
        .navbar { background: #2c3e50; color: white; padding: 12px 24px; display: flex; justify-content: space-between; align-items: center; }
        .navbar a { color: #ecf0f1; text-decoration: none; margin-left: 16px; }
        .navbar a:hover { color: #3498db; }
        .container { max-width: 960px; margin: 24px auto; padding: 0 16px; }
        .card { background: white; border-radius: 8px; padding: 20px; margin-bottom: 16px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .btn { display: inline-block; padding: 8px 16px; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; color: white; font-size: 14px; }
        .btn-primary { background: #3498db; }
        .btn-success { background: #27ae60; }
        .btn-danger { background: #e74c3c; }
        .btn-warning { background: #f39c12; }
        table { width: 100%; border-collapse: collapse; margin-top: 12px; }
        th, td { padding: 10px 12px; text-align: left; border-bottom: 1px solid #eee; }
        th { background: #f8f9fa; font-weight: bold; }
        .form-group { margin-bottom: 14px; }
        .form-group label { display: block; margin-bottom: 4px; font-weight: bold; }
        .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
        .error { color: #e74c3c; margin-bottom: 12px; }
        .success { color: #27ae60; margin-bottom: 12px; }
        .badge { display: inline-block; padding: 2px 8px; border-radius: 10px; font-size: 12px; color: white; }
        .badge-pending { background: #f39c12; }
        .badge-accepted { background: #27ae60; }
        .badge-rejected { background: #e74c3c; }
        .badge-open { background: #3498db; }
        .badge-closed { background: #95a5a6; }
    </style>
</head>
<body>
<div class="navbar">
    <span><strong>TA Recruitment System</strong></span>
    <div>
        <c:if test="${not empty currentUser}">
            <span>Welcome, ${currentUser.name} (${currentUser.role})</span>
            <c:if test="${currentUser.role == 'TA'}">
                <a href="${pageContext.request.contextPath}/jobs?action=list">Jobs</a>
                <a href="${pageContext.request.contextPath}/applications?action=myApplications">My Applications</a>
            </c:if>
            <c:if test="${currentUser.role == 'MO'}">
                <a href="${pageContext.request.contextPath}/jobs?action=myJobs">My Jobs</a>
                <a href="${pageContext.request.contextPath}/jobs?action=create">Post Job</a>
            </c:if>
            <c:if test="${currentUser.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/applications?action=workload">Workload</a>
            </c:if>
            <a href="${pageContext.request.contextPath}/profile">Profile</a>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </c:if>
    </div>
</div>
<div class="container">
