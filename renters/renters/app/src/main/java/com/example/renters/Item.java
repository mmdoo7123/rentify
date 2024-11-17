package com.example.renters;

public class Item {
    private String id; // Firestore document ID
    private String name;
    private String description;
    private double fee; // Fee for renting
    private String timePeriod; // Rent duration
    private String category; // Category of the item
    private String lessorId;
    private String role;

    // Default constructor required for Firestore deserialization
    public Item() {
    }

    public Item(String id, String name, String description, double fee, String timePeriod, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.fee = fee;
        this.timePeriod = timePeriod;
        this.category = category;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public String getLessorId() {
        return lessorId; // Getter for lessorId
    }

    public void setLessorId(String lessorId) {
        this.lessorId = lessorId; // Setter for lessorId
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.name = role;
    }
}
