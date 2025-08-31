package com.youtube.voting.servlet.admin;

import com.youtube.voting.dao.VideoDAO;
import com.youtube.voting.model.Video;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@WebServlet("/admin/videos")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,     
    maxFileSize = 1024 * 1024 * 10,      
    maxRequestSize = 1024 * 1024 * 50    
)
public class AdminVideosServlet extends HttpServlet {
    
    private VideoDAO videoDAO;
    private static final String UPLOAD_DIR = "uploads/images";
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
    
    @Override
    public void init() throws ServletException {
        super.init();
        videoDAO = new VideoDAO();
        
        createUploadDirectory();
        
        System.out.println("AdminVideosServlet initialized");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            if ("edit".equals(action)) {
                showEditForm(request, response);
            } else if ("delete".equals(action)) {
                showDeleteConfirmation(request, response);
            } else if ("add".equals(action)) {
                showAddForm(request, response);
            } else {
                showVideosList(request, response);
            }
        } catch (Exception e) {
            System.err.println("Error in AdminVideosServlet GET: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("/admin/videos.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            if ("create".equals(action)) {
                createVideo(request, response);
            } else if ("update".equals(action)) {
                updateVideo(request, response);
            } else if ("delete".equals(action)) {
                deleteVideo(request, response);
            } else {
                showVideosList(request, response);
            }
        } catch (Exception e) {
            System.err.println("Error in AdminVideosServlet POST: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            showVideosList(request, response);
        }
    }
    
    private void showVideosList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pageParam = request.getParameter("page");
        int currentPage = 1;
        int pageSize = 10; 
        
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }
        
        List<Video> videos = videoDAO.getAllVideosSortedByWilsonScoreWithPagination(currentPage, pageSize);
        int totalVideos = videoDAO.getTotalVideoCount();
        int totalPages = videoDAO.getTotalPages(pageSize);
        
        boolean hasPrevious = currentPage > 1;
        boolean hasNext = currentPage < totalPages;
        
        request.setAttribute("videos", videos);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalVideos", totalVideos);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("hasPrevious", hasPrevious);
        request.setAttribute("hasNext", hasNext);
        
