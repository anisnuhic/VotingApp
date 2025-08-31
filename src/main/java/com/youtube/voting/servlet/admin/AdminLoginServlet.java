package com.youtube.voting.servlet.admin;

import com.youtube.voting.dao.UserDAO;
import com.youtube.voting.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        System.out.println("AdminLoginServlet initialized");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            if (loggedInUser != null && loggedInUser.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return;
            }
        }
        
        request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");
        
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            
            request.setAttribute("error", "Username and password are required");
            request.setAttribute("username", username); 
            request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
            return;
        }
        
        try {
            User user = userDAO.authenticate(username.trim(), password);
            
            if (user != null && user.isAdmin()) {
                HttpSession session = request.getSession(true);
                session.setAttribute("loggedInUser", user);
                
                session.setMaxInactiveInterval(30 * 60);
                
                if ("on".equals(remember)) {
                    session.setMaxInactiveInterval(7 * 24 * 60 * 60); 
                }
                
                System.out.println("User " + user.getUsername() + " successfully logged in to admin panel");
                
                String redirectUrl = request.getParameter("redirect");
                if (redirectUrl != null && !redirectUrl.trim().isEmpty() && 
                    redirectUrl.startsWith("/admin/")) {
                    response.sendRedirect(request.getContextPath() + redirectUrl);
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                }
                
            } else if (user != null && !user.isAdmin()) {
                request.setAttribute("error", "Access denied - Admin privileges required");
                request.setAttribute("username", username);
                request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
                
            } else {
                request.setAttribute("error", "Invalid username or password");
                request.setAttribute("username", username);
                request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            System.err.println("Error during admin login: " + e.getMessage());
            e.printStackTrace();
            
            request.setAttribute("error", "An error occurred during login. Please try again.");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
        }
    }
}