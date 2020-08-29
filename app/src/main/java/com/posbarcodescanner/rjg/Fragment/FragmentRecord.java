package com.posbarcodescanner.rjg.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.posbarcodescanner.rjg.Adapter.SalesHistoryAdapter;
import com.posbarcodescanner.rjg.Entity.SalesHistory;
import com.posbarcodescanner.rjg.R;
import com.posbarcodescanner.rjg.ViewModel.SalesHistoryViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRecord extends Fragment {
    private static final String TAG = "FragmentRecord";

    private SalesHistoryViewModel mSalesHistoryViewModel;

    private String search = "%%";

    private RecyclerView rv;
    private TextView tItemCount, tTotalPrice;
    private SalesHistoryAdapter mSalesHistoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_record, container, false);

        getActivity().setTitle("Transaction Record");
        setHasOptionsMenu(true);

        tItemCount = v.findViewById(R.id.tItemCount);
        tTotalPrice = v.findViewById(R.id.tTotalPrice);

        mSalesHistoryViewModel = new ViewModelProvider(this).get(SalesHistoryViewModel.class);
        rv = v.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setHasFixedSize(true);

        mSalesHistoryAdapter = new SalesHistoryAdapter(getContext());
        rv.setAdapter(mSalesHistoryAdapter);

        mSalesHistoryAdapter.setOnClickListener(new SalesHistoryAdapter.OnClickListener() {
            @Override
            public void onClickListener(SalesHistory salesHistory) {
                LayoutInflater inflater1 = getLayoutInflater();
                View v = inflater1.inflate(R.layout.dialog_delete_sales_history, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setView(v);
                Button btOkay = v.findViewById(R.id.btOkay);
                AlertDialog dialog = builder.create();
                dialog.show();

                btOkay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSalesHistoryViewModel.deleteSalesHistory(salesHistory);
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Recorded deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        getRecords(search);

        return v;
    }

    private void getRecords(String search) {
        mSalesHistoryViewModel.getAllSalesHistory("%" + search + "%").observe(getViewLifecycleOwner(), new Observer<List<SalesHistory>>() {
            @Override
            public void onChanged(List<SalesHistory> salesHistories) {
                mSalesHistoryAdapter.setList(salesHistories);
            }
        });

        mSalesHistoryViewModel.getItemCount("%" + search + "%").observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                tItemCount.setText("Total Item Count: #" + integer);
            }
        });

        mSalesHistoryViewModel.getTotalPrice("%" + search + "%").observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                tTotalPrice.setText("Total Price Sold: P" + aDouble);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_transaction_record, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getRecords("%" + newText + "%");
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}
