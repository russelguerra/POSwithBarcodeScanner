package com.posbarcodescanner.rjg.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.posbarcodescanner.rjg.Dao.CustomersDao;
import com.posbarcodescanner.rjg.Dao.ItemsDao;
import com.posbarcodescanner.rjg.Dao.SalesHistoryDao;
import com.posbarcodescanner.rjg.Dao.TransactionCustomerDao;
import com.posbarcodescanner.rjg.Dao.TransactionDao;
import com.posbarcodescanner.rjg.Entity.Customers;
import com.posbarcodescanner.rjg.Entity.Items;
import com.posbarcodescanner.rjg.Entity.SalesHistory;
import com.posbarcodescanner.rjg.Entity.TransactionCustomer;
import com.posbarcodescanner.rjg.Entity.Transactions;

@Database(entities = {Customers.class, Items.class, SalesHistory.class, Transactions.class, TransactionCustomer.class}, version = 1)
public abstract class POSdatabase extends RoomDatabase {

    public abstract ItemsDao itemsDao();

    public abstract CustomersDao customersDao();

    public abstract SalesHistoryDao salesHistoryDao();

    public abstract TransactionDao transactionDao();

    public abstract TransactionCustomerDao transactionCustomerDao();

    private static volatile POSdatabase itemsRoomInstance;

    public static POSdatabase getDatabase(Context context) {
        if (itemsRoomInstance == null) {
            synchronized (POSdatabase.class) {
                if (itemsRoomInstance == null) {
                    itemsRoomInstance = Room.databaseBuilder(context.getApplicationContext(),
                            POSdatabase.class, "pos_database")
                            .build();
                }
            }
        }
        return itemsRoomInstance;
    }
}
