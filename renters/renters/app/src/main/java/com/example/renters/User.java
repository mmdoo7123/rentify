package com.example.renters;

public class User {
    private String email;
    private String userId;
    private String name;

    public User() {
        // Empty constructor needed for Firestore serialization
    }

    public User(String email) {
        this.email = email;
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
}
