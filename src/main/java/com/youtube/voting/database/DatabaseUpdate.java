package com.youtube.voting.database;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class DatabaseUpdate {
    
    public static void updateDatabaseSchema() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("🔄 Updating database schema for admin functionality...");
            
            // 1. Kreiraj roles tabelu
            createRolesTable(conn);
            
            // 2. Kreiraj users tabelu
            createUsersTable(conn);
            
            // 3. Kreiraj user_roles many-to-many tabelu
            createUserRolesTable(conn);
            
            // 4. Dodaj nova polja u videos tabelu za custom slike
            updateVideosTable(conn);
            
            // 5. Dodaj pocetne role i admin korisnika
            insertInitialAdminData(conn);
            
            System.out.println("✅ Database schema updated successfully!");
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createRolesTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS roles (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "name VARCHAR(50) NOT NULL UNIQUE, " +
            "description TEXT, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("✅ Roles table created/checked");
        }
    }
    
    private static void createUsersTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "username VARCHAR(50) NOT NULL UNIQUE, " +
            "email VARCHAR(100) NOT NULL UNIQUE, " +
            "password_hash VARCHAR(255) NOT NULL, " +
            "active BOOLEAN DEFAULT TRUE, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "last_login TIMESTAMP NULL" +
            ")";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("✅ Users table created/checked");
        }
    }
    
    private static void createUserRolesTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS user_roles (" +
            "user_id INT, " +
            "role_id INT, " +
            "assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "PRIMARY KEY (user_id, role_id), " +
            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE" +
            ")";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("✅ User_roles table created/checked");
        }
    }
    
    private static void updateVideosTable(Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet rs = metaData.getColumns(null, null, "videos", "custom_image_path");
        
        if (!rs.next()) {
            String[] alterStatements = {
                "ALTER TABLE videos ADD COLUMN custom_image_path VARCHAR(500) NULL",
                "ALTER TABLE videos ADD COLUMN use_custom_image BOOLEAN DEFAULT FALSE",
                "ALTER TABLE videos ADD COLUMN description TEXT NULL"
            };
            
            try (Statement stmt = conn.createStatement()) {
                for (String alterSql : alterStatements) {
                    try {
                        stmt.executeUpdate(alterSql);
                        System.out.println("✅ Added column to videos table");
                    } catch (SQLException e) {
                        if (!e.getMessage().contains("Duplicate column name")) {
                            throw e;
                        }
                        System.out.println("ℹ️ Column already exists in videos table");
                    }
                }
            }
        } else {
            System.out.println("ℹ️ Videos table already updated");
        }
    }
    
    private static void insertInitialAdminData(Connection conn) throws SQLException {
        String checkRoleSql = "SELECT COUNT(*) FROM roles WHERE name = 'Admin'";
        try (PreparedStatement stmt = conn.prepareStatement(checkRoleSql)) {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                String insertRoleSql = "INSERT INTO roles (name, description) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertRoleSql)) {
                    insertStmt.setString(1, "Admin");
                    insertStmt.setString(2, "Administrator role with full access to admin panel");
                    insertStmt.executeUpdate();
                    System.out.println("✅ Admin role created");
                }
            }
        }
        
        // 2. Provjeri da li admin korisnik postoji
        String checkUserSql = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
        try (PreparedStatement stmt = conn.prepareStatement(checkUserSql)) {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                // Kreiraj admin korisnika
                String hashedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt());
                String insertUserSql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
                
                int userId;
                try (PreparedStatement insertStmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setString(1, "admin");
                    insertStmt.setString(2, "admin@youtube-voting.com");
                    insertStmt.setString(3, hashedPassword);
                    insertStmt.executeUpdate();
                    
                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    generatedKeys.next();
                    userId = generatedKeys.getInt(1);
                    System.out.println("✅ Admin user created (username: admin, password: admin123)");
                }
                
                String getRoleIdSql = "SELECT id FROM roles WHERE name = 'Admin'";
                int roleId;
                try (PreparedStatement stmt2 = conn.prepareStatement(getRoleIdSql)) {
                    ResultSet rs2 = stmt2.executeQuery();
                    rs2.next();
                    roleId = rs2.getInt(1);
                }
                
                String assignRoleSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
                try (PreparedStatement assignStmt = conn.prepareStatement(assignRoleSql)) {
                    assignStmt.setInt(1, userId);
                    assignStmt.setInt(2, roleId);
                    assignStmt.executeUpdate();
                    System.out.println("✅ Admin role assigned to admin user");
                }
            } else {
                System.out.println("ℹ️ Admin user already exists");
            }
        }
        
        String checkEditorRoleSql = "SELECT COUNT(*) FROM roles WHERE name = 'Editor'";
        try (PreparedStatement stmt = conn.prepareStatement(checkEditorRoleSql)) {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                String insertRoleSql = "INSERT INTO roles (name, description) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertRoleSql)) {
                    insertStmt.setString(1, "Editor");
                    insertStmt.setString(2, "Editor role with limited admin access");
                    insertStmt.executeUpdate();
                    System.out.println("✅ Editor role created for future use");
                }
            }
        }
        
        System.out.println();
        System.out.println("🎉 ADMIN SETUP COMPLETE!");
        System.out.println("===========================");
        System.out.println("Default admin credentials:");
        System.out.println("Username: admin");
        System.out.println("Password: admin123");
        System.out.println("Login URL: /admin/login");
        System.out.println("===========================");
    }
    
    public static void main(String[] args) {
        System.out.println("YOUTUBE VOTING - DATABASE UPDATE");
        System.out.println("==================================");
        updateDatabaseSchema();
    }
}