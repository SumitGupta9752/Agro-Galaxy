package com.krishika.Activity.Model;

public class DeliveryAddress {
    private String name;
    private String phone;
    private String address;

    // Default constructor required for Firebase
    public DeliveryAddress() {
    }

    public DeliveryAddress(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    // Getters and setters (if needed)
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
}