package com.youtube.voting.model;

import java.time.LocalDateTime;

public class Role {
    private int id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    
    // Konstruktori
    public Role() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Role(String name) {
        this();
        this.name = name;
    }
    
    public Role(String name, String description) {
        this(name);
        this.description = description;
    }
    
    // Getteri i setteri
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Utility metode
    public String getFormattedCreatedAt() {
        return createdAt != null ? createdAt.toString() : "";
    }
    
    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Role role = (Role) obj;
        return id == role.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}