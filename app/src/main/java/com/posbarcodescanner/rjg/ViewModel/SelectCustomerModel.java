package com.posbarcodescanner.rjg.ViewModel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.posbarcodescanner.rjg.Dao.CustomersDao;
import com.posbarcodescanner.rjg.Database.POSdatabase;
import com.posbarcodescanner.rjg.Entity.Customers;

import java.util.List;

public class SelectCustomerModel extends AndroidViewModel {
    private static final String TAG = "SelectCustomerModel";

    private static CustomersDao customersDao;
    private POSdatabase posDatabase;
    private LiveData<List<Customers>> allCustomers;

    public SelectCustomerModel(@NonNull Application application) {
        super(application);

        posDatabase = POSdatabase.getDatabase(application);
        customersDao = posDatabase.customersDao();
    }

    public static void insertCustomer(Customers customer) {
        new InsertAsyncTask(customersDao).execute(customer);
    }

    public static void updateCustomer(Customers customer) {
        new UpdateAsyncTask(customersDao).execute(customer);
    }

    public static void deleteCustomer(Customers customer) {
        new DeleteAsyncTask(customersDao).execute(customer);
    }

    public LiveData<List<Customers>> getAllCustomers(String search) {
        return customersDao.getAllCustomers(search.toLowerCase());
    }

    public LiveData<Customers> getCustomer(int id) {
        return customersDao.getCustomer(id);
    }

    public LiveData<List<Customers>> checkCustomer(String name) {
        return customersDao.checkCustomer(name.toLowerCase());
    }

    private static class OperationsAsyncTask extends AsyncTask<Customers, Void, Void> {

        CustomersDao asyncTaskDao;

        OperationsAsyncTask(CustomersDao dao) {
            this.asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Customers... customers) {
            return null;
        }
    }

    private static class InsertAsyncTask extends OperationsAsyncTask {

        public InsertAsyncTask(CustomersDao customersDao) {
            super(customersDao);
        }

        @Override
        protected Void doInBackground(Customers... customers) {
            customersDao.insert(customers[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i(TAG, "onActivityResult: customer added");
        }
    }

    private static class UpdateAsyncTask extends OperationsAsyncTask {

        public UpdateAsyncTask(CustomersDao customersDao) {
            super(customersDao);
        }

        @Override
        protected Void doInBackground(Customers... customers) {
            customersDao.update(customers[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i(TAG, "onActivityResult: customer updated");
        }
    }

    private static class DeleteAsyncTask extends OperationsAsyncTask {

        public DeleteAsyncTask(CustomersDao customersDao) {
            super(customersDao);
        }

        @Override
        protected Void doInBackground(Customers... customers) {
            customersDao.delete(customers[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i(TAG, "onActivityResult: customer deleted");
        }
    }

}
