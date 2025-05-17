package com.krishika.Activity.Model;

public class Order {
    private String orderId;
    private String orderDetails;
    private String date;

    public Order(String orderId, String orderDetails, String date) {
        this.orderId = orderId;
        this.orderDetails = orderDetails;
        this.date = date;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderDetails() {
        return orderDetails;
    }

    public String getDate() {
        return date;
    }
}
