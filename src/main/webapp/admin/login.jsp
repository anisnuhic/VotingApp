<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Login - YouTube Voting</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/css/admin-style.css">
</head>
<body style="display: flex; align-items: center; justify-content: center; min-height: 100vh;">
    <div class="login-container">
        <div class="login-header">
            <h1>🔐 Admin Panel</h1>
            <p>YouTube Voting Administration</p>
        </div>
        
        <c:if test="${param.logout == 'success'}">
            <div class="message success-message">
                ✅ You have been successfully logged out.
            </div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="message error-message">
                ❌ <c:out value="${error}" />
            </div>
        </c:if>
        
        <div class="info-box">
            <h3>📋 Default Credentials</h3>
            <p>
                Username: <code>admin</code><br>
                Password: <code>admin123</code>
            </p>
        </div>
        
        <form method="post" action="${pageContext.request.contextPath}/admin/login">
            <div class="form-group">
                <label for="username">👤 Username</label>
                <input type="text" 
                       id="username" 
                       name="username" 
                       value="<c:out value='${username}' />" 
                       required 
                       autofocus
                       placeholder="Enter your username">
            </div>
            
            <div class="form-group">
                <label for="password">🔑 Password</label>
                <input type="password" 
                       id="password" 
                       name="password" 
                       required
                       placeholder="Enter your password">
            </div>
            
            <div class="checkbox-group">
                <input type="checkbox" id="remember" name="remember">
                <label for="remember">Remember me for 7 days</label>
            </div>
            
            <c:if test="${not empty param.redirect}">
                <input type="hidden" name="redirect" value="<c:out value='${param.redirect}' />">
            </c:if>
            
            <button type="submit" class="login-button">
                🚀 Login to Admin Panel
            </button>
        </form>
        
        <div class="back-link">
            <a href="${pageContext.request.contextPath}/home">
                ← Back to YouTube Voting
            </a>
        </div>
    </div>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const usernameField = document.getElementById('username');
            const passwordField = document.getElementById('password');
            
            if (!usernameField.value) {
                usernameField.focus();
            } else {
                passwordField.focus();
            }
        });
        
        document.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                document.querySelector('form').submit();
            }
        });
    </script>
</body>
</html>