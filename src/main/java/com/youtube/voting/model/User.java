package com.youtube.voting.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private List<Role> roles;
    
    // Konstruktori
    public User() {
        this.roles = new ArrayList<>();
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }
    
    public User(String username, String email, String passwordHash) {
        this();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    
    // Getteri i setteri
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public List<Role> getRoles() {
        return roles;
    }
    
    public void setRoles(List<Role> roles) {
        this.roles = roles != null ? roles : new ArrayList<>();
    }
    
    // Metode za upravljanje ulogama
    public void addRole(Role role) {
        if (role != null && !roles.contains(role)) {
            roles.add(role);
        }
    }
    
    public void removeRole(Role role) {
        roles.remove(role);
    }
    
    public boolean hasRole(String roleName) {
        return roles.stream()
                   .anyMatch(role -> role.getName().equalsIgnoreCase(roleName));
    }
    
    public boolean hasAnyRole(String... roleNames) {
        for (String roleName : roleNames) {
            if (hasRole(roleName)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAdmin() {
        return hasRole("Admin");
    }
    
    // Utility metode
    public String getRolesAsString() {
        if (roles.isEmpty()) {
            return "No roles";
        }
        return String.join(", ", roles.stream()
                                       .map(Role::getName)
                                       .toArray(String[]::new));
    }
    
    public String getFormattedCreatedAt() {
        return createdAt != null ? createdAt.toString() : "";
    }
    
    public String getFormattedLastLogin() {
        return lastLogin != null ? lastLogin.toString() : "Never";
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                ", roles=" + getRolesAsString() +
                ", createdAt=" + createdAt +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id == user.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}