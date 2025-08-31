<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - YouTube Voting</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/css/admin-style.css">
</head>
<body>
    <header class="admin-header">
        <div class="header-content">
            <h1 class="admin-title">🎬 Admin Dashboard</h1>
            <div class="user-info">
                <span class="welcome-text">Welcome, <strong><c:out value="${currentUser.username}" /></strong>!</span>
                <a href="${pageContext.request.contextPath}/admin/logout" class="logout-btn">🚪 Logout</a>
            </div>
        </div>
    </header>
    
    <nav class="admin-nav">
        <div class="nav-content">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-link active">📊 Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/videos" class="nav-link">🎥 Manage Videos</a>
            <a href="${pageContext.request.contextPath}/admin/users" class="nav-link">👥 Manage Users</a>
            <a href="${pageContext.request.contextPath}/home" class="nav-link">🌐 View Site</a>
        </div>
    </nav>
    
    <main class="content">
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-number"><c:out value="${totalVideos}" /></div>
                <div class="stat-label">🎬 Total Videos</div>
            </div>
            
            <div class="stat-card">
                <div class="stat-number"><c:out value="${totalVotes}" /></div>
                <div class="stat-label">🗳️ Total Votes</div>
            </div>
            
            <div class="stat-card">
                <div class="stat-number"><c:out value="${totalUsers}" /></div>
                <div class="stat-label">👥 Total Users</div>
            </div>
            
            <div class="stat-card">
                <div class="stat-number"><c:out value="${totalAdmins}" /></div>
                <div class="stat-label">👑 Admin Users</div>
            </div>
        </div>
        
        <div class="content-grid">
            <div class="content-section">
                <h2 class="section-title">🏆 Top Performing Videos</h2>
                <ul class="video-list">
                    <c:forEach var="video" items="${topVideos}" varStatus="status">
                        <li class="video-item">
                            <img src="<c:out value='${video.thumbnailUrl}' />" 
                                 alt="<c:out value='${video.title}' />" 
                                 class="video-thumbnail">
                            <div class="video-info">
                                <div class="video-title"><c:out value="${video.title}" /></div>
                                <div class="video-stats">
                                    👍 <span class="votes-positive"><c:out value="${video.positiveVotes}" /></span> | 
                                    👎 <span class="votes-negative"><c:out value="${video.negativeVotes}" /></span> | 
                                    📊 <c:out value="${video.totalVotes}" /> total
                                </div>
                            </div>
                        </li>
                    </c:forEach>
                </ul>
            </div>
            
            <div class="content-section">
                <h2 class="section-title">📅 Recent Videos</h2>
                <ul class="video-list">
                    <c:forEach var="video" items="${latestVideos}" varStatus="status">
                        <li class="video-item">
                            <img src="<c:out value='${video.thumbnailUrl}' />" 
                                 alt="<c:out value='${video.title}' />" 
                                 class="video-thumbnail">
                            <div class="video-info">
                                <div class="video-title"><c:out value="${video.title}" /></div>
                                <div class="video-stats">
                                    👍 <span class="votes-positive"><c:out value="${video.positiveVotes}" /></span> | 
                                    👎 <span class="votes-negative"><c:out value="${video.negativeVotes}" /></span> | 
                                    📊 <c:out value="${video.totalVotes}" /> total
                                </div>
                            </div>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>
        
        <div class="stats-grid">
            <a href="${pageContext.request.contextPath}/admin/videos" class="btn btn-primary">
                🎥 Manage Videos
            </a>
            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-success">
                👥 Manage Users
            </a>
            <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary">
                🌐 View Public Site
            </a>
        </div>
    </main>
</body>
</html>