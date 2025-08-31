package com.youtube.voting.filter;

import com.youtube.voting.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/admin/*")
public class AdminSecurityFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AdminSecurityFilter initialized - protecting /admin/* paths");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestPath = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        String relativePath = requestPath.substring(contextPath.length());
        
        System.out.println("AdminSecurityFilter: Checking access to " + relativePath);
        
        if (relativePath.equals("/admin/login") || 
            relativePath.equals("/admin/login.jsp") ||
            relativePath.startsWith("/admin/assets/") ||
            relativePath.startsWith("/admin/css/") ||
            relativePath.startsWith("/admin/js/")) {
            
            chain.doFilter(request, response);
            return;
        }
        
        HttpSession session = httpRequest.getSession(false);
        User loggedInUser = null;
        
        if (session != null) {
            loggedInUser = (User) session.getAttribute("loggedInUser");
        }
        
        if (loggedInUser == null) {
            System.out.println("AdminSecurityFilter: No user logged in, redirecting to login");
            
            if (isAjaxRequest(httpRequest)) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"error\": \"Not authenticated\", \"redirect\": \"/admin/login\"}");
                return;
            }
            
            String loginUrl = contextPath + "/admin/login";
            httpResponse.sendRedirect(loginUrl);
            return;
        }
        
        if (!loggedInUser.isAdmin()) {
            System.out.println("AdminSecurityFilter: User " + loggedInUser.getUsername() + 
                              " doesn't have admin role, denying access");
            
            if (isAjaxRequest(httpRequest)) {
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"error\": \"Access denied - Admin role required\"}");
                return;
            }
            
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, 
                                  "Access denied - Admin role required");
            return;
        }
        
        System.out.println("AdminSecurityFilter: Access granted to user " + loggedInUser.getUsername());
        
        httpRequest.setAttribute("currentUser", loggedInUser);
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        System.out.println("AdminSecurityFilter destroyed");
    }
    
    private boolean isAjaxRequest(HttpServletRequest request) {
        String xRequestedWith = request.getHeader("X-Requested-With");
        String accept = request.getHeader("Accept");
        
        return "XMLHttpRequest".equals(xRequestedWith) || 
               (accept != null && accept.contains("application/json"));
    }
}