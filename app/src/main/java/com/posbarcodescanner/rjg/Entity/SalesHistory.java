package com.posbarcodescanner.rjg.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SalesHistory {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String transactionID;
    private String dateTime;
    private int customerID;
    private String customerName;
    private int itemID;
    private String itemName;
    private int quantity;
    private double totalPrice;

    public SalesHistory(String transactionID, String dateTime, int customerID, String customerName, int itemID, String itemName, int quantity, double totalPrice) {
        this.transactionID = transactionID;
        this.dateTime = dateTime;
        this.customerID = customerID;
        this.customerName = customerName;
        this.itemID = itemID;
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
