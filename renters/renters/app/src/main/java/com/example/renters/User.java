package com.example.renters;

public class User {
    private String email;
    private int userId;

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
}
