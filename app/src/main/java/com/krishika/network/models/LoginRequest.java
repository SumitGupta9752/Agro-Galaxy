package com.krishika.network.models;

public class LoginRequest {
    private String email;
    private String phone;
    private String password;

    public LoginRequest(String email, String phone, String password) {
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    // getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}