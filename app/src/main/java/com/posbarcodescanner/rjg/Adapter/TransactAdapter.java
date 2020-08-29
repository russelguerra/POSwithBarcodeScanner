package com.posbarcodescanner.rjg.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.posbarcodescanner.rjg.Entity.Transactions;
import com.posbarcodescanner.rjg.R;

import java.util.List;

public class TransactAdapter extends RecyclerView.Adapter<TransactAdapter.ViewHolder> {

    private Context context;
    private List<Transactions> list;
    private OnClickListener onClickListener;

    public TransactAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_transact, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (list != null) {
            Transactions transact = list.get(position);
            holder.setData(transact.getItemName(), transact.getQuantity(), transact.getTotalPrice(), position);
            holder.setListeners();
        }
    }

    @Override
    public int getItemCount() {
        if (this.list != null)
            return this.list.size();
        else return 0;
    }

    public void setList(List<Transactions> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tPrice, tQuantity, tName;
        int position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tName = itemView.findViewById(R.id.tName);
            tQuantity = itemView.findViewById(R.id.tQuantity);
            tPrice = itemView.findViewById(R.id.tPrice);
        }

        public void setData(String name, int quantity, double price, int position) {
            tName.setText(name);
            tPrice.setText(String.valueOf(price));
            tQuantity.setText(String.valueOf(quantity));
            this.position = position;
        }

        public void setListeners() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickListener != null) {
                        onClickListener.onClickListener(list.get(position));
                    }
                }
            });
        }
    }

    public interface OnClickListener {
        void onClickListener(Transactions transactions);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}
