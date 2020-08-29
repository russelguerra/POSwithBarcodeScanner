package com.posbarcodescanner.rjg.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.posbarcodescanner.rjg.Entity.Customers;
import com.posbarcodescanner.rjg.R;

import java.util.List;

public class NewCustomerActivity extends AppCompatActivity {
    private static final String TAG = "NewCustomerActivity";
    public static final String NEW_NAME = "NEW_CUSTOMER_NAME";

    TextView tWarning;
    Button btOkay;
    EditText etName;

    List<Customers> list;
    String name;
    Boolean isExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_customer);

        setTitle("Add New Customer");

        tWarning = findViewById(R.id.tWarning);
        btOkay = findViewById(R.id.btAdd);
        etName = findViewById(R.id.etName);

        btOkay.setEnabled(false);

        list = SelectCustomersActivity.checkCustomerNames;

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name = etName.getText().toString().trim();

                if (etName.getText().toString().trim().isEmpty())
                    btOkay.setEnabled(false);
                else {
                    btOkay.setEnabled(true);
                    for (Customers customer : SelectCustomersActivity.checkCustomerNames) {
                        if (customer.getName().toLowerCase().equals(name.toLowerCase())) {
                            isExists = true;
                            break;
                        } else {
                            isExists = false;
                        }
                    }

                    if(isExists) {
                        tWarning.setVisibility(View.VISIBLE);
                        btOkay.setEnabled(false);
                    } else {
                        tWarning.setVisibility(View.INVISIBLE);
                        btOkay.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.putExtra(NEW_NAME, name);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    @Override
    public boolean onNavigateUp() {
        return super.onNavigateUp();
    }
}
