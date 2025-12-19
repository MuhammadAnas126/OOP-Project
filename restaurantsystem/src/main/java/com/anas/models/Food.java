
package com.anas.models;

public class Food extends MenuItem {

    public Food(String itemName, double baseCost) {
        super(itemName, baseCost);
    }

    // Food cost does not increase based on any extra condition
    @Override
    public double computeFinalCost() {
        return baseCost;
    }

    @Override
    public String getCategory() {
        return "Regular Food";
    }
}


