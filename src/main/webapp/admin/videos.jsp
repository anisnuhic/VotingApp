<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Videos - Admin Panel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/css/admin-style.css">
</head>
<body>
    <header class="admin-header">
        <div class="header-content">
            <h1 class="admin-title">🎥 Manage Videos</h1>
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
        <div class="page-header">
            <h2 class="page-title">🎥 Video Management</h2>
            <a href="${pageContext.request.contextPath}/admin/videos?action=add" class="add-btn">
                ➕ Add New Video
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
                    <div class="stat-number"><c:out value="${totalVideos}" /></div>
                    <div class="stat-label">Total Videos</div>
                </div>
                <div class="stat-item">
                    <div class="stat-number">
                        <c:set var="customImageCount" value="0" />
                        <c:forEach var="video" items="${videos}">
                            <c:if test="${video.useCustomImage}">
                                <c:set var="customImageCount" value="${customImageCount + 1}" />
                            </c:if>
                        </c:forEach>
                        <c:out value="${customImageCount}" />
                    </div>
                    <div class="stat-label">Custom Images</div>
                </div>
                <div class="stat-item">
                    <div class="stat-number"><c:out value="${currentPage}" /></div>
                    <div class="stat-label">Current Page</div>
                </div>
                <div class="stat-item">
                    <div class="stat-number"><c:out value="${totalPages}" /></div>
                    <div class="stat-label">Total Pages</div>
                </div>
            </div>
        </div>
        
        <c:choose>
            <c:when test="${not empty videos}">
                <table class="data-table videos-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Thumbnail</th>
                            <th>Video Info</th>
                            <th>Votes</th>
                            <th>Wilson Score</th>
                            <th>Image Type</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="video" items="${videos}">
                            <tr>
                                <td><strong><c:out value="${video.id}" /></strong></td>
                                <td>
                                    <img src="<c:out value='${video.thumbnailUrl}' />" 
                                         alt="<c:out value='${video.title}' />" 
                                         class="video-thumbnail"
                                         onerror="this.src='https://img.youtube.com/vi/${video.youtubeId}/mqdefault.jpg';">
                                </td>
                                <td>
                                    <div class="video-title"><c:out value="${video.title}" /></div>
                                    <div class="video-id">YT: <c:out value="${video.youtubeId}" /></div>
                                    <c:if test="${not empty video.description}">
                                        <div class="video-description" title="<c:out value='${video.description}' />">
                                            <c:out value="${video.description}" />
                                        </div>
                                    </c:if>
                                </td>
                                <td>
                                    <div class="votes-container">
                                        <div class="vote-item">
                                            👍 <span class="votes-positive"><c:out value="${video.positiveVotes}" /></span>
                                        </div>
                                        <div class="vote-item">
                                            👎 <span class="votes-negative"><c:out value="${video.negativeVotes}" /></span>
                                        </div>
                                        <div class="vote-item">
                                            📊 <span class="votes-total"><c:out value="${video.totalVotes}" /></span>
                                        </div>
                                    </div>
                                </td>
                                <td>
                                    <div class="wilson-score">
                                        <fmt:formatNumber value="${video.wilsonScore}" pattern="#0.0000" />
                                    </div>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${video.useCustomImage}">
                                            <span class="custom-image-indicator">📷 Custom</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="youtube-image-indicator">🎬 YouTube</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="action-buttons">
                                        <a href="${video.embedUrl}" 
                                           target="_blank" 
                                           class="btn btn-view" 
                                           title="View on YouTube">👁️ View</a>
                                        
                                        <a href="${pageContext.request.contextPath}/admin/videos?action=edit&id=${video.id}" 
                                           class="btn btn-edit">✏️ Edit</a>
                                        
                                        <a href="${pageContext.request.contextPath}/admin/videos?action=delete&id=${video.id}" 
                                           class="btn btn-delete"
                                           onclick="return confirm('Are you sure you want to delete video: ${video.title}?')">🗑️ Delete</a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                
                <div class="pagination-container">
                    <div class="page-info">
                        Showing videos 
                        <strong><c:out value="${(currentPage - 1) * pageSize + 1}" /></strong> to 
                        <strong><c:out value="${currentPage * pageSize > totalVideos ? totalVideos : currentPage * pageSize}" /></strong> 
                        of <strong><c:out value="${totalVideos}" /></strong>
                    </div>
                    
                    <div class="pagination">
                        <c:if test="${hasPrevious}">
                            <a href="?page=${currentPage - 1}" class="page-link">← Previous</a>
                        </c:if>
                        
                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <c:choose>
                                <c:when test="${i == currentPage}">
                                    <span class="page-link current"><c:out value="${i}" /></span>
                                </c:when>
                                <c:otherwise>
                                    <a href="?page=${i}" class="page-link"><c:out value="${i}" /></a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                        
                        <c:if test="${hasNext}">
                            <a href="?page=${currentPage + 1}" class="page-link">Next →</a>
                        </c:if>
                    </div>
                </div>
                
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <h3>No Videos Found</h3>
                    <p>There are no videos in the system yet.</p>
                    <a href="${pageContext.request.contextPath}/admin/videos?action=add" class="add-btn mt-20">
                        ➕ Add First Video
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </main>
</body>
</html>