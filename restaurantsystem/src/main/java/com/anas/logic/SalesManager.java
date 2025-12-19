package com.anas.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class SalesManager {
    private static final String FILE_NAME = "sales_data.txt";

    // 1. Record a new sale (Append to file)
    public static void recordSale(double amount) {
        String record = amount + "\n";
        try {
            Files.write(Paths.get(FILE_NAME), record.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Could not save sale: " + e.getMessage());
        }
    }

    // 2. Calculate Total Revenue (Sum of all lines)
    @SuppressWarnings("CallToPrintStackTrace")
    public static double getTotalRevenue() {
        double total = 0.0;
        try {
            if (!Files.exists(Paths.get(FILE_NAME))) return 0.0;
            
            List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
            for (String line : lines) {
                try {
                    // Clean the string and parse
                    String clean = line.trim();
                    if (!clean.isEmpty()) {
                        total += Double.parseDouble(clean);
                    }
                } catch (NumberFormatException e) {
                    // Ignore bad lines
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total;
    }

    // 3. Count Total Orders (Count of lines)
    public static int getTotalOrders() {
        try {
            if (!Files.exists(Paths.get(FILE_NAME))) return 0;
            return Files.readAllLines(Paths.get(FILE_NAME)).size();
        } catch (IOException e) {
            return 0;
        }
    }

    public static void clearChartData() {
        try {
            // Wipes "item_counts.txt" (Pie Chart, Bar Chart)
            Files.write(Paths.get("item_counts.txt"), new byte[0]); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearRevenueData() {
        try {
            // Wipes "sales_data.txt" (Total Sold, Total Revenue)
            Files.write(Paths.get("sales_data.txt"), new byte[0]); 
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 1. SAVE: Update count when an order is placed
    public static void updateItemCounts(java.util.List<com.anas.models.MenuItem> items) {
        try {
            java.util.Map<String, Integer> counts = new java.util.HashMap<>();
            java.nio.file.Path path = java.nio.file.Paths.get("item_counts.txt");

            // Load existing
            if (java.nio.file.Files.exists(path)) {
                for (String line : java.nio.file.Files.readAllLines(path)) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) counts.put(parts[0], Integer.parseInt(parts[1]));
                }
            }

            // Add new items
            for (com.anas.models.MenuItem item : items) {
                String name = item.getItemName();
                counts.put(name, counts.getOrDefault(name, 0) + 1);
            }

            // Save back
            java.util.List<String> outLines = new java.util.ArrayList<>();
            for (String key : counts.keySet()) outLines.add(key + ":" + counts.get(key));
            java.nio.file.Files.write(path, outLines);

        } catch (java.io.IOException e) { e.printStackTrace(); }
    }

    // 2. LOAD: Get data for Charts
    @SuppressWarnings("UseSpecificCatch")
    public static java.util.Map<String, Integer> getItemCounts() {
        java.util.Map<String, Integer> counts = new java.util.HashMap<>();
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("item_counts.txt");
            if (java.nio.file.Files.exists(path)) {
                for (String line : java.nio.file.Files.readAllLines(path)) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) counts.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
        } catch (Exception e) {}
        return counts;
    }
}