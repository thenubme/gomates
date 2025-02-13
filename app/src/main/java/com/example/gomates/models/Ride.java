package com.example.gomates.models;

public class Ride {
    private String id;
    private String origin;
    private String destination;
    private double fare;
    private String driverId;
    private String timestamp;

    public Ride() {
        // Required empty constructor for Firestore
    }

    public Ride(String origin, String destination, double fare, String driverId) {
        this.origin = origin;
        this.destination = destination;
        this.fare = fare;
        this.driverId = driverId;
        this.timestamp = String.valueOf(System.currentTimeMillis());
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }
    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
