<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Delete Video - Admin Panel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/css/admin-style.css">
</head>
<body>
    <header class="admin-header">
        <div class="header-content">
            <h1 class="admin-title">🗑️ Delete Video</h1>
            <div class="user-info">
                <span class="welcome-text">Welcome, <strong><c:out value="${sessionScope.loggedInUser.username}" /></strong>!</span>
                <a href="${pageContext.request.contextPath}/admin/logout" class="logout-btn">🚪 Logout</a>
            </div>
        </div>
    </header>
    
    <nav class="admin-nav">
        <div class="nav-content">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-link">📊 Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/videos" class="nav-link active">🎥 Manage Videos</a>
            <a href="${pageContext.request.contextPath}/admin/users" class="nav-link">👥 Manage Users</a>
            <a href="${pageContext.request.contextPath}/home" class="nav-link">🌐 View Site</a>
        </div>
    </nav>
    
    <main class="content">
        <div class="page-header center">
            <h2 class="page-title">Delete Video</h2>
            <div class="breadcrumb">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a> → 
                <a href="${pageContext.request.contextPath}/admin/videos">Videos</a> → Delete
            </div>
        </div>
        
        <div class="delete-container">
            <div class="warning-icon">⚠️</div>
            <h3 class="warning-title">Confirm Video Deletion</h3>
            <p class="warning-message">
                You are about to permanently delete this video. This action cannot be undone.
            </p>
            
            <div class="video-details">
                <div class="video-info">
                    <img src="<c:out value='${video.thumbnailUrl}' />" 
                         alt="<c:out value='${video.title}' />" 
                         class="video-thumbnail">
                    
                    <div class="video-data">
                        <h3><c:out value="${video.title}" /></h3>
                        <p><strong>Video ID:</strong> <c:out value="${video.id}" /></p>
                        <p><strong>YouTube ID:</strong> <c:out value="${video.youtubeId}" /></p>
                        <c:if test="${not empty video.description}">
                            <p><strong>Description:</strong> <c:out value="${video.description}" /></p>
                        </c:if>
                        <p><strong>Image Type:</strong> 
                            <c:choose>
                                <c:when test="${video.useCustomImage}">
                                    <span style="color: #28a745;">Custom Image</span>
                                </c:when>
                                <c:otherwise>
                                    <span style="color: #dc3545;">YouTube Thumbnail</span>
                                </c:otherwise>
                            </c:choose>
                        </p>
                        
                        <div class="stats-grid mt-20">
                            <div class="stat-item">
                                <div class="stat-number"><c:out value="${video.positiveVotes}" /></div>
                                <div class="stat-label">👍 Positive</div>
                            </div>
                            <div class="stat-item">
                                <div class="stat-number"><c:out value="${video.negativeVotes}" /></div>
                                <div class="stat-label">👎 Negative</div>
                            </div>
                            <div class="stat-item">
                                <div class="stat-number"><c:out value="${video.totalVotes}" /></div>
                                <div class="stat-label">📊 Total</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="danger-notice">
                <h4>⚠️ Warning</h4>
                <p>This will permanently delete the video from the database and remove any associated custom images.</p>
            </div>
            
            <div class="form-actions">
                <form method="post" action="${pageContext.request.contextPath}/admin/videos" style="display: inline;">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="<c:out value='${video.id}' />">
                    <button type="submit" class="btn btn-danger" onclick="return confirm('Are you absolutely sure you want to delete this video?');">
                        🗑️ Yes, Delete Video
                    </button>
                </form>
                <a href="${pageContext.request.contextPath}/admin/videos" class="btn btn-secondary">❌ Cancel</a>
            </div>
        </div>
    </main>
</body>
</html>