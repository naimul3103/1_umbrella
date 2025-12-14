package com.nasimabc.a1umbrella;

public class Booking {
    private String bookingId;
    private String userId;
    private String providerName;
    private String serviceType;
    private String date;
    private String time;
    private String address;
    private String notes;
    private String status;

    public Booking() {
        // Required for Firestore
    }

    public Booking(String userId, String providerName, String serviceType, String date, String time, String address, String notes, String status) {
        this.userId = userId;
        this.providerName = providerName;
        this.serviceType = serviceType;
        this.date = date;
        this.time = time;
        this.address = address;
        this.notes = notes;
        this.status = status;
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
