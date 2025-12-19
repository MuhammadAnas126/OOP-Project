package com.anas.models;
// Base class for every product in the restaurant menu

import java.io.Serializable;

public abstract class MenuItem implements Billable, Serializable {
    private static final long serialVersionUID = 1L;
    private boolean isAvailable = true;
    protected String itemName;
    protected double baseCost;

    public MenuItem(String itemName, double baseCost) {
        this.itemName = itemName;
        this.baseCost = baseCost;
    }

    public String getItemName() {
        return itemName;
    }

    public double getBaseCost() {
        return baseCost;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    // Child classes must define their category
    public abstract String getCategory();

    public String getStatusString() {
    return isAvailable ? "In Stock" : "Out of Stock";
}

    @Override
    public String toString() {
        return itemName + " - " + baseCost;
    }
}