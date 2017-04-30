package com.example.ettoregalli.shoppingmanager2.database.model;

import java.math.BigDecimal;

public class ListSubtotal {

    private String finalDestination;
    private BigDecimal totalPrice;
    private int finalDestinationVisualIndex;

    public ListSubtotal() {
    }

    public String getFinalDestination() {
        return finalDestination;
    }

    public void setFinalDestination(String finalDestination) {
        this.finalDestination = finalDestination;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getFinalDestinationVisualIndex() {
        return finalDestinationVisualIndex;
    }

    public void setFinalDestinationVisualIndex(int finalDestinationVisualIndex) {
        this.finalDestinationVisualIndex = finalDestinationVisualIndex;
    }
}
