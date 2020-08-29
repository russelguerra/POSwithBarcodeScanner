package com.posbarcodescanner.rjg.ViewModel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.posbarcodescanner.rjg.Dao.TransactionCustomerDao;
import com.posbarcodescanner.rjg.Database.POSdatabase;
import com.posbarcodescanner.rjg.Entity.TransactionCustomer;

public class TransactCustomerViewModel extends AndroidViewModel {
    private static final String TAG = "TransactCustomerViewMod";

    private static TransactionCustomerDao transactionCustomerDao;
    private POSdatabase mPOSdatabase;
    private LiveData<TransactionCustomer> mTransactionCustomerLiveData;

    public TransactCustomerViewModel(@NonNull Application application) {
        super(application);

        mPOSdatabase = POSdatabase.getDatabase(application);
        transactionCustomerDao = mPOSdatabase.transactionCustomerDao();
    }

    public LiveData<TransactionCustomer> getCustomer() {
        return transactionCustomerDao.getCustomer();
    }

    public static void insertCustomer(TransactionCustomer transactionCustomer) {
        new InsertAsyncTask(transactionCustomerDao).execute(transactionCustomer);
    }

    public static void updateCustomer(TransactionCustomer transactionCustomer) {
        new UpdateAsyncTask(transactionCustomerDao).execute(transactionCustomer);
    }

    public static void deleteCustomer(TransactionCustomer transactionCustomer) {
        new DeleteAsyncTask(transactionCustomerDao).execute(transactionCustomer);
    }

    public static void deleteCustomers() {
        new DeleteCustomersAsyncTask(transactionCustomerDao).execute();
    }

    private static class DeleteCustomersAsyncTask extends AsyncTask<Void, Void, Void> {
        TransactionCustomerDao deleteCustomerDao;

        DeleteCustomersAsyncTask(TransactionCustomerDao dao) {
            this.deleteCustomerDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            deleteCustomerDao.deleteCustomers();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i(TAG, "onPostExecute: Customers deleted");
        }
    }

    private static class OperationsAsyncTask extends AsyncTask<TransactionCustomer, Void, Void> {
        TransactionCustomerDao asyncTaskDao;

        OperationsAsyncTask(TransactionCustomerDao dao) {
            this.asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(TransactionCustomer... transactionCustomers) {
            return null;
        }
    }

    private static class InsertAsyncTask extends OperationsAsyncTask {

        public InsertAsyncTask(TransactionCustomerDao dao) {
            super(dao);
        }

        @Override
        protected Void doInBackground(TransactionCustomer... transactionCustomers) {
            transactionCustomerDao.insert(transactionCustomers[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i(TAG, "onPostExecute: Customer added to transact.");
        }
    }

    private static class UpdateAsyncTask extends OperationsAsyncTask {

        public UpdateAsyncTask(TransactionCustomerDao dao) {
            super(dao);
        }

        @Override
        protected Void doInBackground(TransactionCustomer... transactionCustomers) {
            transactionCustomerDao.update(transactionCustomers[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i(TAG, "onPostExecute: Customer updated to transact.");
        }
    }

    private static class DeleteAsyncTask extends OperationsAsyncTask {

        public DeleteAsyncTask(TransactionCustomerDao dao) {
            super(dao);
        }

        @Override
        protected Void doInBackground(TransactionCustomer... transactionCustomers) {
            transactionCustomerDao.delete(transactionCustomers[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i(TAG, "onPostExecute: Customer deleted to transact.");
        }
    }
}
