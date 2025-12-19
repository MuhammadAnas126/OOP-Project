
package com.anas.models;

// Represents any drink with size-based pricing
public class Drink extends MenuItem {

    public Drink(String itemName, double baseCost) {
        super(itemName, baseCost);
    }

    // Convenience constructor for UI/tests
    public Drink(String itemName, double baseCost, String cupSize) {
        this(itemName, baseCost);
    }

    @Override
    public String getCategory() {
        return "Drink";
    }

    @Override
    public double computeFinalCost() {
            return baseCost;
    }
}