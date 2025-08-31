<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Users - Admin Panel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/css/admin-style.css">
</head>
<body>
    <header class="admin-header">
        <div class="header-content">
            <h1 class="admin-title">👥 Manage Users</h1>
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
        <div class="page-header">
            <h2 class="page-title">👥 User Management</h2>
            <a href="${pageContext.request.contextPath}/admin/users?action=add" class="add-btn">
                ➕ Add New User
            </a>
        </div>
        
        <c:if test="${not empty success}">
            <div class="message success-message">
                ✅ <c:out value="${success}" />
            </div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="message error-message">
                ❌ <c:out value="${error}" />
            </div>
        </c:if>
        
        <div class="stats-card">
            <div class="stats-grid">
                <div class="stat-item">
                    <div class="stat-number"><c:out value="${users.size()}" /></div>
                    <div class="stat-label">Total Users</div>
                </div>
                <div class="stat-item">
                    <div class="stat-number">
                        <c:set var="activeCount" value="0" />
                        <c:forEach var="user" items="${users}">
                            <c:if test="${user.active}">
                                <c:set var="activeCount" value="${activeCount + 1}" />
                            </c:if>
                        </c:forEach>
                        <c:out value="${activeCount}" />
                    </div>
                    <div class="stat-label">Active Users</div>
                </div>
                <div class="stat-item">
                    <div class="stat-number">
                        <c:set var="adminCount" value="0" />
                        <c:forEach var="user" items="${users}">
                            <c:if test="${user.admin}">
                                <c:set var="adminCount" value="${adminCount + 1}" />
                            </c:if>
                        </c:forEach>
                        <c:out value="${adminCount}" />
                    </div>
                    <div class="stat-label">Admin Users</div>
                </div>
                <div class="stat-item">
                    <div class="stat-number"><c:out value="${roles.size()}" /></div>
                    <div class="stat-label">Available Roles</div>
                </div>
            </div>
        </div>
        
        <c:choose>
            <c:when test="${not empty users}">
                <table class="data-table users-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Status</th>
                            <th>Roles</th>
                            <th>Created</th>
                            <th>Last Login</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="user" items="${users}">
                            <tr>
                                <td><c:out value="${user.id}" /></td>
                                <td><strong><c:out value="${user.username}" /></strong></td>
                                <td><c:out value="${user.email}" /></td>
                                <td>
                                    <span class="user-status ${user.active ? 'status-active' : 'status-inactive'}">
                                        <c:out value="${user.active ? 'Active' : 'Inactive'}" />
                                    </span>
                                </td>
                                <td>
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
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty user.formattedCreatedAt}">
                                            <c:out value="${user.formattedCreatedAt}" />
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty user.formattedLastLogin && user.formattedLastLogin != 'Never'}">
                                            <c:out value="${user.formattedLastLogin}" />
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: #ccc; font-style: italic;">Never</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="action-buttons">
                                        <a href="${pageContext.request.contextPath}/admin/users?action=edit&id=${user.id}" 
                                           class="btn btn-edit">✏️ Edit</a>
                                        
                                        <c:if test="${user.id != sessionScope.loggedInUser.id}">
                                            <a href="${pageContext.request.contextPath}/admin/users?action=delete&id=${user.id}" 
                                               class="btn btn-delete"
                                               onclick="return confirm('Are you sure you want to delete user ${user.username}?')">🗑️ Delete</a>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <h3>No Users Found</h3>
                    <p>There are no users in the system yet.</p>
                    <a href="${pageContext.request.contextPath}/admin/users?action=add" class="add-btn mt-20">
                        ➕ Add First User
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </main>
</body>
</html>