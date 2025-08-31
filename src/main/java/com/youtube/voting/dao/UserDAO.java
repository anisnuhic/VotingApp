package com.youtube.voting.dao;

import com.youtube.voting.database.DatabaseConnection;
import com.youtube.voting.model.User;
import com.youtube.voting.model.Role;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {
    
    public User authenticate(String username, String password) {
    String sql = "SELECT u.*, r.id as role_id, r.name as role_name, r.description as role_description " +
                 "FROM users u " +
                 "LEFT JOIN user_roles ur ON u.id = ur.user_id " +
                 "LEFT JOIN roles r ON ur.role_id = r.id " +    
                 "WHERE u.username = ? AND u.active = TRUE " +
                 "ORDER BY r.name";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        
        User user = null;
        List<Role> roles = new ArrayList<>();
        String storedHash = null;
        
        while (rs.next()) {
            if (user == null) {
                storedHash = rs.getString("password_hash");
                
                if (!BCrypt.checkpw(password, storedHash)) {
                    return null; // Pogrešan password
                }
                
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(storedHash);
                user.setActive(rs.getBoolean("active"));
                
                Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
                if (createdAtTimestamp != null) {
                    user.setCreatedAt(createdAtTimestamp.toLocalDateTime());
                }
                
                Timestamp lastLoginTimestamp = rs.getTimestamp("last_login");
                if (lastLoginTimestamp != null) {
                    user.setLastLogin(lastLoginTimestamp.toLocalDateTime());
                }
            }
            
            if (rs.getObject("role_id") != null) {
                Role role = new Role();
                role.setId(rs.getInt("role_id"));
                role.setName(rs.getString("role_name"));
                role.setDescription(rs.getString("role_description"));
                roles.add(role);
            }
        }
        
        if (user != null) {
            user.setRoles(roles);
            updateLastLogin(user.getId());
        }
        
        return user;
        
    } catch (SQLException e) {
        System.err.println("Error authenticating user: " + e.getMessage());
        e.printStackTrace();
    }
    
    return null;
}
    
    // Kreiranje novog korisnika
    public boolean createUser(User user) {
        if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
            return false;
        }
        
        String sql = "INSERT INTO users (username, email, password_hash, active) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setBoolean(4, user.isActive());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Kreiranje korisnika sa hash-ovanjem password-a
    public boolean createUser(String username, String email, String plainPassword) {
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        User user = new User(username, email, hashedPassword);
        return createUser(user);
    }
    
    // Ažuriranje korisnika
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, active = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setBoolean(3, user.isActive());
            stmt.setInt(4, user.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Ažuriranje password-a
    public boolean updatePassword(int userId, String newPassword) {
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Brisanje korisnika
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public User findById(int id) {
    String sql = "SELECT u.*, r.id as role_id, r.name as role_name, r.description as role_description " +
                 "FROM users u " +
                 "LEFT JOIN user_roles ur ON u.id = ur.user_id " +
                 "LEFT JOIN roles r ON ur.role_id = r.id " +    
                 "WHERE u.id = ? " +
                 "ORDER BY r.name";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        
        User user = null;
        List<Role> roles = new ArrayList<>();
        
        while (rs.next()) {
            // Kreiraj user objekat samo jednom
            if (user == null) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setActive(rs.getBoolean("active"));
                
                Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
                if (createdAtTimestamp != null) {
                    user.setCreatedAt(createdAtTimestamp.toLocalDateTime());
                }
                
                Timestamp lastLoginTimestamp = rs.getTimestamp("last_login");
                if (lastLoginTimestamp != null) {
                    user.setLastLogin(lastLoginTimestamp.toLocalDateTime());
                }
            }
            
            if (rs.getObject("role_id") != null) {
                Role role = new Role();
                role.setId(rs.getInt("role_id"));
                role.setName(rs.getString("role_name"));
                role.setDescription(rs.getString("role_description"));
                roles.add(role);
            }
        }
        
        if (user != null) {
            user.setRoles(roles);
        }
        
        return user;
        
    } catch (SQLException e) {
        System.err.println("Error finding user by ID: " + e.getMessage());
        e.printStackTrace();
    }
    
    return null;
}
    
    public User findByUsername(String username) {
    String sql = "SELECT u.*, r.id as role_id, r.name as role_name, r.description as role_description " +
                 "FROM users u " +
                 "LEFT JOIN user_roles ur ON u.id = ur.user_id " +
                 "LEFT JOIN roles r ON ur.role_id = r.id " +    
                 "WHERE u.username = ? " +
                 "ORDER BY r.name";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        
        User user = null;
        List<Role> roles = new ArrayList<>();
        
        while (rs.next()) {
            if (user == null) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setActive(rs.getBoolean("active"));
                
                Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
                if (createdAtTimestamp != null) {
                    user.setCreatedAt(createdAtTimestamp.toLocalDateTime());
                }
                
                Timestamp lastLoginTimestamp = rs.getTimestamp("last_login");
                if (lastLoginTimestamp != null) {
                    user.setLastLogin(lastLoginTimestamp.toLocalDateTime());
                }
            }
            
            if (rs.getObject("role_id") != null) {
                Role role = new Role();
                role.setId(rs.getInt("role_id"));
                role.setName(rs.getString("role_name"));
                role.setDescription(rs.getString("role_description"));
                roles.add(role);
            }
        }
        
        if (user != null) {
            user.setRoles(roles);
        }
        
        return user;
        
    } catch (SQLException e) {
        System.err.println("Error finding user by username: " + e.getMessage());
        e.printStackTrace();
    }
    
    return null;
}
    
    public List<User> getAllUsers() {
    List<User> users = new ArrayList<>();
    String sql = "SELECT u.*, r.id as role_id, r.name as role_name, r.description as role_description " +
                 "FROM users u " +
                 "LEFT JOIN user_roles ur ON u.id = ur.user_id " +
                 "LEFT JOIN roles r ON ur.role_id = r.id " +    
                 "ORDER BY u.username, r.name";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        ResultSet rs = stmt.executeQuery();
        
        Map<Integer, User> userMap = new HashMap<>();
        
        while (rs.next()) {
            int userId = rs.getInt("id");
            
            if (!userMap.containsKey(userId)) {
                User user = new User();
                user.setId(userId);
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setActive(rs.getBoolean("active"));
                
                Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
                if (createdAtTimestamp != null) {
                    user.setCreatedAt(createdAtTimestamp.toLocalDateTime());
                }
                
                Timestamp lastLoginTimestamp = rs.getTimestamp("last_login");
                if (lastLoginTimestamp != null) {
                    user.setLastLogin(lastLoginTimestamp.toLocalDateTime());
                }
                
                user.setRoles(new ArrayList<>());
                userMap.put(userId, user);
            }
            
            if (rs.getObject("role_id") != null) {
                Role role = new Role();
                role.setId(rs.getInt("role_id"));
                role.setName(rs.getString("role_name"));
                role.setDescription(rs.getString("role_description"));
                
                userMap.get(userId).getRoles().add(role);
            }
        }
        
        users.addAll(userMap.values());
        
    } catch (SQLException e) {
        System.err.println("Error getting all users: " + e.getMessage());
        e.printStackTrace();
    }
    
    return users;
}
    
    // Dodavanje uloge korisniku
    public boolean addRoleToUser(int userId, int roleId) {
        String sql = "INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, roleId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding role to user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Uklanjanje uloge od korisnika
    public boolean removeRoleFromUser(int userId, int roleId) {
        String sql = "DELETE FROM user_roles WHERE user_id = ? AND role_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, roleId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error removing role from user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Provjera da li username postoji
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
        }
        
        return false;
    }
    
    // Provjera da li email postoji
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
        }
        
        return false;
    }
    
    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
        }
    }
}