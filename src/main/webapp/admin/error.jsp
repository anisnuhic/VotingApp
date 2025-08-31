<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Admin Panel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/css/admin-style.css">
</head>
<body style="display: flex; align-items: center; justify-content: center; min-height: 100vh;">
    <div class="error-container">
        <div class="error-icon">⚠️</div>
        
        <h1 class="error-title">Oops! Something went wrong</h1>
        
        <div class="error-message">
            <c:choose>
                <c:when test="${not empty error}">
                    <c:out value="${error}" />
                </c:when>
                <c:when test="${not empty pageContext.exception.message}">
                    <c:out value="${pageContext.exception.message}" />
                </c:when>
                <c:otherwise>
                    An unexpected error occurred while processing your request.
                </c:otherwise>
            </c:choose>
        </div>
        
        <c:if test="${not empty pageContext.exception}">
            <div class="error-details">
                <h3>🔍 Technical Details</h3>
                <p><strong>Exception Type:</strong> <c:out value="${pageContext.exception.class.simpleName}" /></p>
                <c:if test="${not empty pageContext.exception.message}">
                    <p><strong>Message:</strong> <c:out value="${pageContext.exception.message}" /></p>
                </c:if>
                
                <c:if test="${pageContext.request.userPrincipal != null}">
                    <details style="margin-top: 15px;">
                        <summary style="cursor: pointer; color: #ffd700; font-weight: bold;">📋 Stack Trace</summary>
                        <pre style="margin-top: 10px; max-height: 200px; overflow-y: auto;">
<c:forEach var="trace" items="${pageContext.exception.stackTrace}" end="10">
<c:out value="${trace}" />
</c:forEach>
                        </pre>
                    </details>
                </c:if>
            </div>
        </c:if>
        
        <div class="stats-grid">
            <a href="javascript:history.back()" class="btn btn-secondary">
                ⬅️ Go Back
            </a>
            
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-primary">
                🏠 Admin Dashboard
            </a>
            
            <a href="${pageContext.request.contextPath}/home" class="btn btn-success">
                🌐 Main Site
            </a>
        </div>
        
        <div style="margin-top: 30px; padding-top: 20px; border-top: 1px solid #444; color: #666; font-size: 0.9em;">
            <p>If this problem persists, please contact the system administrator.</p>
            <p style="margin-top: 5px;">
                <strong>Error Code:</strong> 
                <c:choose>
                    <c:when test="${not empty param.code}">
                        <c:out value="${param.code}" />
                    </c:when>
                    <c:otherwise>
                        ADMIN_ERROR_${pageContext.response.status}
                    </c:otherwise>
                </c:choose>
            </p>
        </div>
    </div>
</body>
</html>