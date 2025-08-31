package com.youtube.voting.servlet.admin;

import com.youtube.voting.dao.UserDAO;
import com.youtube.voting.dao.RoleDAO;
import com.youtube.voting.model.User;
import com.youtube.voting.model.Role;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/users")
public class AdminUsersServlet extends HttpServlet {
    
    private UserDAO userDAO;
    private RoleDAO roleDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        roleDAO = new RoleDAO();
        System.out.println("AdminUsersServlet initialized");
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
                showUsersList(request, response);
            }
        } catch (Exception e) {
            System.err.println("Error in AdminUsersServlet GET: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            if ("create".equals(action)) {
                createUser(request, response);
            } else if ("update".equals(action)) {
                updateUser(request, response);
            } else if ("delete".equals(action)) {
                deleteUser(request, response);
            } else if ("toggle-role".equals(action)) {
                toggleUserRole(request, response);
            } else {
                showUsersList(request, response);
            }
        } catch (Exception e) {
            System.err.println("Error in AdminUsersServlet POST: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            showUsersList(request, response);
        }
    }
    
    private void showUsersList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<User> allUsers = userDAO.getAllUsers();
        List<Role> allRoles = roleDAO.getAllRoles();
        
        request.setAttribute("users", allUsers);
        request.setAttribute("roles", allRoles);
        request.setAttribute("currentPage", "users");
        
        request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
    }
    
    private void showAddForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Role> allRoles = roleDAO.getAllRoles();
        request.setAttribute("roles", allRoles);
        request.setAttribute("mode", "add");
        request.setAttribute("currentPage", "users");
        
        request.getRequestDispatcher("/admin/user-form.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String userIdStr = request.getParameter("id");
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            request.setAttribute("error", "User ID is required");
            showUsersList(request, response);
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            User user = userDAO.findById(userId);
            
            if (user == null) {
                request.setAttribute("error", "User not found");
                showUsersList(request, response);
                return;
            }
            
            List<Role> allRoles = roleDAO.getAllRoles();
            List<Role> availableRoles = roleDAO.getAvailableRolesForUser(userId);
            
            request.setAttribute("user", user);
            request.setAttribute("roles", allRoles);
            request.setAttribute("availableRoles", availableRoles);
            request.setAttribute("mode", "edit");
            request.setAttribute("currentPage", "users");
            
            request.getRequestDispatcher("/admin/user-form.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid user ID");
            showUsersList(request, response);
        }
    }
    
    private void showDeleteConfirmation(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String userIdStr = request.getParameter("id");
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            request.setAttribute("error", "User ID is required");
            showUsersList(request, response);
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            User user = userDAO.findById(userId);
            
            if (user == null) {
                request.setAttribute("error", "User not found");
                showUsersList(request, response);
                return;
            }
            
            request.setAttribute("user", user);
            request.setAttribute("mode", "delete");
            request.setAttribute("currentPage", "users");
            
            request.getRequestDispatcher("/admin/user-delete.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid user ID");
            showUsersList(request, response);
        }
    }
    
    private void createUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String active = request.getParameter("active");
        String[] roleIds = request.getParameterValues("roles");
        
        if (username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            
            request.setAttribute("error", "Username, email and password are required");
            showAddForm(request, response);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            showAddForm(request, response);
            return;
        }
        
        if (userDAO.usernameExists(username.trim())) {
            request.setAttribute("error", "Username already exists");
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            showAddForm(request, response);
            return;
        }
        
        if (userDAO.emailExists(email.trim())) {
            request.setAttribute("error", "Email already exists");
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            showAddForm(request, response);
            return;
        }
        
        User user = new User();
        user.setUsername(username.trim());
        user.setEmail(email.trim());
        user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setActive("on".equals(active));
        
        boolean created = userDAO.createUser(user);
        
        if (created) {
            if (roleIds != null) {
                for (String roleIdStr : roleIds) {
                    try {
                        int roleId = Integer.parseInt(roleIdStr);
                        userDAO.addRoleToUser(user.getId(), roleId);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid role ID: " + roleIdStr);
                    }
                }
            }
            
            request.setAttribute("success", "User created successfully");
            System.out.println("User " + username + " created successfully");
        } else {
            request.setAttribute("error", "Failed to create user");
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
    
    private void updateUser(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    String userIdStr = request.getParameter("id");
    String username = request.getParameter("username");
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    String confirmPassword = request.getParameter("confirmPassword");
    String active = request.getParameter("active");
    String[] roleIds = request.getParameterValues("roles"); 
    
    if (userIdStr == null || userIdStr.trim().isEmpty()) {
        request.setAttribute("error", "User ID is required");
        showUsersList(request, response);
        return;
    }
    
    try {
        int userId = Integer.parseInt(userIdStr);
        User existingUser = userDAO.findById(userId);
        
        if (existingUser == null) {
            request.setAttribute("error", "User not found");
            showUsersList(request, response);
            return;
        }
        
        // Validacija
        if (username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            
            request.setAttribute("error", "Username and email are required");
            showEditForm(request, response);
            return;
        }
        
        User userWithSameUsername = userDAO.findByUsername(username.trim());
        if (userWithSameUsername != null && userWithSameUsername.getId() != userId) {
            request.setAttribute("error", "Username already exists");
            showEditForm(request, response);
            return;
        }
        
        if (userDAO.emailExists(email.trim()) && !existingUser.getEmail().equals(email.trim())) {
            request.setAttribute("error", "Email already exists");
            showEditForm(request, response);
            return;
        }
        
        existingUser.setUsername(username.trim());
        existingUser.setEmail(email.trim());
        existingUser.setActive("on".equals(active));
        
        if (password != null && !password.trim().isEmpty()) {
            if (!password.equals(confirmPassword)) {
                request.setAttribute("error", "Passwords do not match");
                showEditForm(request, response);
                return;
            }
            existingUser.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        }
        
        boolean updated = userDAO.updateUser(existingUser);
        
        if (updated) {
            updateUserRoles(userId, roleIds);
            
            request.setAttribute("success", "User updated successfully");
            System.out.println("User " + username + " updated successfully");
        } else {
            request.setAttribute("error", "Failed to update user");
        }
        
    } catch (NumberFormatException e) {
        request.setAttribute("error", "Invalid user ID");
    }
    
    response.sendRedirect(request.getContextPath() + "/admin/users");
}

private void updateUserRoles(int userId, String[] newRoleIds) {
    try {
        System.out.println("DEBUG: Updating roles for user " + userId);
        
        List<Role> currentRoles = roleDAO.getRolesForUser(userId);
        for (Role role : currentRoles) {
            boolean removed = userDAO.removeRoleFromUser(userId, role.getId());
            System.out.println("DEBUG: Removed role " + role.getName() + " (" + role.getId() + "): " + removed);
        }
        
        if (newRoleIds != null) {
            System.out.println("DEBUG: Adding " + newRoleIds.length + " new roles");
            for (String roleIdStr : newRoleIds) {
                try {
                    int roleId = Integer.parseInt(roleIdStr);
                    boolean added = userDAO.addRoleToUser(userId, roleId);
                    System.out.println("DEBUG: Added role " + roleId + ": " + added);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid role ID in update: " + roleIdStr);
                }
            }
        } else {
            System.out.println("DEBUG: No roles selected - user will have no roles");
        }
        
    } catch (Exception e) {
        System.err.println("Error updating user roles: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    private void deleteUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String userIdStr = request.getParameter("id");
        
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            request.setAttribute("error", "User ID is required");
            showUsersList(request, response);
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            User user = userDAO.findById(userId);
            
            if (user == null) {
                request.setAttribute("error", "User not found");
                showUsersList(request, response);
                return;
            }
            
            User currentUser = (User) request.getAttribute("currentUser");
            if (currentUser != null && currentUser.getId() == userId) {
                request.setAttribute("error", "Cannot delete your own account");
                showUsersList(request, response);
                return;
            }
            
            boolean deleted = userDAO.deleteUser(userId);
            
            if (deleted) {
                request.setAttribute("success", "User deleted successfully");
                System.out.println("User " + user.getUsername() + " deleted successfully");
            } else {
                request.setAttribute("error", "Failed to delete user");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid user ID");
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
    
    private void toggleUserRole(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String userIdStr = request.getParameter("userId");
        String roleIdStr = request.getParameter("roleId");
        String operation = request.getParameter("operation");
        
        if (userIdStr == null || roleIdStr == null || operation == null) {
            request.setAttribute("error", "Missing required parameters");
            showUsersList(request, response);
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            int roleId = Integer.parseInt(roleIdStr);
            
            boolean success = false;
            String message = "";
            
            if ("add".equals(operation)) {
                success = userDAO.addRoleToUser(userId, roleId);
                message = success ? "Role added successfully" : "Failed to add role";
            } else if ("remove".equals(operation)) {
                success = userDAO.removeRoleFromUser(userId, roleId);
                message = success ? "Role removed successfully" : "Failed to remove role";
            }
            
            if (success) {
                request.setAttribute("success", message);
            } else {
                request.setAttribute("error", message);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid user or role ID");
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
}