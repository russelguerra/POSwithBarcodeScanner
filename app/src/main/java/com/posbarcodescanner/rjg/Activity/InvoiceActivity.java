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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printable.RawPrintable;
import com.mazenrashed.printooth.data.printable.TextPrintable;
import com.mazenrashed.printooth.data.printer.DefaultPrinter;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.mazenrashed.printooth.utilities.Printing;
import com.mazenrashed.printooth.utilities.PrintingCallback;
import com.posbarcodescanner.rjg.Adapter.TransactAdapter;
import com.posbarcodescanner.rjg.Entity.SalesHistory;
import com.posbarcodescanner.rjg.Entity.TransactionCustomer;
import com.posbarcodescanner.rjg.Entity.Transactions;
import com.posbarcodescanner.rjg.R;
import com.posbarcodescanner.rjg.ViewModel.ItemsViewModel;
import com.posbarcodescanner.rjg.ViewModel.SalesHistoryViewModel;
import com.posbarcodescanner.rjg.ViewModel.SelectCustomerModel;
import com.posbarcodescanner.rjg.ViewModel.TransactCustomerViewModel;
import com.posbarcodescanner.rjg.ViewModel.TransactionsViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class InvoiceActivity extends AppCompatActivity implements PrintingCallback {
    private static final String TAG = "InvoiceActivity";
    private static final int SELECT_CUSTOMER = 99;
    private static final int SELECT_ITEM = 98;
    private static int NEW_ITEM_BARCODE = 95;

    private FloatingActionButton manual_add, scan, checkOut;
    private RecyclerView rv;
    private TextView tItemCount, tTotalPrice;

    private SimpleDateFormat sdf;
    private String dateTime;
    private String uuiD = "";
    private String customerName = "";
    private int customerID = 0;
    private List<Transactions> listTransaction = new ArrayList<>();
    private int checkOutCounter = 0;

    private SelectCustomerModel selectCustomerModel;
    private ItemsViewModel itemsViewModel;
    private TransactionsViewModel transactionsViewModel;
    private TransactCustomerViewModel mTransactCustomerViewModel;
    private SalesHistoryViewModel mSalesHistoryViewModel;

    private TransactAdapter transactAdapter;

    private int quantity = 0, itemCount = 0, printCounter = 0;
    private double totalPrice = 0, change = 0, debit = 0;

    private IntentResult result;
    private boolean barcodeDetected = false;

    private Printing printing;
    ArrayList<Printable> printables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        setTitle("Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        manual_add = findViewById(R.id.manual_add);
        checkOut = findViewById(R.id.checkOut);
        scan = findViewById(R.id.scan);
        rv = findViewById(R.id.rv);
        tItemCount = findViewById(R.id.tItemCount);
        tTotalPrice = findViewById(R.id.tTotalPrice);

        initPrinting();

        printables = new ArrayList<>();
        printables.add(new RawPrintable.Builder(new byte[]{27, 100, 4}).build());


        transactAdapter = new TransactAdapter(this);

        selectCustomerModel = new ViewModelProvider(this).get(SelectCustomerModel.class);
        itemsViewModel = new ViewModelProvider(this).get(ItemsViewModel.class);
        transactionsViewModel = new ViewModelProvider(this).get(TransactionsViewModel.class);
        mTransactCustomerViewModel = new ViewModelProvider(this).get(TransactCustomerViewModel.class);
        mSalesHistoryViewModel = new ViewModelProvider(this).get(SalesHistoryViewModel.class);

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        rv.setAdapter(transactAdapter);
        transactAdapter.setOnClickListener(new TransactAdapter.OnClickListener() {
            @Override
            public void onClickListener(final Transactions transactions) {

                LayoutInflater layoutInflater = getLayoutInflater();
                View v = layoutInflater.inflate(R.layout.dialog_delete_item_from_transaction, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceActivity.this)
                        .setView(v);

                TextView tName = v.findViewById(R.id.tName);
                TextView tQuantity = v.findViewById(R.id.tQuantity);
                TextView btOkay = v.findViewById(R.id.btOkay);

                tName.setText("Item name: " + transactions.getItemName());
                tQuantity.setText("Quantity: " + transactions.getQuantity());

                final AlertDialog dialog = builder.create();

                btOkay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        transactionsViewModel.deleteItem(transactions);
                        Toast.makeText(InvoiceActivity.this, "Entry deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        transactionsViewModel.getAllItemsInTransaction().observe(this, new Observer<List<Transactions>>() {
            @Override
            public void onChanged(List<Transactions> transactions) {
                transactAdapter.setList(transactions);
                if (transactions.size() > 0) {
                    if (checkOutCounter == 0)
                        listTransaction = transactions;
                    checkOut.setEnabled(true);
                } else {
                    checkOut.setEnabled(false);
                }
            }
        });

        transactionsViewModel.getItemCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != null)
                    itemCount = integer;
                else
                    itemCount = 0;

                tItemCount.setText("Item count: #" + itemCount);
            }
        });

        transactionsViewModel.getTotalPrice().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double a) {
                if (a != null)
                    totalPrice = a;
                else
                    totalPrice = 0.0;
                tTotalPrice.setText("Total Price: P" + totalPrice);
            }
        });

        mTransactCustomerViewModel.getCustomer().observe(this, new Observer<TransactionCustomer>() {
            @Override
            public void onChanged(TransactionCustomer transactionCustomer) {
                if (transactionCustomer != null) {
                    setTitle(transactionCustomer.getName());
                }
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(InvoiceActivity.this);
                integrator.setCaptureActivity(CaptureAct.class);
                integrator.setOrientationLocked(true);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scanning...");
                integrator.initiateScan();

            }
        });

        manual_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(InvoiceActivity.this, SelectItemsActivity.class), SELECT_ITEM);
            }
        });

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.dialog_confirm_checkout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceActivity.this)
                        .setView(v);
                final AlertDialog dialog = builder.create();
                dialog.show();

                Button btOkay = v.findViewById(R.id.btOkay);
                TextView tTotal = v.findViewById(R.id.tTotal);
                TextView tChange = v.findViewById(R.id.tChange);
                EditText etCash = v.findViewById(R.id.etCash);

                tTotal.setText("Total: P" + totalPrice);
                btOkay.setEnabled(false);

                etCash.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (etCash.getText().toString().trim().isEmpty()) {
                            debit = 0;
                        } else {
                            debit = Double.parseDouble(etCash.getText().toString().trim());
                        }

                        change = debit - totalPrice;

                        tChange.setText("Change: P" + change);

                        if (change < 0) {
                            btOkay.setEnabled(false);
                        } else {
                            btOkay.setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                btOkay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (printing != null) {
                            printing.setPrintingCallback(InvoiceActivity.this);
                            Log.i(TAG, "onClick: Printer not null");
                        }

                        if (!Printooth.INSTANCE.hasPairedPrinter()) {
                            startActivityForResult(new Intent(InvoiceActivity.this, ScanningActivity.class), ScanningActivity.SCANNING_FOR_PRINTER);
                        } else {
                            Log.w(TAG, "onClick: " + Printooth.INSTANCE.getPairedPrinter().getName());
                            uuiD = UUID.randomUUID().toString();
                            checkOutCounter = 1;
                            sdf = new SimpleDateFormat("MM.dd.yyyy/hh:mmaaa", Locale.getDefault());
                            dateTime = sdf.format(new Date());

                            printables.add(new TextPrintable.Builder()
                                    .setText("JESCIMAC SARI-SARI STORE\n")
                                    .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                                    .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                                    .build());
                            printables.add(new TextPrintable.Builder()
                                    .setText("Date/Time: " + dateTime + "\n--------------------------------")
                                    .build());

                            for (Transactions transactio : listTransaction) {
                                SalesHistory salesHistory = new SalesHistory(uuiD, dateTime,
                                        customerID, customerName, transactio.getItemID(), transactio.getItemName(),
                                        transactio.getQuantity(), transactio.getTotalPrice());
                                SalesHistoryViewModel.insertSalesHistory(salesHistory);

                                printables.add(new TextPrintable.Builder()
                                        .setText(transactio.getQuantity() + "x " + transactio.getItemName() + "...@P" + transactio.getTotalPrice() + "\n")
                                        .build());
                            }
                            printables.add(new TextPrintable.Builder()
                                    .setText("--------------------------------")
                                    .build());
                            printables.add(new TextPrintable.Builder()
                                    .setText("Item count: " + itemCount + " | ")
                                    .build());
                            printables.add(new TextPrintable.Builder()
                                    .setText("Total: P" + totalPrice + "\n")
                                    .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                                    .build());
                            printables.add(new TextPrintable.Builder()
                                    .setText("Debit: P" + debit + " | ")
                                    .build());
                            printables.add(new TextPrintable.Builder()
                                    .setText("Change: P" + change + "\n")
                                    .build());
                            printables.add(new TextPrintable.Builder()
                                    .setText("Customer: " + customerName + "\n")
                                    .build());
                            printables.add(new TextPrintable.Builder()
                                    .setText("THANK YOU!!!\n\n")
                                    .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                                    .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                                    .build());
                            printing.print(printables);

                            TransactionsViewModel.deleteWholeTransaction();
                            TransactCustomerViewModel.deleteCustomers();
                            setTitle("Cart");
                            dialog.dismiss();
                            finish();

                            Toast.makeText(InvoiceActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void printReceipt() {


        printing.print(printables);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                transactionsViewModel.getAllItemsInTransaction().observe(this, new Observer<List<Transactions>>() {
                    @Override
                    public void onChanged(List<Transactions> transactions) {
                        if (transactions.size() > 0) {
                            LayoutInflater inflater = getLayoutInflater();
                            View v = inflater.inflate(R.layout.dialog_cart_exit, null);
                            AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceActivity.this)
                                    .setView(v);
                            Button btNo = v.findViewById(R.id.btNo);
                            Button btOkay = v.findViewById(R.id.btOkay);

                            final AlertDialog dialog = builder.create();
                            dialog.show();

                            btOkay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    transactionsViewModel.deleteWholeTransaction();
                                    mTransactCustomerViewModel.deleteCustomers();
                                    Toast.makeText(InvoiceActivity.this, "Cart has been void", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    finish();
                                }
                            });

                            btNo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            mTransactCustomerViewModel.deleteCustomers();
                            finish();
                        }
                    }
                });
                return true;
            case R.id.select_customer:
                startActivityForResult(new Intent(InvoiceActivity.this, SelectCustomersActivity.class), SELECT_CUSTOMER);
                return true;
            case R.id.clear_customer:
                TransactCustomerViewModel.deleteCustomers();
                setTitle("Cart");
                Toast.makeText(this, "Customer name cleared", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.printer:
                if (Printooth.INSTANCE.hasPairedPrinter()) {
                    Printooth.INSTANCE.removeCurrentPrinter();
                    Toast.makeText(this, "Unpaired with printer", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Pair with printer", Toast.LENGTH_SHORT).show();
                    startActivityForResult(new Intent(InvoiceActivity.this, ScanningActivity.class), ScanningActivity.SCANNING_FOR_PRINTER);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_CUSTOMER && resultCode == RESULT_OK) {
            Log.i(TAG, "onActivityResult: Select Customer");
            if (data != null) {
                customerID = data.getIntExtra("CUSTOMER_ID", 0);
                customerName = data.getStringExtra("CUSTOMER_NAME");

                TransactCustomerViewModel.insertCustomer(new TransactionCustomer(customerID, customerName));
            } else {
                Log.i(TAG, "onActivityResult: No Data");
            }

        } else if (requestCode == SELECT_ITEM && resultCode == RESULT_OK) {
            Log.i(TAG, "onActivityResult: Select item");
            if (data != null) {
                TransactionsViewModel.insertItem(new Transactions(data.getIntExtra("item_id", 0),
                        data.getStringExtra("item_name"),
                        data.getIntExtra("item_quantity", 0),
                        data.getDoubleExtra("total_price", 0)));
                Toast.makeText(this, "Entry added", Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "onActivityResult: item not added");
            }
        } else if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == RESULT_OK) {
            initPrinting();
        } else {
            result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    Intent intent = new Intent(InvoiceActivity.this, SelectItemsActivity.class);
                    intent.putExtra("barcode", result.getContents());
                    intent.putExtra("is_scan", true);
                    startActivityForResult(intent, SELECT_ITEM);
                } else {
                    Log.i(TAG, "onActivityResult: No barcode result");
                }
            }
        }
    }

    private void initPrinting() {
        if (Printooth.INSTANCE.hasPairedPrinter()) {
            printing = Printooth.INSTANCE.printer();
            Log.i(TAG, "initPrinting: Printer instance");
        } else {
            Log.i(TAG, "initPrinting: Else instance");
        }
        if (printing != null) {
            printing.setPrintingCallback(this);
            Log.i(TAG, "initPrinting: Printer callback");
        } else {
            Log.i(TAG, "initPrinting: Else callback");
        }
    }

    @Override
    public void connectingWithPrinter() {
        Toast.makeText(this, "Connecting to printer...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void connectionFailed(String s) {
        Toast.makeText(this, "Connection to printer failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String s) {
        Toast.makeText(this, "Error connecting to printer", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessage(String s) {
        Toast.makeText(this, "Printer: " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void printingOrderSentSuccessfully() {
        Toast.makeText(this, "Printing successful", Toast.LENGTH_SHORT).show();
    }
}
