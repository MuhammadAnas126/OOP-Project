
package com.anas.logic;
import java.util.ArrayList;

import com.anas.models.Billable;
import com.anas.models.MenuItem;

public class OrderCart {

    private final ArrayList<MenuItem> cartItems;

    public OrderCart() {
        cartItems = new ArrayList<>();
    }

    public void addItem(MenuItem item) {
        cartItems.add(item);
    }

    public void removeItem(MenuItem item) {
        cartItems.remove(item);
    }

    public void clearCart() {
        cartItems.clear();
    }

    // Convenience alias used by UI code
    public void clear() {
        clearCart();
    }

    public double getSubTotal() {
        double sum = 0;
        for (MenuItem item : cartItems) {
            sum += item.computeFinalCost(); // polymorphism call
        }
        return sum;
    }

    public double getTax() {
        return getSubTotal() * Billable.TAX_RATE_CARD;
    }

    public double getGrandTotal() {
        return getSubTotal() + getTax();
    }

    // Expose a copy of internal list for UI use
    public java.util.ArrayList<MenuItem> getList() {
        return new java.util.ArrayList<>(cartItems);
    }
}
