package com.posbarcodescanner.rjg.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.posbarcodescanner.rjg.Entity.SalesHistory;

import java.util.List;

@Dao
public interface SalesHistoryDao {
    @Insert
    void insert(SalesHistory a);

    @Query("SELECT * FROM SalesHistory WHERE itemName LIKE :search OR customerName LIKE :search OR dateTime LIKE :search")
    LiveData<List<SalesHistory>> getAllSalesHistory(String search);

    @Query("SElECT * FROM SalesHistory WHERE transactionID LIKE :id")
    LiveData<SalesHistory> getSaleHistory(String id);

    @Update
    void update(SalesHistory a);

    @Delete
    void delete(SalesHistory a);

    @Query("SELECT SUM(quantity) FROM SalesHistory WHERE itemName LIKE :search OR customerName LIKE :search OR dateTime LIKE :search")
    LiveData<Integer> getItemCount(String search);

    @Query("SELECT SUM(totalPrice) FROM SalesHistory WHERE itemName LIKE :search OR customerName LIKE :search OR dateTime LIKE :search")
    LiveData<Double> getTotalPrice(String search);
}
