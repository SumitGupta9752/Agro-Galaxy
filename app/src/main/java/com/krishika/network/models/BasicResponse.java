package com.krishika.network.models;

public class BasicResponse {
    private String message;
    private String error;

    public BasicResponse() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
