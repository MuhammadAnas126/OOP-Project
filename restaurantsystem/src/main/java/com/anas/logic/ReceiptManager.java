package com.anas.logic;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.anas.models.MenuItem;

public class ReceiptManager {

    public static String getReceiptText(OrderCart cart, double rawSubtotal, double discountAmt, String promoCode, double taxAmt, double grandTotal, String payMethod) {
        StringBuilder sb = new StringBuilder();
        
        // --- 1. FORMATTING SETUP ---
        // Header: Item (Left), Qty (Center), Price (Right)
        String headerFmt = " %-30s %5s %10s%n";
        // Item Row: Name, Quantity, Total for that line
        String itemFmt   = " %-30s x%-4d %10.2f%n";
        // Totals: Label, Value
        String totalFmt  = " %-36s %10.2f%n";
        
        String line      = "--------------------------------------------------\n";
        
        // --- 2. HEADER SECTION ---
        sb.append("**************************************************\n");
        sb.append("* TECH BITES                   *\n");
        sb.append("**************************************************\n");

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        sb.append(" Date: ").append(time).append("\n");
        // Simple random order number logic
        sb.append(" Order #: ").append(System.currentTimeMillis() % 10000).append("\n");
        sb.append(line);
        
        // Print Columns Header
        sb.append(String.format(headerFmt, "ITEM", "QTY", "PRICE"));
        sb.append(line);
        
        // We use Maps to count items before printing
        Map<String, Integer> counts = new HashMap<>();
        Map<String, Double> itemUnitPrices = new HashMap<>();

        for (MenuItem item : cart.getList()) {
            String name = item.getItemName();
            counts.put(name, counts.getOrDefault(name, 0) + 1);
            // Store the unit cost to calculate line total later
            itemUnitPrices.put(name, item.computeFinalCost()); 
        }

        // --- 4. PRINT ITEMS ---
        for (String name : counts.keySet()) {
            int qty = counts.get(name);
            double unitPrice = itemUnitPrices.get(name);
            double lineTotal = unitPrice * qty;

            // Truncate long names to fit in the 20-char column
            String displayName = name;
            if (displayName.length() > 28) {
                displayName = displayName.substring(0, 26) + "..";
            }
            
            sb.append(String.format(itemFmt, displayName, qty, lineTotal));
        }
        
        sb.append(line);
        
        // --- 5. FINANCIAL TOTALS ---
        sb.append(String.format(totalFmt, "Subtotal:", rawSubtotal));
        
        if (discountAmt > 0) {
            // Show discount as a negative number
            sb.append(String.format(totalFmt, "Discount (" + promoCode + "):", -discountAmt));
        }

        String taxLabel = "Tax (" + (payMethod.equals("Card") ? "5%" : "15%") + "):";
        sb.append(String.format(totalFmt, taxLabel, taxAmt));
        
        sb.append("==================================================\n");
        sb.append(String.format(totalFmt, "GRAND TOTAL:", grandTotal));
        sb.append("==================================================\n");
        sb.append(" Paid via: ").append(payMethod).append("\n");
        sb.append("\n          THANK YOU FOR ORDERING!       \n");
        sb.append("**************************************************\n");

        // --- 6. SAVE FILE SILENTLY ---
        try (PrintWriter writer = new PrintWriter(new FileWriter("LastReceipt.txt"))) {
            writer.print(sb.toString());
        } catch (Exception e) {
            // Ignore errors for receipt saving
        }

        return sb.toString();
    }
}