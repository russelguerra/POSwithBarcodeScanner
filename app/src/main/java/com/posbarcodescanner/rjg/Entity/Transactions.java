package com.posbarcodescanner.rjg.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Transactions {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int itemID;
    private String itemName;
    private int quantity;
    private double totalPrice;

    public Transactions(int itemID, String itemName, int quantity, double totalPrice) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
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
