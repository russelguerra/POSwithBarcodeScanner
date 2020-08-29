package com.posbarcodescanner.rjg.Activity;

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

import com.google.android.material.textfield.TextInputEditText;
import com.posbarcodescanner.rjg.Adapter.ItemsAdapter;
import com.posbarcodescanner.rjg.Entity.Items;
import com.posbarcodescanner.rjg.Entity.Transactions;
import com.posbarcodescanner.rjg.R;
import com.posbarcodescanner.rjg.ViewModel.ItemsViewModel;
import com.posbarcodescanner.rjg.ViewModel.TransactionsViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.posbarcodescanner.rjg.Activity.UpdateItemActivity.ITEM_BARCODE;
import static com.posbarcodescanner.rjg.Activity.UpdateItemActivity.ITEM_DATE_TIME;
import static com.posbarcodescanner.rjg.Activity.UpdateItemActivity.ITEM_ID;
import static com.posbarcodescanner.rjg.Activity.UpdateItemActivity.ITEM_NAME;
import static com.posbarcodescanner.rjg.Activity.UpdateItemActivity.ITEM_PRICE;

public class SelectItemsActivity extends AppCompatActivity {
    private static final String TAG = "SelectItemsActivity";
    public static final int UPDATE_ITEM = 201;

    private RecyclerView rv;
    private ItemsAdapter itemsAdapter;
    private ItemsViewModel model;
    private TransactionsViewModel mTransactionsViewModel;

    private String search = "", barcode = "";

    private SimpleDateFormat sdf;
    private String dateTime;
    private double totalPrice = 0.0, price = 0.0;
    private int quantity = 0;
    private boolean barcodeExists = false, nameExists = false, noPrice = true, isScan = false;

    private Bundle bundle;
    private AlertDialog dialog1, dialog2;
    private int updateCounter = 0;

