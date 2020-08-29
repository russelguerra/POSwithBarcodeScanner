package com.posbarcodescanner.rjg.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.posbarcodescanner.rjg.Entity.Transactions;

import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insert(Transactions a);

    @Query("SELECT * FROM TRANSACTIONS WHERE itemID LIKE :id")
    LiveData<Transactions> checkItemID(int id);

    @Query("SELECT * FROM Transactions ORDER BY id DESC")
    LiveData<List<Transactions>> getAllItemsInTransaction();

    @Query("SELECT SUM(quantity) FROM Transactions")
    LiveData<Integer> getItemCount();

    @Query("SELECT SUM(totalPrice) FROM Transactions")
    LiveData<Double> getTotalPrice();

    @Query("DELETE FROM Transactions")
    void deleteWholeTransaction();

    @Query("DELETE FROM Transactions WHERE itemID = :id")
    void deleteAllItemID(int id);

    @Update
    void update(Transactions a);

    @Delete
    void delete(Transactions a);
}
