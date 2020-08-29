package com.posbarcodescanner.rjg.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Items {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private double price;
    private String dateTime;
    private String barcode;

    public Items(String name, double price, String dateTime, String barcode) {
        this.name = name;
        this.price = price;
        this.dateTime = dateTime;
        this.barcode = barcode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
