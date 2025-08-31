<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${mode == 'edit' ? 'Edit Video' : 'Add Video'}" /> - Admin Panel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/css/admin-style.css">
</head>
<body>
    <header class="admin-header">
        <div class="header-content">
            <h1 class="admin-title">
                <c:choose>
                    <c:when test="${mode == 'edit'}">✏️ Edit Video</c:when>
                    <c:otherwise>➕ Add Video</c:otherwise>
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
            <a href="${pageContext.request.contextPath}/admin/videos" class="nav-link active">🎥 Manage Videos</a>
            <a href="${pageContext.request.contextPath}/admin/users" class="nav-link">👥 Manage Users</a>
            <a href="${pageContext.request.contextPath}/home" class="nav-link">🌐 View Site</a>
        </div>
    </nav>
    
    <main class="content">
        <div class="page-header center">
            <h2 class="page-title">
                <c:choose>
                    <c:when test="${mode == 'edit'}">Edit Video</c:when>
                    <c:otherwise>Add New Video</c:otherwise>
                </c:choose>
            </h2>
            <div class="breadcrumb">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a> → 
                <a href="${pageContext.request.contextPath}/admin/videos">Videos</a> → 
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
            
            <form method="post" action="${pageContext.request.contextPath}/admin/videos" enctype="multipart/form-data">
                <input type="hidden" name="action" value="${mode == 'edit' ? 'update' : 'create'}">
                <c:if test="${mode == 'edit'}">
                    <input type="hidden" name="id" value="<c:out value='${video.id}' />">
                </c:if>
                
                <div class="form-grid">
                    <!-- Basic Information Section -->
                    <div class="form-section">
                        <h3 class="section-title">📝 Basic Information</h3>
                        
                        <div class="form-group">
                            <label for="title">🎬 Video Title <span class="required">*</span></label>
                            <input type="text" 
                                   id="title" 
                                   name="title" 
                                   value="<c:out value='${mode == "edit" ? video.title : param.title}' />" 
                                   required 
                                   autofocus
                                   placeholder="Enter video title">
                        </div>
                        
                        <div class="form-group">
                            <label for="youtubeId">📺 YouTube ID <span class="required">*</span></label>
                            <input type="text" 
                                   id="youtubeId" 
                                   name="youtubeId" 
                                   value="<c:out value='${mode == "edit" ? video.youtubeId : param.youtubeId}' />" 
                                   required
                                   placeholder="e.g., dQw4w9WgXcQ">
                            
                            <div class="help-text">
                                <h4>📝 How to get YouTube ID</h4>
                                <ul>
                                    <li>From URL: <code>youtube.com/watch?v=<strong>dQw4w9WgXcQ</strong></code></li>
                                    <li>From short URL: <code>youtu.be/<strong>dQw4w9WgXcQ</strong></code></li>
                                    <li>Only the ID part (11 characters)</li>
                                </ul>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="description">📄 Description</label>
                            <textarea id="description" 
                                      name="description" 
                                      placeholder="Enter video description (optional)"><c:out value='${mode == "edit" ? video.description : param.description}' /></textarea>
                        </div>
                    </div>
                    
                    <!-- Image Upload Section -->
                    <div class="form-section">
                        <h3 class="section-title">🖼️ Custom Image</h3>
                        
                        <div class="form-group">
                            <label>📷 Upload Custom Thumbnail</label>
                            
                            <div class="file-upload-container" id="fileUploadContainer">
                                <input type="file" 
                                       id="customImage" 
                                       name="customImage" 
                                       accept="image/*" 
                                       class="file-upload-input">
                                
                                <div class="file-upload-content">
                                    <div class="upload-icon">📷</div>
                                    <div class="upload-text">Click to select image or drag & drop</div>
                                    <div class="upload-hint">Supported: JPG, PNG, GIF, WEBP (max 10MB)</div>
                                </div>
                            </div>
                            
                            <c:if test="${mode == 'edit' && video.useCustomImage && not empty video.customImagePath}">
                                <div class="current-image">
                                    <span class="current-image-label">Current Custom Image:</span>
                                    <img src="<c:out value='${pageContext.request.contextPath}/${video.customImagePath}' />" 
                                         alt="Current custom image">
                                    
                                    <div class="remove-image-container">
                                        <div class="checkbox-group">
                                            <input type="checkbox" id="removeImage" name="removeImage">
                                            <label for="removeImage">🗑️ Remove current custom image</label>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                            
                            <div class="help-text">
                                <h4>💡 Image Guidelines</h4>
                                <ul>
                                    <li>Recommended size: 1280x720 pixels (16:9 ratio)</li>
                                    <li>If no custom image is uploaded, YouTube thumbnail will be used</li>
                                    <li>Custom images override YouTube thumbnails</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Vote Statistics Section -->
                    <div class="form-section">
                        <h3 class="section-title">📊 Vote Statistics</h3>
                        
                        <div class="form-group">
                            <label for="positiveVotes">👍 Positive Votes</label>
                            <input type="number" 
                                   id="positiveVotes" 
                                   name="positiveVotes" 
                                   value="<c:out value='${mode == "edit" ? video.positiveVotes : 0}' />" 
                                   min="0"
                                   placeholder="0">
                        </div>
                        
                        <div class="form-group">
                            <label for="negativeVotes">👎 Negative Votes</label>
                            <input type="number" 
                                   id="negativeVotes" 
                                   name="negativeVotes" 
                                   value="<c:out value='${mode == "edit" ? video.negativeVotes : 0}' />" 
                                   min="0"
                                   placeholder="0">
                        </div>
                        
                        <div class="help-text">
                            <h4>📈 Vote Information</h4>
                            <p>These values represent the current vote counts for this video. Leave at 0 for new videos.</p>
                        </div>
                    </div>
                    
                    <!-- Preview Section -->
                    <div class="form-section">
                        <h3 class="section-title">👁️ Preview</h3>
                        
                        <div id="videoPreview">
                            <c:if test="${mode == 'edit'}">
                                <div class="youtube-preview">
                                    <iframe src="<c:out value='${video.embedUrl}' />" 
                                            title="Video preview" 
                                            allowfullscreen>
                                    </iframe>
                                </div>
                                <div class="preview-info">
                                    <strong>Current video:</strong> <c:out value="${video.title}" /><br>
                                    <strong>YouTube ID:</strong> <c:out value="${video.youtubeId}" />
                                </div>
                            </c:if>
                        </div>
                        
                        <div class="help-text">
                            <h4>🎬 Preview Notes</h4>
                            <p>Preview will update automatically when you enter a valid YouTube ID above.</p>
                        </div>
                    </div>
                    
                    <!-- Form Actions -->
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">
                            <c:choose>
                                <c:when test="${mode == 'edit'}">✅ Update Video</c:when>
                                <c:otherwise>➕ Create Video</c:otherwise>
                            </c:choose>
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/videos" class="btn btn-secondary">
                            ❌ Cancel
                        </a>
                    </div>
                </div>
            </form>
        </div>
    </main>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const youtubeIdInput = document.getElementById('youtubeId');
            const titleInput = document.getElementById('title');
            const fileInput = document.getElementById('customImage');
            const fileUploadContainer = document.getElementById('fileUploadContainer');
            const videoPreview = document.getElementById('videoPreview');
            
            // YouTube ID validation and preview
            youtubeIdInput.addEventListener('input', function() {
                const youtubeId = this.value.trim();
                if (youtubeId && youtubeId.length === 11) {
                    updateVideoPreview(youtubeId);
                } else {
                    clearVideoPreview();
                }
            });
            
            function updateVideoPreview(youtubeId) {
                const embedUrl = 'https://www.youtube.com/embed/' + youtubeId;
                videoPreview.innerHTML = 
                    '<div class="youtube-preview">' +
                        '<iframe src="' + embedUrl + '" title="Video preview" allowfullscreen></iframe>' +
                    '</div>' +
                    '<div class="preview-info">' +
                        '<strong>Preview:</strong> ' + (titleInput.value || 'Enter title above') + '<br>' +
                        '<strong>YouTube ID:</strong> ' + youtubeId + '<br>' +
                        '<strong>Embed URL:</strong> ' + embedUrl +
                    '</div>';
            }
            
            function clearVideoPreview() {
                videoPreview.innerHTML = 
                    '<div class="help-text">' +
                        '<h4>🎬 Preview Notes</h4>' +
                        '<p>Enter a valid 11-character YouTube ID to see the preview.</p>' +
                    '</div>';
            }
            
            // File upload handling
            fileInput.addEventListener('change', function() {
                handleFileSelect(this.files[0]);
            });
            
            // Drag and drop
            fileUploadContainer.addEventListener('dragover', function(e) {
                e.preventDefault();
                this.classList.add('dragover');
            });
            
            fileUploadContainer.addEventListener('dragleave', function(e) {
                e.preventDefault();
                this.classList.remove('dragover');
            });
            
            fileUploadContainer.addEventListener('drop', function(e) {
                e.preventDefault();
                this.classList.remove('dragover');
                
                const files = e.dataTransfer.files;
                if (files.length > 0) {
                    fileInput.files = files;
                    handleFileSelect(files[0]);
                }
            });
            
            function handleFileSelect(file) {
                if (!file) return;
                
                // Validate file type
                const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
                if (!allowedTypes.includes(file.type)) {
                    alert('Please select a valid image file (JPG, PNG, GIF, WEBP)');
                    fileInput.value = '';
                    return;
                }
                
                // Validate file size (10MB)
                if (file.size > 10 * 1024 * 1024) {
                    alert('File size must be less than 10MB');
                    fileInput.value = '';
                    return;
                }
                
                // Update UI - using toFixed equivalent for JavaScript
                const uploadContent = fileUploadContainer.querySelector('.file-upload-content');
                const fileSizeMB = Math.round((file.size / 1024 / 1024) * 100) / 100; // JavaScript equivalent of toFixed(2)
                uploadContent.innerHTML = 
                    '<div class="upload-icon">✅</div>' +
                    '<div class="upload-text">Selected: ' + file.name + '</div>' +
                    '<div class="upload-hint">Size: ' + fileSizeMB + ' MB</div>';
            }
            
            // Form validation
            document.querySelector('form').addEventListener('submit', function(e) {
                const youtubeId = youtubeIdInput.value.trim();
                
                if (youtubeId.length !== 11) {
                    e.preventDefault();
                    alert('YouTube ID must be exactly 11 characters long');
                    youtubeIdInput.focus();
                    return false;
                }
                
                // Basic YouTube ID format validation
                if (!/^[a-zA-Z0-9_-]{11}$/.test(youtubeId)) {
                    e.preventDefault();
                    alert('YouTube ID contains invalid characters');
                    youtubeIdInput.focus();
                    return false;
                }
            });
        });
    </script>
</body>
</html>