    private List<Items> listItems = new ArrayList<>();
    private int scanBarcodeCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_items);
        setTitle("List of Items");

        model = new ViewModelProvider(this).get(ItemsViewModel.class);
        mTransactionsViewModel = new ViewModelProvider(this).get(TransactionsViewModel.class);

        itemsAdapter = new ItemsAdapter(this);
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        rv.setAdapter(itemsAdapter);

        getItems(search);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            barcode = bundle.getString("barcode");
            isScan = bundle.getBoolean("is_scan");

            model.checkBarcode(barcode).observe(this, new Observer<List<Items>>() {
                @Override
                public void onChanged(List<Items> items) {
                    if (scanBarcodeCounter == 0) {
                        scanBarcodeCounter = 1;

                        if (items.size() > 0) {
                            Log.i(TAG, "onCreate: isExists");
                            itemExists(items.get(0));
                        } else {
                            Log.i(TAG, "onCreate: Not exists");
                            addItem(isScan);
                        }
                    }
                }
            });
            bundle = null;
        }
    }

    private void getItems(String search) {
        model.searchItems(search).observe(SelectItemsActivity.this, new Observer<List<Items>>() {
            @Override
            public void onChanged(List<Items> items) {
                itemsAdapter.setItems(items);
                listItems = items;
            }
        });

        itemsAdapter.setOnClickListener(new ItemsAdapter.OnClickListener() {
            @Override
            public void onClickListener(final Items item) {
                itemExists(item);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_item, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getItems(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                addItem(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_ITEM && resultCode == RESULT_OK) {
            int id = data.getIntExtra(ITEM_ID, 0);
            String name = data.getStringExtra(ITEM_NAME);
            String barcode = data.getStringExtra(ITEM_BARCODE);
            String dateTime = data.getStringExtra(ITEM_DATE_TIME);
            double price = data.getDoubleExtra(ITEM_PRICE, 0);

            Items item = new Items(name, price, dateTime, barcode);
            item.setId(id);

            mTransactionsViewModel.deleteAllItemID(id);
            model.updateItem(item);
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "onActivityResult: Not added");
        }
    }

    private void addItem(final boolean isScan) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_new_item, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectItemsActivity.this)
                .setView(view);

        final TextInputEditText etBarcode = view.findViewById(R.id.etBarcode);
        final TextInputEditText etName = view.findViewById(R.id.etName);
        final TextInputEditText etPrice = view.findViewById(R.id.etPrice);
        final TextView tWarning = view.findViewById(R.id.tWarning);
        final Button btOkay = view.findViewById(R.id.btOkay);

        final AlertDialog dialog = builder.create();

        if (isScan) {
            etBarcode.setText(barcode);
        }

        etBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                model.checkBarcode(etBarcode.getText().toString().trim()).observe(SelectItemsActivity.this,
                        new Observer<List<Items>>() {
                            @Override
                            public void onChanged(List<Items> items) {
                                if (etBarcode.getText().toString().trim().isEmpty()) {
                                    barcodeExists = false;
                                    if (nameExists) {
                                        tWarning.setText("Name already exists.");
                                        tWarning.setVisibility(View.VISIBLE);
                                        btOkay.setEnabled(false);
                                    } else {
                                        tWarning.setVisibility(View.INVISIBLE);
                                        btOkay.setEnabled(true);
                                    }
                                } else {
                                    if (items.size() > 0) {
                                        barcodeExists = true;
                                        if (items.get(0).getBarcode().isEmpty()) {
                                            tWarning.setVisibility(View.INVISIBLE);
                                            btOkay.setEnabled(true);
                                        } else {
                                            tWarning.setText("Barcode already exists.");
                                            tWarning.setVisibility(View.VISIBLE);
                                            btOkay.setEnabled(false);
                                        }
                                    } else {
                                        barcodeExists = false;
                                        if (nameExists) {
                                            tWarning.setText("Name already exists.");
                                            tWarning.setVisibility(View.VISIBLE);
                                            btOkay.setEnabled(false);
                                        } else {
                                            tWarning.setVisibility(View.INVISIBLE);
                                            btOkay.setEnabled(true);
                                        }
                                    }
                                }


                            }
                        });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                model.checkName(etName.getText().toString().trim().toLowerCase()).observe(SelectItemsActivity.this,
                        new Observer<List<Items>>() {
                            @Override
                            public void onChanged(List<Items> items) {

                                if (items.size() > 0) {
                                    nameExists = true;
                                    tWarning.setText("Name already exists.");
                                    tWarning.setVisibility(View.VISIBLE);
                                    btOkay.setEnabled(false);
                                } else {
                                    nameExists = false;
                                    if (barcodeExists) {
                                        tWarning.setText("Barcode already exists.");
                                        tWarning.setVisibility(View.VISIBLE);
                                        btOkay.setEnabled(false);
                                    } else {
                                        tWarning.setVisibility(View.INVISIBLE);
                                        btOkay.setEnabled(true);
                                    }
                                }

                            }
                        });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etName.getText().toString().isEmpty() || etPrice.getText().toString().isEmpty()) {

                    tWarning.setText("Name and price are required.");
                    tWarning.setVisibility(View.VISIBLE);
                } else {
                    tWarning.setVisibility(View.INVISIBLE);

                    sdf = new SimpleDateFormat("MM.dd.yyyy/hh:mmaaa", Locale.getDefault());
                    dateTime = sdf.format(new Date());

                    String name = etName.getText().toString().trim();
                    double price = Double.parseDouble(etPrice.getText().toString().trim());
                    String barcode = etBarcode.getText().toString().trim();

                    model.insertItem(new Items(name, price, dateTime, barcode));

                    dialog.dismiss();

                    if (isScan) {
                        model.getItem().observe(SelectItemsActivity.this, new Observer<Items>() {
                            @Override
                            public void onChanged(Items items) {
                                itemExists(items);
                                Log.i(TAG, "onChanged: get Item");
                            }
                        });
                    }

                    Toast.makeText(SelectItemsActivity.this, "Item added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void itemExists(final Items item) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_item_quantity, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectItemsActivity.this)
                .setView(view);

        TextView tName = view.findViewById(R.id.tName);
        TextView tBarcode = view.findViewById(R.id.tBarcode);
        final TextView tPrice = view.findViewById(R.id.tPrice);
        final TextView tTotalPrice = view.findViewById(R.id.tTotalPrice);
        final EditText etQuantity = view.findViewById(R.id.etQuantity);
        final Button btOkay = view.findViewById(R.id.btOkay);
        final Button btUpdate = view.findViewById(R.id.btUpdate);
        final Button btDelete = view.findViewById(R.id.btDelete);

        final AlertDialog dialog = builder.create();
        dialog.show();

        tName.setText(item.getName());
        tBarcode.setText("Barcode: " + item.getBarcode());
        tPrice.setText(String.valueOf(item.getPrice()));
        btOkay.setEnabled(false);

        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etQuantity.getText().toString().trim().isEmpty() || etQuantity.getText().toString().trim().equals("0")) {
                    quantity = 0;
                    btOkay.setEnabled(false);
                } else {
                    quantity = Integer.parseInt(etQuantity.getText().toString().trim());
                    btOkay.setEnabled(true);
                }

                totalPrice = quantity * item.getPrice();
                tTotalPrice.setText(String.valueOf(totalPrice));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("item_id", item.getId())
                        .putExtra("item_name", item.getName())
                        .putExtra("item_quantity", quantity)
                        .putExtra("total_price", totalPrice);
                setResult(RESULT_OK, intent);

                if (updateCounter == 1) {
                    dialog1.dismiss();
                    dialog2.dismiss();
                }
                dialog.dismiss();
                finish();
            }
        });

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTransactionsViewModel.checkItemId(item.getId()).observe(SelectItemsActivity.this, new Observer<Transactions>() {
                    @Override
                    public void onChanged(Transactions transactions) {
                        if (transactions != null) {
                            LayoutInflater inflater = getLayoutInflater();
                            View v = inflater.inflate(R.layout.dialog_item_detected_in_cart, null);
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(SelectItemsActivity.this)
                                    .setView(v);
                            dialog1 = builder1.create();
                            dialog.dismiss();
                            dialog1.show();
                            updateCounter = 1;

                            Button btOkay = v.findViewById(R.id.btOkay);

                            btOkay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog1.dismiss();
                                }
                            });
                        } else {
                            Intent intent = new Intent(SelectItemsActivity.this, UpdateItemActivity.class);
                            intent.putExtra("item_id", item.getId());
                            intent.putExtra("item_name", item.getName());
                            intent.putExtra("item_barcode", item.getBarcode());
                            intent.putExtra("item_price", item.getPrice());
                            startActivityForResult(intent, UPDATE_ITEM);
                            dialog.dismiss();
                        }
                    }
                });

            }
        });

        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTransactionsViewModel.checkItemId(item.getId()).observe(SelectItemsActivity.this, new Observer<Transactions>() {
                    @Override
                    public void onChanged(Transactions transactions) {
                        dialog.dismiss();
                        if (transactions != null) {
                            LayoutInflater inflater = getLayoutInflater();
                            View v = inflater.inflate(R.layout.dialog_item_detected_in_cart, null);
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(SelectItemsActivity.this)
                                    .setView(v);
                            dialog1 = builder1.create();
                            dialog1.show();
                            updateCounter = 1;

                            Button btOkay = v.findViewById(R.id.btOkay);
                            TextView text = v.findViewById(R.id.text);

                            text.setText("Unable to delete item that is already in the cart.");

                            btOkay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog1.dismiss();
                                }
                            });
                        } else {
                            LayoutInflater inflater = getLayoutInflater();
                            View v = inflater.inflate(R.layout.dialog_delete_item_from_list, null);
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(SelectItemsActivity.this)
                                    .setView(v);
                            dialog2 = builder2.create();
                            dialog2.show();

                            Button btOkay = v.findViewById(R.id.btOkay);
                            TextView tName = v.findViewById(R.id.tName);
                            TextView tPrice = v.findViewById(R.id.tPrice);

                            tName.setText("Item Name: " + item.getName());
                            tPrice.setText("Price: " + item.getPrice());

                            btOkay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    model.deleteItem(item);

                                    dialog2.dismiss();
                                    Toast.makeText(SelectItemsActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
