package com.example.ettoregalli.shoppingmanager2.database.model;

import java.math.BigDecimal;

public class ListItem {

    private int listId;
    private int itemId;
    private String description;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal amountAdded;
    private BigDecimal totalPrice;
    private String finalDestination;
    private int finalDestinationVisualIndex;

    public ListItem() {
    }

    public BigDecimal getAmountAdded() {
        return amountAdded;
    }

    public void setAmountAdded(BigDecimal amountAdded) {
        this.amountAdded = amountAdded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFinalDestination() {
        return finalDestination;
    }

    public void setFinalDestination(String finalDestination) {
        this.finalDestination = finalDestination;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getFinalDestinationVisualIndex() {
        return finalDestinationVisualIndex;
    }

    public void setFinalDestinationVisualIndex(int finalDestinationVisualIndex) {
        this.finalDestinationVisualIndex = finalDestinationVisualIndex;
    }
}
