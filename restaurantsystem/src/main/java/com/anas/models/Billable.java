package com.anas.models;

public interface Billable {

    // Standard tax applied on cash only
    double TAX_RATE_CASH = 0.15;

    // On card
    double TAX_RATE_CARD = 0.05; // 5%

    // Discount Rate (30%)
    double PROMO_DISCOUNT_RATE = 0.30;

    // Calculates the final price of the item
    double computeFinalCost();
}

