package com.posbarcodescanner.rjg.ViewModel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.posbarcodescanner.rjg.Dao.TransactionDao;
import com.posbarcodescanner.rjg.Database.POSdatabase;
import com.posbarcodescanner.rjg.Entity.Transactions;

import java.util.List;

public class TransactionsViewModel extends AndroidViewModel {
    private static final String TAG = "TransactionsViewModel";

    private static TransactionDao transactionDao;
    private POSdatabase posDatabase;
    private LiveData<List<Transactions>> list;

    public TransactionsViewModel(@NonNull Application application) {
        super(application);

        posDatabase = POSdatabase.getDatabase(application);
        transactionDao = posDatabase.transactionDao();
        list = transactionDao.getAllItemsInTransaction();
    }

    public LiveData<List<Transactions>> getAllItemsInTransaction() {
        return list;
    }

    public LiveData<Integer> getItemCount() {
        return transactionDao.getItemCount();
    }

    public LiveData<Double> getTotalPrice() {
        return transactionDao.getTotalPrice();
    }

    public static void insertItem(Transactions transactions) {
        new TransactOnly.InsertAsyncTask(transactionDao).execute(transactions);
    }

    public static void updateItem(Transactions transactions) {
        new TransactOnly.UpdateAsyncTask(transactionDao).execute(transactions);
    }

    public static void deleteItem(Transactions transactions) {
        new TransactOnly.DeleteAsyncTask(transactionDao).execute(transactions);
    }

    public static void deleteWholeTransaction() {
        new DeleteWholeTransactionAsyncTask(transactionDao).execute();
    }

    public LiveData<Transactions> checkItemId(int id) {
        return transactionDao.checkItemID(id);
    }

    public static void deleteAllItemID(int id) {
        new DeleteAllItemId(transactionDao).execute(id);
    }

    private static class DeleteAllItemId extends AsyncTask<Integer, Void, Void> {
        TransactionDao mTransactionDao;

        DeleteAllItemId(TransactionDao dao) {
            this.mTransactionDao = dao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            mTransactionDao.deleteAllItemID(integers[0]);
            return null;
        }
    }

    private static class DeleteWholeTransactionAsyncTask extends AsyncTask<Void, Void, Void> {

        TransactionDao mTransactionDao;

        DeleteWholeTransactionAsyncTask(TransactionDao dao) {
            this.mTransactionDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            transactionDao.deleteWholeTransaction();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i(TAG, "onPostExecute: Whole transaction deleted");
        }
    }

    private static class TransactOnly {
        private static class OperationsAsyncTask extends AsyncTask<Transactions, Void, Void> {

            TransactionDao asyncTaskDao;

            OperationsAsyncTask(TransactionDao dao) {
                this.asyncTaskDao = dao;
            }

            @Override
            protected Void doInBackground(Transactions... transactions) {
                return null;
            }
        }

        private static class InsertAsyncTask extends OperationsAsyncTask {

            public InsertAsyncTask(TransactionDao transactionDao) {
                super(transactionDao);
            }

            @Override
            protected Void doInBackground(Transactions... transactions) {
                transactionDao.insert(transactions[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.i(TAG, "onActivityResult: item added");
            }
        }

        private static class UpdateAsyncTask extends OperationsAsyncTask {

            public UpdateAsyncTask(TransactionDao transactionDao) {
                super(transactionDao);
            }

            @Override
            protected Void doInBackground(Transactions... transactions) {
                transactionDao.update(transactions[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.i(TAG, "onActivityResult: item updated");
            }
        }

        private static class DeleteAsyncTask extends OperationsAsyncTask {

            public DeleteAsyncTask(TransactionDao transactionDao) {
                super(transactionDao);
            }

            @Override
            protected Void doInBackground(Transactions... transactions) {
                transactionDao.delete(transactions[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.i(TAG, "onActivityResult: item deleted");
            }
        }
    }
}
