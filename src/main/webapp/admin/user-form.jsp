<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${mode == 'edit' ? 'Edit User' : 'Add User'}" /> - Admin Panel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/css/admin-style.css">
</head>
<body>
    <header class="admin-header">
        <div class="header-content">
            <h1 class="admin-title">
                <c:choose>
                    <c:when test="${mode == 'edit'}">✏️ Edit User</c:when>
                    <c:otherwise>➕ Add User</c:otherwise>
                </c:choose>
            </h1>
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
            <h2 class="page-title">
                <c:choose>
                    <c:when test="${mode == 'edit'}">Edit User</c:when>
                    <c:otherwise>Add New User</c:otherwise>
                </c:choose>
            </h2>
            <div class="breadcrumb">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a> → 
                <a href="${pageContext.request.contextPath}/admin/users">Users</a> → 
                <c:choose>
                    <c:when test="${mode == 'edit'}">Edit</c:when>
                    <c:otherwise>Add</c:otherwise>
                </c:choose>
            </div>
        </div>
        
        <div class="form-container">
            <c:if test="${not empty error}">
                <div class="message error-message">
                    ❌ <c:out value="${error}" />
                </div>
            </c:if>
            
            <form method="post" action="${pageContext.request.contextPath}/admin/users">
                <input type="hidden" name="action" value="${mode == 'edit' ? 'update' : 'create'}">
                <c:if test="${mode == 'edit'}">
                    <input type="hidden" name="id" value="<c:out value='${user.id}' />">
                </c:if>
                
                <div class="form-grid">
                    <!-- Basic Information -->
                    <div class="form-section">
                        <h3 class="section-title">📝 Basic Information</h3>
                        
                        <div class="form-group">
                            <label for="username">👤 Username <span class="required">*</span></label>
                            <input type="text" 
                                   id="username" 
                                   name="username" 
                                   value="<c:out value='${mode == "edit" ? user.username : param.username}' />" 
                                   required 
                                   autofocus
                                   placeholder="Enter username">
                        </div>
                        
                        <div class="form-group">
                            <label for="email">📧 Email <span class="required">*</span></label>
                            <input type="email" 
                                   id="email" 
                                   name="email" 
                                   value="<c:out value='${mode == "edit" ? user.email : param.email}' />" 
                                   required
                                   placeholder="Enter email address">
                        </div>
                        
                        <div class="form-group">
                            <label for="password">🔑 Password <c:if test="${mode != 'edit'}"><span class="required">*</span></c:if></label>
                            <input type="password" 
                                   id="password" 
                                   name="password" 
                                   <c:if test="${mode != 'edit'}">required</c:if>
                                   placeholder="Enter password">
                            
                            <c:if test="${mode == 'edit'}">
                                <div class="password-note">
                                    <h4>📝 Password Update</h4>
                                    <p>Leave blank to keep the current password. Enter a new password only if you want to change it.</p>
                                </div>
                            </c:if>
                        </div>
                        
                        <div class="form-group">
                            <label for="confirmPassword">🔑 Confirm Password <c:if test="${mode != 'edit'}"><span class="required">*</span></c:if></label>
                            <input type="password" 
                                   id="confirmPassword" 
                                   name="confirmPassword" 
                                   <c:if test="${mode != 'edit'}">required</c:if>
                                   placeholder="Confirm password">
                        </div>
                        
                        <div class="form-group">
                            <div class="checkbox-group">
                                <input type="checkbox" 
                                       id="active" 
                                       name="active" 
                                       <c:if test="${mode == 'edit' ? user.active : true}">checked</c:if>>
                                <label for="active">✅ Active User</label>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Roles Section -->
                    <div class="form-section">
                        <h3 class="section-title">👑 User Roles</h3>
                        
                        <div class="roles-section">
                            <div class="roles-grid">
                                <c:forEach var="role" items="${roles}">
                                    <div class="role-item">
                                        <div class="checkbox-group">
                                            <input type="checkbox" 
                                                   id="role_${role.id}" 
                                                   name="roles" 
                                                   value="${role.id}"
                                                   <c:if test="${mode == 'edit'}">
                                                       <c:forEach var="userRole" items="${user.roles}">
                                                           <c:if test="${userRole.id == role.id}">checked</c:if>
                                                       </c:forEach>
                                                   </c:if>>
                                            <label for="role_${role.id}">
                                                <div class="role-name"><c:out value="${role.name}" /></div>
                                                <div class="role-description"><c:out value="${role.description}" /></div>
                                            </label>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                    
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">
                            <c:choose>
                                <c:when test="${mode == 'edit'}">✅ Update User</c:when>
                                <c:otherwise>➕ Create User</c:otherwise>
                            </c:choose>
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">
                            ❌ Cancel
                        </a>
                    </div>
                </div>
            </form>
        </div>
    </main>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const password = document.getElementById('password');
            const confirmPassword = document.getElementById('confirmPassword');
            
            function validatePasswords() {
                if (password.value !== confirmPassword.value) {
                    confirmPassword.setCustomValidity('Passwords do not match');
                } else {
                    confirmPassword.setCustomValidity('');
                }
            }
            
            password.addEventListener('input', validatePasswords);
            confirmPassword.addEventListener('input', validatePasswords);
            
            const roleCheckboxes = document.querySelectorAll('input[name="roles"]');
            roleCheckboxes.forEach(checkbox => {
                checkbox.addEventListener('change', function() {
                    const roleItem = this.closest('.role-item');
                    if (this.checked) {
                        roleItem.classList.add('selected');
                    } else {
                        roleItem.classList.remove('selected');
                    }
                });
                
                if (checkbox.checked) {
                    checkbox.closest('.role-item').classList.add('selected');
                }
            });
        });
    </script>
</body>
</html>