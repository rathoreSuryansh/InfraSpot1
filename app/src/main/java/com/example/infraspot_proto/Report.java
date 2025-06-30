package com.example.infraspot_proto;

public class Report {
    public String description;
    public double latitude;
    public double longitude;
    public String imageUrl;
    public String status;
    private String reportId; // 👈 Add this

    public Report() {
        // Required for Firebase
    }

    public Report(String description, double latitude, double longitude, String imageUrl, String status) {
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
}
