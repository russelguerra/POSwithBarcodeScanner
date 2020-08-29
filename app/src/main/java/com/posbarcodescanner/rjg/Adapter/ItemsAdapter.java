package com.posbarcodescanner.rjg.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.posbarcodescanner.rjg.Entity.Items;
import com.posbarcodescanner.rjg.R;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    private static final String TAG = "ItemsAdapter";

    private Context context;
    private List<Items> items;
    private OnClickListener onClickListener;

    public ItemsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_items, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (items != null) {
            Items item = items.get(position);
            holder.setData(item.getName(), item.getBarcode(), item.getPrice(), position);

            holder.setListeners();
        } else {
            holder.tName.setText("No item");
        }
    }

    @Override
    public int getItemCount() {
        if (this.items != null)
            return this.items.size();
        else return 0;
    }

    public void setItems(List<Items> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public boolean checkBarcode(String barcode) {
        boolean isTrue = false;
        for (Items item : this.items) {
            if (item.getBarcode().equals(barcode)) isTrue = true;
            else isTrue = false;
        }
        return isTrue;
    }

    public Items getItem(String barcode) {
        Items new_item = null;
        for (Items item : this.items) {
            if (item.getBarcode().equals(barcode)) {
                new_item = item;
            }
        }
        return new_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tName, tPrice, tBarcode;
        private int position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tName = itemView.findViewById(R.id.tName);
            tPrice = itemView.findViewById(R.id.tPrice);
            tBarcode = itemView.findViewById(R.id.tBarcode);
        }

        public void setData(String name, String barcode, double price, int position) {
            tName.setText(name);
            tPrice.setText(String.valueOf(price));
            if (barcode.isEmpty())
                tBarcode.setText("n/a");
            else
                tBarcode.setText(barcode);
            this.position = position;
        }

        public void setListeners() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickListener != null) {
                        onClickListener.onClickListener(items.get(position));
                    }
                }
            });
        }
    }

    public interface OnClickListener {
        void onClickListener(Items item);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}