        request.getRequestDispatcher("/admin/videos.jsp").forward(request, response);
    }
    
    private void showAddForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setAttribute("mode", "add");
        request.setAttribute("currentPage", "videos");
        
        request.getRequestDispatcher("/admin/video-form.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String videoIdStr = request.getParameter("id");
        if (videoIdStr == null || videoIdStr.trim().isEmpty()) {
            request.setAttribute("error", "Video ID is required");
            showVideosList(request, response);
            return;
        }
        
        try {
            int videoId = Integer.parseInt(videoIdStr);
            Video video = videoDAO.findById(videoId);
            
            if (video == null) {
                request.setAttribute("error", "Video not found");
                showVideosList(request, response);
                return;
            }
            
            request.setAttribute("video", video);
            request.setAttribute("mode", "edit");
            request.setAttribute("currentPage", "videos");
            
            request.getRequestDispatcher("/admin/video-form.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid video ID");
            showVideosList(request, response);
        }
    }
    
    private void showDeleteConfirmation(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String videoIdStr = request.getParameter("id");
        if (videoIdStr == null || videoIdStr.trim().isEmpty()) {
            request.setAttribute("error", "Video ID is required");
            showVideosList(request, response);
            return;
        }
        
        try {
            int videoId = Integer.parseInt(videoIdStr);
            Video video = videoDAO.findById(videoId);
            
            if (video == null) {
                request.setAttribute("error", "Video not found");
                showVideosList(request, response);
                return;
            }
            
            request.setAttribute("video", video);
            request.setAttribute("mode", "delete");
            request.setAttribute("currentPage", "videos");
            
            request.getRequestDispatcher("/admin/video-delete.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid video ID");
            showVideosList(request, response);
        }
    }
    
    private void createVideo(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String title = request.getParameter("title");
        String youtubeId = request.getParameter("youtubeId");
        String description = request.getParameter("description");
        String positiveVotesStr = request.getParameter("positiveVotes");
        String negativeVotesStr = request.getParameter("negativeVotes");
        Part imagePart = request.getPart("customImage");
        
        if (title == null || title.trim().isEmpty() ||
            youtubeId == null || youtubeId.trim().isEmpty()) {
            
            request.setAttribute("error", "Title and YouTube ID are required");
            showAddForm(request, response);
            return;
        }
        
        if (videoDAO.youtubeIdExists(youtubeId.trim())) {
            request.setAttribute("error", "YouTube ID already exists");
            request.setAttribute("title", title);
            request.setAttribute("youtubeId", youtubeId);
            request.setAttribute("description", description);
            showAddForm(request, response);
            return;
        }
        
        int positiveVotes = 0;
        int negativeVotes = 0;
        
        try {
            if (positiveVotesStr != null && !positiveVotesStr.trim().isEmpty()) {
                positiveVotes = Integer.parseInt(positiveVotesStr.trim());
            }
            if (negativeVotesStr != null && !negativeVotesStr.trim().isEmpty()) {
                negativeVotes = Integer.parseInt(negativeVotesStr.trim());
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid vote counts");
            showAddForm(request, response);
            return;
        }
        
        Video video = new Video();
        video.setTitle(title.trim());
        video.setYoutubeId(youtubeId.trim());
        video.setEmbedUrl("https://www.youtube.com/embed/" + youtubeId.trim());
        video.setDescription(description != null ? description.trim() : "");
        video.setPositiveVotes(positiveVotes);
        video.setNegativeVotes(negativeVotes);
        
        String uploadedImagePath = handleImageUpload(imagePart);
        if (uploadedImagePath != null) {
            video.setCustomImagePath(uploadedImagePath);
            video.setUseCustomImage(true);
        } else {
            video.setUseCustomImage(false);
        }
        
        boolean created = videoDAO.createVideo(video);
        
        if (created) {
            request.setAttribute("success", "Video created successfully");
            System.out.println("Video '" + title + "' created successfully");
        } else {
            request.setAttribute("error", "Failed to create video");
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/videos");
    }
    
    private void updateVideo(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    String videoIdStr = request.getParameter("id");
    String title = request.getParameter("title");
    String youtubeId = request.getParameter("youtubeId");
    String description = request.getParameter("description");
    String positiveVotesStr = request.getParameter("positiveVotes");
    String negativeVotesStr = request.getParameter("negativeVotes");
    String removeImage = request.getParameter("removeImage");
    Part imagePart = request.getPart("customImage");
    
    if (videoIdStr == null || videoIdStr.trim().isEmpty()) {
        request.setAttribute("error", "Video ID is required");
        showVideosList(request, response);
        return;
    }
    
    try {
        int originalVideoId = Integer.parseInt(videoIdStr);
        Video existingVideo = videoDAO.findById(originalVideoId);
        
        if (existingVideo == null) {
            request.setAttribute("error", "Video not found");
            showVideosList(request, response);
            return;
        }
        
        if (title == null || title.trim().isEmpty() ||
            youtubeId == null || youtubeId.trim().isEmpty()) {
            
            request.setAttribute("error", "Title and YouTube ID are required");
            showEditForm(request, response);
            return;
        }
        
        int positiveVotes = 0;
        int negativeVotes = 0;
        
        try {
            if (positiveVotesStr != null && !positiveVotesStr.trim().isEmpty()) {
                positiveVotes = Integer.parseInt(positiveVotesStr.trim());
            }
            if (negativeVotesStr != null && !negativeVotesStr.trim().isEmpty()) {
                negativeVotes = Integer.parseInt(negativeVotesStr.trim());
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid vote counts");
            showEditForm(request, response);
            return;
        }
        
        String currentYoutubeId = existingVideo.getYoutubeId();
        String newYoutubeId = youtubeId.trim();
        
        if (newYoutubeId.equals(currentYoutubeId)) {
            System.out.println("YouTube ID nije promenjen - updating postojeći video");
            
            existingVideo.setTitle(title.trim());
            existingVideo.setEmbedUrl("https://www.youtube.com/embed/" + newYoutubeId);
            existingVideo.setDescription(description != null ? description.trim() : "");
            existingVideo.setPositiveVotes(positiveVotes);
            existingVideo.setNegativeVotes(negativeVotes);
            
            String uploadedImagePath = handleImageUpload(imagePart);
            if ("on".equals(removeImage)) {
                if (existingVideo.getCustomImagePath() != null) {
                    deleteImageFile(existingVideo.getCustomImagePath());
                }
                existingVideo.setCustomImagePath(null);
                existingVideo.setUseCustomImage(false);
            } else if (uploadedImagePath != null) {
                if (existingVideo.getCustomImagePath() != null) {
                    deleteImageFile(existingVideo.getCustomImagePath());
                }
                existingVideo.setCustomImagePath(uploadedImagePath);
                existingVideo.setUseCustomImage(true);
            }
            
            boolean updated = videoDAO.updateVideo(existingVideo);
            
            if (updated) {
                request.setAttribute("success", "Video updated successfully");
                System.out.println("Video '" + title + "' updated successfully");
            } else {
                request.setAttribute("error", "Failed to update video");
            }
            
        } else {
            System.out.println("YouTube ID promenjen sa '" + currentYoutubeId + "' na '" + newYoutubeId + "' - creating novi video");
            
            if (videoDAO.youtubeIdExists(newYoutubeId)) {
                request.setAttribute("error", "YouTube ID '" + newYoutubeId + "' already exists. Cannot create duplicate.");
                showEditForm(request, response);
                return;
            }
            
            Video newVideo = new Video();
            newVideo.setTitle(title.trim());
            newVideo.setYoutubeId(newYoutubeId);
            newVideo.setEmbedUrl("https://www.youtube.com/embed/" + newYoutubeId);
            newVideo.setDescription(description != null ? description.trim() : "");
            newVideo.setPositiveVotes(positiveVotes);
            newVideo.setNegativeVotes(negativeVotes);
            
            String uploadedImagePath = handleImageUpload(imagePart);
            if (uploadedImagePath != null) {
                newVideo.setCustomImagePath(uploadedImagePath);
                newVideo.setUseCustomImage(true);
            } else {
                newVideo.setUseCustomImage(false);
            }
            
            boolean created = videoDAO.createVideo(newVideo);
            
            if (created) {
                request.setAttribute("success", "New video created successfully with YouTube ID: " + newYoutubeId);
                System.out.println("New video '" + title + "' created successfully");
            } else {
                request.setAttribute("error", "Failed to create new video");
            }
        }
        
    } catch (NumberFormatException e) {
        request.setAttribute("error", "Invalid video ID");
    }
    
    response.sendRedirect(request.getContextPath() + "/admin/videos");
}
    
    private void deleteVideo(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String videoIdStr = request.getParameter("id");
        
        if (videoIdStr == null || videoIdStr.trim().isEmpty()) {
            request.setAttribute("error", "Video ID is required");
            showVideosList(request, response);
            return;
        }
        
        try {
            int videoId = Integer.parseInt(videoIdStr);
            Video video = videoDAO.findById(videoId);
            
            if (video == null) {
                request.setAttribute("error", "Video not found");
                showVideosList(request, response);
                return;
            }
            
            if (video.getCustomImagePath() != null) {
                deleteImageFile(video.getCustomImagePath());
            }
            
            boolean deleted = videoDAO.deleteVideo(videoId);
            
            if (deleted) {
                request.setAttribute("success", "Video deleted successfully");
                System.out.println("Video '" + video.getTitle() + "' deleted successfully");
            } else {
                request.setAttribute("error", "Failed to delete video");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid video ID");
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/videos");
    }
    
    
    private void createUploadDirectory() {
        try {
            String contextPath = getServletContext().getRealPath("");
            Path uploadPath = Paths.get(contextPath, UPLOAD_DIR);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created upload directory: " + uploadPath);
            }
            
        } catch (IOException e) {
            System.err.println("Failed to create upload directory: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String handleImageUpload(Part imagePart) {
        if (imagePart == null || imagePart.getSize() == 0) {
            return null;
        }
        
        try {
            String originalFileName = imagePart.getSubmittedFileName();
            if (originalFileName == null || originalFileName.trim().isEmpty()) {
                return null;
            }
            
            String fileExtension = getFileExtension(originalFileName).toLowerCase();
            if (!isAllowedImageExtension(fileExtension)) {
                throw new IOException("Unsupported file type: " + fileExtension);
            }
            
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            
            String contextPath = getServletContext().getRealPath("");
            Path uploadPath = Paths.get(contextPath, UPLOAD_DIR);
            Path filePath = uploadPath.resolve(uniqueFileName);
            
            Files.copy(imagePart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            String relativePath = UPLOAD_DIR + "/" + uniqueFileName;
            System.out.println("Image uploaded successfully: " + relativePath);
            
            return relativePath;
            
        } catch (IOException e) {
            System.err.println("Error uploading image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private void deleteImageFile(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return;
        }
        
        try {
            String contextPath = getServletContext().getRealPath("");
            Path filePath = Paths.get(contextPath, imagePath);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("Deleted image file: " + imagePath);
            }
            
        } catch (IOException e) {
            System.err.println("Error deleting image file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex);
    }
    
    private boolean isAllowedImageExtension(String extension) {
        for (String allowedExtension : ALLOWED_EXTENSIONS) {
            if (allowedExtension.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}