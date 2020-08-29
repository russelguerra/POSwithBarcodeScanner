package com.posbarcodescanner.rjg.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.posbarcodescanner.rjg.Entity.Items;
import com.posbarcodescanner.rjg.R;
import com.posbarcodescanner.rjg.ViewModel.ItemsViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpdateItemActivity extends AppCompatActivity {
    private static final String TAG = "NewItemActivity";

    public static final String ITEM_NAME = "item_name";
    public static final String ITEM_PRICE = "item_price";
    public static final String ITEM_DATE_TIME = "item_date_time";
    public static final String ITEM_BARCODE = "item_barcode";
    public static final String ITEM_ID = "ITEM_ID";

    private TextInputEditText etBarcode, etName, etPrice;
    private TextView tWarning;
    private Button btOkay;

    private SimpleDateFormat sdf;
    private String dateTime;

    private ItemsViewModel model;

    private String name, barcode;
    private int id;
    private double price;
    private Boolean barcodeExists = false, nameExists = false, noPrice = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);
        setTitle("Update Item");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getInt("item_id");
            name = extras.getString("item_name");
            barcode = extras.getString("item_barcode");
            price = extras.getDouble("item_price");
        }

        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etBarcode = findViewById(R.id.etBarcode);
        btOkay = findViewById(R.id.btOkay);
        tWarning = findViewById(R.id.tWarning);

        etName.setText(name);
        etBarcode.setText(barcode);
        etPrice.setText(String.valueOf(price));

        btOkay.setEnabled(false);

        model = new ViewModelProvider(this).get(ItemsViewModel.class);

        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!etPrice.getText().toString().trim().isEmpty() || !etPrice.getText().toString().trim().equals("0")) {
                    btOkay.setEnabled(true);
                } else {
                    btOkay.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                model.checkBarcode(etBarcode.getText().toString().trim()).observe(UpdateItemActivity.this,
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
                model.checkName(etName.getText().toString().trim()).observe(UpdateItemActivity.this,
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
                Intent resultIntent = new Intent();

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

                    resultIntent.putExtra(ITEM_ID, id);
                    resultIntent.putExtra(ITEM_NAME, name);
                    resultIntent.putExtra(ITEM_PRICE, price);
                    resultIntent.putExtra(ITEM_DATE_TIME, dateTime);
                    resultIntent.putExtra(ITEM_BARCODE, barcode);
                    setResult(RESULT_OK, resultIntent);

                    finish();
                }
            }
        });
    }

    @Override
    public boolean onNavigateUp() {
        return super.onNavigateUp();
    }
}
