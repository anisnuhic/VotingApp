package com.youtube.voting.servlet.admin;

import com.youtube.voting.dao.VideoDAO;
import com.youtube.voting.dao.UserDAO;
import com.youtube.voting.dao.RoleDAO;
import com.youtube.voting.model.User;
import com.youtube.voting.model.Video;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    
    private VideoDAO videoDAO;
    private UserDAO userDAO;
    private RoleDAO roleDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        videoDAO = new VideoDAO();
        userDAO = new UserDAO();
        roleDAO = new RoleDAO();
        System.out.println("AdminDashboardServlet initialized");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            User currentUser = (User) request.getAttribute("currentUser");
            
            int totalVideos = videoDAO.getTotalVideoCount();
            List<User> allUsers = userDAO.getAllUsers();
            int totalUsers = allUsers.size();
            int totalAdmins = (int) allUsers.stream().filter(User::isAdmin).count();
            
            List<Video> topVideos = videoDAO.getTop5Videos();
            
            List<Video> allVideosSorted = videoDAO.getAllVideosSortedByVotes();
            List<Video> latestVideos = allVideosSorted.size() > 5 ? 
                allVideosSorted.subList(0, 5) : allVideosSorted;
            
            int totalPositiveVotes = 0;
            int totalNegativeVotes = 0;
            for (Video video : allVideosSorted) {
                totalPositiveVotes += video.getPositiveVotes();
                totalNegativeVotes += video.getNegativeVotes();
            }
            int totalVotes = totalPositiveVotes + totalNegativeVotes;
            
            request.setAttribute("currentUser", currentUser);
            request.setAttribute("totalVideos", totalVideos);
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("totalAdmins", totalAdmins);
            request.setAttribute("totalVotes", totalVotes);
            request.setAttribute("totalPositiveVotes", totalPositiveVotes);
            request.setAttribute("totalNegativeVotes", totalNegativeVotes);
            request.setAttribute("topVideos", topVideos);
            request.setAttribute("latestVideos", latestVideos);
            
            System.out.println("Dashboard stats: Videos=" + totalVideos + 
                              ", Users=" + totalUsers + 
                              ", Admins=" + totalAdmins + 
                              ", Votes=" + totalVotes);
            
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error in AdminDashboardServlet: " + e.getMessage());
            e.printStackTrace();
            
            request.setAttribute("error", "Error loading dashboard: " + e.getMessage());
            request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
        }
    }
}