package com.anas.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String orderId;
    private final String customerName;
    private final String address;
    private final String paymentMethod; // "Cash" or "Card"
    private final double totalAmount;
    private String status; // "Pending", "Preparing", "Out for Delivery", "Completed", "Cancelled"
    private final ArrayList<MenuItem> items;
    private final String orderTime;

    public Order(String name, String address, String payment, double total, ArrayList<MenuItem> items) {
        this.orderId = "ORD-" + System.currentTimeMillis() % 10000; // Random ID
        this.customerName = name;
        this.address = address;
        this.paymentMethod = payment;
        this.totalAmount = total;
        this.items = new ArrayList<>(items); // Copy list
        this.status = "Pending";
        this.orderTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    // Getters & Setters
    public String getOrderId() {
        return orderId; 
    }
    public String getCustomerName() { 
        return customerName; 
    }
    public String getAddress() { 
        return address; 
    }
    public String getPaymentMethod() { 
        return paymentMethod; 
    }
    public double getTotalAmount() { 
        return totalAmount; 
    }
    public ArrayList<MenuItem> getItems() { 
        return items; 
    }
    public String getOrderTime() { 
        return orderTime; 
    }
    
    public String getStatus() { 
        return status; 
    }
    public void setStatus(String status) { 
        this.status = status; 
    }

    // Helper to print item summary
    public String getItemsSummary() {
        StringBuilder sb = new StringBuilder();
        for(MenuItem i : items) sb.append("- ").append(i.getItemName()).append("\n");
        return sb.toString();
    }
}