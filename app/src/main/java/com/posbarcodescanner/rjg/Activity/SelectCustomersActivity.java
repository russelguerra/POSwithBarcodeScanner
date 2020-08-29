package com.posbarcodescanner.rjg.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.posbarcodescanner.rjg.Adapter.CustomersAdapter;
import com.posbarcodescanner.rjg.Entity.Customers;
import com.posbarcodescanner.rjg.R;
import com.posbarcodescanner.rjg.ViewModel.SelectCustomerModel;

import java.util.List;

import static com.posbarcodescanner.rjg.Activity.NewCustomerActivity.NEW_NAME;

public class SelectCustomersActivity extends AppCompatActivity {
    private static final String TAG = "SelectCustomersActivity";
    private static final int NEW_CUSTOMER = 100;
    public static final int NEW_CUSTOMER_NAME = 105;

    private SelectCustomerModel model;
    private RecyclerView rv;
    private CustomersAdapter customerAdapter;
    public static List<Customers> checkCustomerNames;
    Customers customer;

    private String search = "%%";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_customers);

        setTitle("List of Customers");

        model = new ViewModelProvider(this).get(SelectCustomerModel.class);

        customerAdapter = new CustomersAdapter(this);
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        rv.setAdapter(customerAdapter);

        getAllCustomers(search);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_customer, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                customerAdapter.filterList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getAllCustomers("%" + newText + "%");
                return false;
            }
        });

        return true;
    }

    private void getAllCustomers(String search) {
        model.getAllCustomers("%" + search + "%").observe(SelectCustomersActivity.this, new Observer<List<Customers>>() {
            @Override
            public void onChanged(List<Customers> customers) {
                customerAdapter.setList(customers);
                checkCustomerNames = customers;
            }
        });

        customerAdapter.setOnClickNameListener(new CustomersAdapter.OnClickNameListener() {
            @Override
            public void onClickNameListener(Customers customer) {
                Intent intent = new Intent();
                intent.putExtra("CUSTOMER_ID", customer.getId());
                intent.putExtra("CUSTOMER_NAME", customer.getName());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        customerAdapter.setOnUpdateListener(new CustomersAdapter.OnUpdateListener() {
            @Override
            public void onUpdateListener(final Customers customers) {
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.dialog_update_customer_name, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectCustomersActivity.this)
                        .setView(v);
                final EditText etName = v.findViewById(R.id.etName);
                final Button btOkay = v.findViewById(R.id.btOkay);
                final TextView tWarning = v.findViewById(R.id.tWarning);
                TextView text = v.findViewById(R.id.text);
                final AlertDialog dialog = builder.create();
                dialog.show();

                text.setText("Update Customer Name: " + customers.getName());
                etName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (etName.getText().toString().trim().isEmpty()) {
                            btOkay.setEnabled(false);
                        } else {
                            model.checkCustomer(etName.getText().toString().trim())
                                    .observe(SelectCustomersActivity.this, new Observer<List<Customers>>() {
                                        @Override
                                        public void onChanged(List<Customers> customers) {
                                            if (customers.size() > 0) {
                                                tWarning.setVisibility(View.VISIBLE);
                                                btOkay.setEnabled(false);
                                            } else {
                                                tWarning.setVisibility(View.INVISIBLE);
                                                btOkay.setEnabled(true);
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                btOkay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(SelectCustomersActivity.this, "Customre name updated", Toast.LENGTH_SHORT).show();
                        Customers customers1 = new Customers(etName.getText().toString().trim());
                        customers1.setId(customers.getId());
                        model.updateCustomer(customers1);
                        dialog.dismiss();
                    }
                });
            }
        });

        customerAdapter.setOnDeleteListener(new CustomersAdapter.OnDeleteListener() {
            @Override
            public void onDeleteListener(final Customers customers) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectCustomersActivity.this)
                        .setMessage("Delete customer '" + customers.getName() + "'?")
                        .setTitle("Warning! Deleting customer...")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SelectCustomerModel.deleteCustomer(customers);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.i(TAG, "onClick: Did not delete customer");
                            }
                        })
                        .setCancelable(true);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.dialog_new_customer, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectCustomersActivity.this)
                        .setView(view);

                final TextView tWarning = view.findViewById(R.id.tWarning);
                final Button btOkay = view.findViewById(R.id.btOkay);
                final EditText etName = view.findViewById(R.id.etName);

                btOkay.setEnabled(false);

                final AlertDialog dialog = builder.create();
                dialog.show();

                etName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (etName.getText().toString().trim().isEmpty()) {
                            btOkay.setEnabled(false);
                        } else {
                            model.checkCustomer(etName.getText().toString().trim())
                                    .observe(SelectCustomersActivity.this, new Observer<List<Customers>>() {
                                        @Override
                                        public void onChanged(List<Customers> customers) {
                                            if (customers.size() > 0) {
                                                tWarning.setVisibility(View.VISIBLE);
                                                btOkay.setEnabled(false);
                                            } else {
                                                tWarning.setVisibility(View.INVISIBLE);
                                                btOkay.setEnabled(true);
                                            }
                                        }
                                    });
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                btOkay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (etName.getText().toString().trim().isEmpty()) {
                            Toast.makeText(SelectCustomersActivity.this, "Need to input name", Toast.LENGTH_SHORT).show();
                        } else {
                            model.insertCustomer(new Customers(etName.getText().toString().trim()));
                            Toast.makeText(SelectCustomersActivity.this, "Customer added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    }
                });

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_CUSTOMER && resultCode == RESULT_OK) {
            Customers customer = new Customers(data.getStringExtra(NEW_NAME));
            model.insertCustomer(customer);
        } else {
            Log.i(TAG, "onActivityResult: Customer not added");
        }
    }

    @Override
    public boolean onNavigateUp() {
        return super.onNavigateUp();
    }
}
