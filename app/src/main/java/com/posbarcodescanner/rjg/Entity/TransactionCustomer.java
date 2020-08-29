package com.posbarcodescanner.rjg.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TransactionCustomer {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int customerID;
    private String name;

    public TransactionCustomer(int customerID, String name) {
        this.customerID = customerID;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
