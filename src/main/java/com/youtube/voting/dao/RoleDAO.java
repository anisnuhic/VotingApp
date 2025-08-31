package com.youtube.voting.dao;

import com.youtube.voting.database.DatabaseConnection;
import com.youtube.voting.model.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {
    
    // Kreiranje nove uloge
    public boolean createRole(Role role) {
        String sql = "INSERT INTO roles (name, description) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, role.getName());
            stmt.setString(2, role.getDescription());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    role.setId(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating role: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Ažuriranje uloge
    public boolean updateRole(Role role) {
        String sql = "UPDATE roles SET name = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, role.getName());
            stmt.setString(2, role.getDescription());
            stmt.setInt(3, role.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating role: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Brisanje uloge
    public boolean deleteRole(int roleId) {
        String deleteUserRolesSql = "DELETE FROM user_roles WHERE role_id = ?";
        String deleteRoleSql = "DELETE FROM roles WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                try (PreparedStatement stmt1 = conn.prepareStatement(deleteUserRolesSql)) {
                    stmt1.setInt(1, roleId);
                    stmt1.executeUpdate();
                }
                
                try (PreparedStatement stmt2 = conn.prepareStatement(deleteRoleSql)) {
                    stmt2.setInt(1, roleId);
                    int rowsAffected = stmt2.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        conn.commit();
                        return true;
                    }
                }
                
                conn.rollback();
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting role: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Pronalaženje uloge po ID-u
    public Role findById(int id) {
        String sql = "SELECT * FROM roles WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToRole(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding role by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Pronalaženje uloge po imenu
    public Role findByName(String name) {
        String sql = "SELECT * FROM roles WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToRole(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding role by name: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Dohvatanje svih uloga
    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                roles.add(mapResultSetToRole(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all roles: " + e.getMessage());
            e.printStackTrace();
        }
        
        return roles;
    }
    
    // Dohvatanje uloga za određenog korisnika
    public List<Role> getRolesForUser(int userId) {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT r.* FROM roles r " +
            "JOIN user_roles ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = ? " +
            "ORDER BY r.name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                roles.add(mapResultSetToRole(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting roles for user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return roles;
    }
    
    // Dohvatanje uloga koje korisnik NEMA
    public List<Role> getAvailableRolesForUser(int userId) {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT r.* FROM roles r " +
            "WHERE r.id NOT IN (" +
            "SELECT ur.role_id FROM user_roles ur WHERE ur.user_id = ?" +
            ") " +
            "ORDER BY r.name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                roles.add(mapResultSetToRole(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting available roles for user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return roles;
    }
    
    // Provjera da li naziv uloge postoji
    public boolean roleNameExists(String name) {
        String sql = "SELECT COUNT(*) FROM roles WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking role name existence: " + e.getMessage());
        }
        
        return false;
    }
    
    // Provjera da li naziv uloge postoji (osim za odredjeni ID)
    public boolean roleNameExistsExcept(String name, int excludeId) {
        String sql = "SELECT COUNT(*) FROM roles WHERE name = ? AND id != ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setInt(2, excludeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking role name existence: " + e.getMessage());
        }
        
        return false;
    }
    
    // Broj korisnika sa odredjenom ulogom
    public int getUserCountForRole(int roleId) {
        String sql = "SELECT COUNT(*) FROM user_roles WHERE role_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roleId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user count for role: " + e.getMessage());
        }
        
        return 0;
    }
    
    //Metoda za mapiranje ResultSet-a u Role objekt
    private Role mapResultSetToRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getInt("id"));
        role.setName(rs.getString("name"));
        role.setDescription(rs.getString("description"));
        
        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        if (createdAtTimestamp != null) {
            role.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }
        
        return role;
    }
}