package com.posbarcodescanner.rjg.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.posbarcodescanner.rjg.Entity.TransactionCustomer;

@Dao
public interface TransactionCustomerDao {

    @Query("SELECT * FROM TransactionCustomer ORDER BY id DESC")
    LiveData<TransactionCustomer> getCustomer();

    @Insert
    void insert(TransactionCustomer transactionCustomer);

    @Update
    void update(TransactionCustomer transactionCustomer);

    @Delete
    void delete(TransactionCustomer transactionCustomer);

    @Query("DELETE FROM TransactionCustomer")
    void deleteCustomers();
}
