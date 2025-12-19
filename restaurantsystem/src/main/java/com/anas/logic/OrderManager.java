package com.anas.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.anas.models.Order;

public class OrderManager {
    private static final String FILE_NAME = "active_orders.dat";

    // SAVE NEW ORDER
    public static void placeOrder(Order order) {
        ArrayList<Order> orders = loadOrders();
        orders.add(order);
        saveOrders(orders);

    }

    // UPDATE ORDER STATUS (For Admin)
    public static void updateOrderStatus(String orderId, String newStatus) {
        ArrayList<Order> orders = loadOrders();
        for (Order o : orders) {
            if (o.getOrderId().equals(orderId)) {
                o.setStatus(newStatus);
                break;
            }
        }
        saveOrders(orders);
    }

    // DELETE ORDER
    public static void deleteOrder(String orderId) {
        ArrayList<Order> orders = loadOrders();
        orders.removeIf(o -> o.getOrderId().equals(orderId));
        saveOrders(orders);
    }

    public static void clearAllOrders() {
    try {
        // Overwrite the file with an empty list
        saveOrders(new java.util.ArrayList<>()); 
    } catch (Exception e) {
        System.out.println("Error clearing orders: " + e.getMessage());
    }
}

    // Cancel Order
    public static void cancelOrder(String orderId) {
        ArrayList<Order> orders = loadOrders();
        for (Order o : orders) {
            if (o.getOrderId().equals(orderId)) {
                o.setStatus("Cancelled"); 
                break;
            }
        }
        saveOrders(orders);
    }
    // LOAD ALL ORDERS
    @SuppressWarnings("unchecked")
    public static ArrayList<Order> loadOrders() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (ArrayList<Order>) ois.readObject();
        } catch (Exception e) {
            System.out.println("ERROR LOADING ORDERS:");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // PRIVATE SAVE HELPER
    @SuppressWarnings("CallToPrintStackTrace")
    private static void saveOrders(ArrayList<Order> orders) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(orders);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}