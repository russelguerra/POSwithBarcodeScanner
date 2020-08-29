package com.posbarcodescanner.rjg.ViewModel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.posbarcodescanner.rjg.Dao.SalesHistoryDao;
import com.posbarcodescanner.rjg.Database.POSdatabase;
import com.posbarcodescanner.rjg.Entity.SalesHistory;

import java.util.List;

public class SalesHistoryViewModel extends AndroidViewModel {
    private static final String TAG = "SalesHistoryViewModel";

    private static SalesHistoryDao sSalesHistoryDao;
    private POSdatabase mPOSdatabase;

    public SalesHistoryViewModel(@NonNull Application application) {
        super(application);

        mPOSdatabase = POSdatabase.getDatabase(application);
        sSalesHistoryDao = mPOSdatabase.salesHistoryDao();
    }

    public LiveData<Integer> getItemCount(String search) {
        return sSalesHistoryDao.getItemCount(search.toLowerCase());
    }

    public LiveData<Double> getTotalPrice(String search) {
        return sSalesHistoryDao.getTotalPrice(search.toLowerCase());
    }

    public LiveData<List<SalesHistory>> getAllSalesHistory(String search) {
        return sSalesHistoryDao.getAllSalesHistory(search.toLowerCase());
    }

    public static void insertSalesHistory(SalesHistory salesHistory) {
        new InsertSalesHistoryAsyncTask(sSalesHistoryDao).execute(salesHistory);
    }

    public static void deleteSalesHistory(SalesHistory salesHistory) {
        new DeleteSalesHistoryAsyncTask(sSalesHistoryDao).execute(salesHistory);
    }

    public static void DeleteSalesHistoryAsyncTask(SalesHistory salesHistory) {
        new DeleteSalesHistoryAsyncTask(sSalesHistoryDao).execute(salesHistory);
    }

    private static class InsertSalesHistoryAsyncTask extends AsyncTask<SalesHistory, Void, Void> {
        SalesHistoryDao asyncTaskDao;

        InsertSalesHistoryAsyncTask(SalesHistoryDao dao) {
            this.asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(SalesHistory... salesHistories) {
            asyncTaskDao.insert(salesHistories[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i(TAG, "onPostExecute: sales history added");
        }
    }


    private static class DeleteSalesHistoryAsyncTask extends AsyncTask<SalesHistory, Void, Void> {
        SalesHistoryDao asyncTaskDao;

        DeleteSalesHistoryAsyncTask(SalesHistoryDao dao) {
            this.asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(SalesHistory... salesHistories) {
            asyncTaskDao.delete(salesHistories[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i(TAG, "onPostExecute: sales history deleted");
        }
    }
}
