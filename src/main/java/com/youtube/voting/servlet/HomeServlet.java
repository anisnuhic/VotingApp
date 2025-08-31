package com.youtube.voting.servlet;

import com.youtube.voting.dao.VideoDAO;
import com.youtube.voting.model.Video;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    
    private VideoDAO videoDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        videoDAO = new VideoDAO();
        System.out.println("HomeServlet initialized!");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            List<Video> videosToShow;
            
            String video1Param = request.getParameter("video1");
            String video2Param = request.getParameter("video2");
            
            if (video1Param != null && video2Param != null && 
                !video1Param.trim().isEmpty() && !video2Param.trim().isEmpty()) {
                
                try {
                    int video1Id = Integer.parseInt(video1Param.trim());
                    int video2Id = Integer.parseInt(video2Param.trim());
                    
                    System.out.println("Attempting to fetch specific videos: " + video1Id + ", " + video2Id);
                    
                    videosToShow = videoDAO.getSpecificVideos(video1Id, video2Id);
                    
                    if (videosToShow.size() != 2) {
                        System.out.println("Specific videos not found (got: " + videosToShow.size() + " videos), using random videos");
                        videosToShow = videoDAO.getRandomVideos(2);
                    } else {
                        System.out.println("Successfully loaded specific videos: " + video1Id + ", " + video2Id);
                    }
                    
                } catch (NumberFormatException e) {
                    System.out.println("Invalid video ID parameters (" + video1Param + ", " + video2Param + "), using random videos");
                    videosToShow = videoDAO.getRandomVideos(2);
                }
                
            } else {
                System.out.println("Loading 2 random videos");
                videosToShow = videoDAO.getRandomVideos(2);
            }
            
            System.out.println("Fetching Top 5 videos...");
            List<Video> top5Videos = videoDAO.getTop5Videos();
            
            System.out.println("=== DEBUG INFORMATION ===");
            System.out.println("Random/Specific videos: " + videosToShow.size());
            for (Video video : videosToShow) {
                System.out.println("- " + video.getTitle() + " (ID: " + video.getId() + ", Votes: " + video.getPositiveVotes() + ")");
            }
            
            System.out.println("Top 5 videos: " + top5Videos.size());
            for (Video video : top5Videos) {
                System.out.println("- " + video.getTitle() + " (ID: " + video.getId() + ", Votes: " + video.getPositiveVotes() + ")");
            }
            System.out.println("========================");
            
            if (videosToShow.isEmpty()) {
                System.out.println("ERROR: No videos to display!");
                request.setAttribute("error", "No videos available in the database.");
            } else {
                System.out.println("Successfully loaded " + videosToShow.size() + " videos for voting");
            }
            
            request.setAttribute("randomVideos", videosToShow);
            request.setAttribute("top5Videos", top5Videos);
            
            System.out.println("Set attributes:");
            System.out.println("- randomVideos: " + (request.getAttribute("randomVideos") != null ? "SET" : "NULL"));
            System.out.println("- top5Videos: " + (request.getAttribute("top5Videos") != null ? "SET" : "NULL"));
            
            System.out.println("Forwarding to index.jsp");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("ERROR in HomeServlet: " + e.getMessage());
            e.printStackTrace();
            
            request.setAttribute("error", "Error loading videos: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}