package com.youtube.voting.servlet.admin;

import com.youtube.voting.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/logout")
public class AdminLogoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        performLogout(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        performLogout(request, response);
    }
    
    private void performLogout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            
            if (loggedInUser != null) {
                System.out.println("User " + loggedInUser.getUsername() + " logged out from admin panel");
            }
            
            session.invalidate();
        }
        
        String loginUrl = request.getContextPath() + "/admin/login?logout=success";
        response.sendRedirect(loginUrl);
    }
}