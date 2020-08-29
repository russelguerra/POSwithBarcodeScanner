package com.posbarcodescanner.rjg.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.posbarcodescanner.rjg.Entity.SalesHistory;
import com.posbarcodescanner.rjg.R;

import java.util.List;

public class SalesHistoryAdapter extends RecyclerView.Adapter<SalesHistoryAdapter.ViewHolder> {

    private Context context;
    private List<SalesHistory> list;
    private OnClickListener onClickListener;

    public SalesHistoryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_sales_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (list != null) {
            SalesHistory salesHistory = list.get(position);
            holder.setData(salesHistory.getDateTime(), salesHistory.getCustomerName(), salesHistory.getItemName(), salesHistory.getQuantity(), salesHistory.getTotalPrice(), position);
            holder.setListeners();
        }
    }

    @Override
    public int getItemCount() {
        if (this.list != null)
            return this.list.size();
        else return 0;
    }

    public void setList(List<SalesHistory> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tTimedate, tCustomer, tItemName, tItemCount, tPrice;
        int position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tTimedate = itemView.findViewById(R.id.tTimedate);
            tCustomer = itemView.findViewById(R.id.tCustomer);
            tItemName = itemView.findViewById(R.id.tItemName);
            tItemCount = itemView.findViewById(R.id.tItemCount);
            tPrice = itemView.findViewById(R.id.tPrice);
        }

        public void setData(String dateTime, String customer, String itemName, int itemCount, double price, int position) {
            tTimedate.setText(dateTime);
            tCustomer.setText(customer);
            tItemName.setText(itemName);
            tItemCount.setText(String.valueOf(itemCount) + "x");
            tPrice.setText("@P" + String.valueOf(price));
            this.position = position;
        }

        public void setListeners() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickListener != null)
                        onClickListener.onClickListener(list.get(position));
                }
            });
        }
    }

    public interface OnClickListener {
        void onClickListener(SalesHistory salesHistory);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
