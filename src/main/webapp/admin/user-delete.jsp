<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Delete User - Admin Panel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/css/admin-style.css">
</head>
<body>
    <header class="admin-header">
        <div class="header-content">
            <h1 class="admin-title">🗑️ Delete User</h1>
            <div class="user-info">
                <span class="welcome-text">Welcome, <strong><c:out value="${sessionScope.loggedInUser.username}" /></strong>!</span>
                <a href="${pageContext.request.contextPath}/admin/logout" class="logout-btn">🚪 Logout</a>
            </div>
        </div>
    </header>
    
    <nav class="admin-nav">
        <div class="nav-content">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-link">📊 Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/videos" class="nav-link">🎥 Manage Videos</a>
            <a href="${pageContext.request.contextPath}/admin/users" class="nav-link active">👥 Manage Users</a>
            <a href="${pageContext.request.contextPath}/home" class="nav-link">🌐 View Site</a>
        </div>
    </nav>
    
    <main class="content">
        <div class="page-header center">
            <h2 class="page-title">Delete User</h2>
            <div class="breadcrumb">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a> → 
                <a href="${pageContext.request.contextPath}/admin/users">Users</a> → Delete
            </div>
        </div>
        
        <div class="delete-container">
            <div class="warning-icon">⚠️</div>
            <h3 class="warning-title">Confirm User Deletion</h3>
            <p class="warning-message">
                You are about to permanently delete this user account. This action cannot be undone.
            </p>
            
            <div class="user-details">
                <div class="user-info-grid">
                    <div class="user-field">
                        <div class="field-label">👤 Username</div>
                        <div class="field-value"><c:out value="${user.username}" /></div>
                    </div>
                    <div class="user-field">
                        <div class="field-label">📧 Email</div>
                        <div class="field-value"><c:out value="${user.email}" /></div>
                    </div>
                    <div class="user-field">
                        <div class="field-label">📊 Status</div>
                        <div class="field-value">
                            <span class="user-status ${user.active ? 'status-active' : 'status-inactive'}">
                                <c:out value="${user.active ? 'Active' : 'Inactive'}" />
                            </span>
                        </div>
                    </div>
                    <div class="user-field">
                        <div class="field-label">👑 Roles</div>
                        <div class="field-value">
                            <div class="user-roles">
                                <c:choose>
                                    <c:when test="${not empty user.roles}">
                                        <c:forEach var="role" items="${user.roles}">
                                            <span class="role-badge ${role.name == 'Admin' ? 'admin' : ''}">
                                                <c:out value="${role.name}" />
                                            </span>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: #ccc; font-style: italic;">No roles</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="danger-notice">
                <h4>⚠️ Warning</h4>
                <p>This will permanently delete the user account and remove all associated role assignments.</p>
            </div>
            
            <div class="form-actions">
                <form method="post" action="${pageContext.request.contextPath}/admin/users" style="display: inline;">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="<c:out value='${user.id}' />">
                    <button type="submit" class="btn btn-danger" onclick="return confirm('Are you absolutely sure you want to delete user: ${user.username}?');">
                        🗑️ Yes, Delete User
                    </button>
                </form>
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">❌ Cancel</a>
            </div>
        </div>
    </main>
</body>
</html>