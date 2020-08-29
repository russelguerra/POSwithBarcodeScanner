package com.posbarcodescanner.rjg.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.posbarcodescanner.rjg.Activity.InvoiceActivity;
import com.posbarcodescanner.rjg.Adapter.ItemsAdapter;
import com.posbarcodescanner.rjg.R;
import com.posbarcodescanner.rjg.ViewModel.MainActivityViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMain extends Fragment {
    private static final String TAG = "FragmentMain";

    private MainActivityViewModel model;
    private ItemsAdapter itemsAdapter;

    private RelativeLayout new_invoice, reload;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        getActivity().setTitle("Cashier");

        new_invoice = v.findViewById(R.id.new_invoice);
        new_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), InvoiceActivity.class));
            }
        });


/*        RecyclerView rv = v.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setHasFixedSize(true);

        itemsAdapter = new ItemsAdapter(getContext());
        rv.setAdapter(itemsAdapter);

        itemsAdapter.setOnDeleteClickListener(new ItemsAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClickListener(Items item) {
                model.delete(item);
            }
        });

        Button button = v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewItemActivity.class);
                intent.putExtra("code",NEW_ITEM_ACTIVITY_REQUEST_CODE);
                startActivityForResult(intent, NEW_ITEM_ACTIVITY_REQUEST_CODE);
            }
        });*/

        return v;
    }

/*    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        model = new ViewModelProvider(this).get(MainActivityViewModel.class);
        model.getAllItems().observe(getViewLifecycleOwner(), new Observer<List<Items>>() {
            @Override
            public void onChanged(List<Items> items) {
                itemsAdapter.setItems(items);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_ITEM_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Items item = new Items(data.getStringExtra(ITEM_NAME), Double.parseDouble(data.getStringExtra(ITEM_PRICE)));
            MainActivityViewModel.insert(item);
        } else {
            Log.i(TAG, "onActivityResult: Item not added");
        }
    }*/
}
