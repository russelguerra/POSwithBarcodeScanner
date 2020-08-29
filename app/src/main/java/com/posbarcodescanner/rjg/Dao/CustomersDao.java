package com.posbarcodescanner.rjg.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.posbarcodescanner.rjg.Entity.Customers;

import java.util.List;

@Dao
public interface CustomersDao {
    @Insert
    void insert(Customers a);

    @Query("SELECT * FROM Customers WHERE name LIKE :search ORDER BY name")
    LiveData<List<Customers>> getAllCustomers(String search);

    @Query("SELECT * FROM Customers WHERE id = :id")
    LiveData<Customers> getCustomer(int id);

    @Query("SELECT * FROM Customers WHERE name LIKE :name")
    LiveData<List<Customers>> checkCustomer(String name);

    @Update
    void update(Customers a);

    @Delete
    void delete(Customers a);
}
