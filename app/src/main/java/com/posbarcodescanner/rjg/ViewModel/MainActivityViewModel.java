package com.posbarcodescanner.rjg.ViewModel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.posbarcodescanner.rjg.Dao.ItemsDao;
import com.posbarcodescanner.rjg.Database.POSdatabase;
import com.posbarcodescanner.rjg.Entity.Items;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {
    private static final String TAG = "MainActivityViewModel";

    private static ItemsDao itemsDao;
    private POSdatabase posDatabase;
    private LiveData<List<Items>> allItems;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);

        posDatabase = POSdatabase.getDatabase(application);
        itemsDao = posDatabase.itemsDao();
        allItems = itemsDao.getAllItems();
    }

    public static void insertItem(Items items) {
        new ItemsOnly.InsertAsyncTask(itemsDao).execute(items);
    }

    public LiveData<List<Items>> getAllItems() {
        return allItems;
    }

    public static void updateItem(Items items) {
        new ItemsOnly.UpdateAsyncTask(itemsDao).execute(items);
    }

    public static void deleteItem(Items items) {
        new ItemsOnly.DeleteAsyncTask(itemsDao).execute(items);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.i(TAG, "onCleared: ViewModel Cleared");
    }

    private static class ItemsOnly {
        private static class OperationsAsyncTask extends AsyncTask<Items, Void, Void> {

            ItemsDao asyncTaskDao;

            OperationsAsyncTask(ItemsDao dao) {
                this.asyncTaskDao = dao;
            }

            @Override
            protected Void doInBackground(Items... items) {
                return null;
            }
        }

        private static class InsertAsyncTask extends OperationsAsyncTask {

            public InsertAsyncTask(ItemsDao itemsDao) {
                super(itemsDao);
            }

            @Override
            protected Void doInBackground(Items... items) {
                itemsDao.insert(items[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.i(TAG, "onActivityResult: item added");
            }
        }

        private static class UpdateAsyncTask extends OperationsAsyncTask {

            public UpdateAsyncTask(ItemsDao itemsDao) {
                super(itemsDao);
            }

            @Override
            protected Void doInBackground(Items... items) {
                itemsDao.update(items[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.i(TAG, "onPostExecute: item updated");
            }
        }

        private static class DeleteAsyncTask extends OperationsAsyncTask {

            public DeleteAsyncTask(ItemsDao itemsDao) {
                super(itemsDao);
            }

            @Override
            protected Void doInBackground(Items... items) {
                itemsDao.delete(items[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.i(TAG, "onPostExecute: item deleted");
            }
        }
    }
}
