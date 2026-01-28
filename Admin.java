package com.mm.quizmaster.models;

/**
 * Admin Model Class
 * Represents an admin (teacher) user in the system
 */
public class Admin {
    private int id;
    private String name;
    private String email;
    private String password;
    private String createdBy;
    private String createdAt;

    public Admin() {
        this.createdBy = "Super Admin";
    }

    public Admin(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdBy = "Super Admin";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
