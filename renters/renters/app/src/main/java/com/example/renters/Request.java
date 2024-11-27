package com.example.renters;

public class Request {
    private String requestId;
    private String itemId;
    private String itemName;
    private String renterName;
    private String renterId;
    private String rentalDuration;
    private String status;
    private String timestamp;  // Use String for date

    // Constructor
    public Request(String requestId, String itemId, String itemName, String renterName,
                   String renterId, String rentalDuration, String status, String timestamp) {
        this.requestId = requestId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.renterName = renterName;
        this.renterId = renterId;
        this.rentalDuration = rentalDuration;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getRenterName() { return renterName; }
    public String getRenterId() { return renterId; }
    public String getRentalDuration() { return rentalDuration; }
    public String getStatus() { return status; }
    public String getTimestamp() { return timestamp; }

    public void setStatus(String status) { this.status = status; }
}