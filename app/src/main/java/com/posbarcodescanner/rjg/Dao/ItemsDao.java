package com.posbarcodescanner.rjg.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.posbarcodescanner.rjg.Entity.Items;

import java.util.List;

@Dao
public interface ItemsDao {

    @Query("SELECT * FROM Items")
    LiveData<List<Items>> getAllItems();

    @Query("SElECT * FROM Items ORDER BY id DESC")
    LiveData<Items> getItem();

    @Query("SELECT * FROM Items WHERE name LIKE :search OR barcode LIKE :search ORDER BY name")
    LiveData<List<Items>> searchItems(String search);

    @Query("SELECT * FROM Items WHERE barcode LIKE :entry")
    LiveData<List<Items>> checkBarcode(String entry);

    @Query("SELECT * FROM Items WHERE name LIKE :entry")
    LiveData<List<Items>> checkName(String entry);

    @Insert
    void insert(Items items);

    @Update
    void update(Items items);

    @Delete
    void delete(Items items);
}
