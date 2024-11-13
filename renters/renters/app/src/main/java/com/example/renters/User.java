package com.example.renters;

public class User {
    private String email;
    private String userId;
    private String name;
    private String role;

    public User() {
        // Empty constructor needed for Firestore serialization
    }

    public User(String email, String role) {
        this.email = email;
        this.role = role;  // Initialize the role in the constructor
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserId(String id) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }


}
