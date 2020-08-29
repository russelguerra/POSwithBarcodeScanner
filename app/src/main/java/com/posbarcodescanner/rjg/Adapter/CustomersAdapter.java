package com.posbarcodescanner.rjg.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.posbarcodescanner.rjg.Entity.Customers;
import com.posbarcodescanner.rjg.R;

import java.util.ArrayList;
import java.util.List;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.ViewHolder> {

    private Context context;
    private List<Customers> list;
    private OnClickNameListener onClickNameListener;
    private OnDeleteListener onDeleteListener;
    private OnUpdateListener onUpdateListener;

    public CustomersAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_customers, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (list != null) {
            Customers customer = list.get(position);
            holder.setData(customer.getName(), position);

            holder.setListeners();
        } else {
            holder.tName.setText("No item");
        }
    }

    @Override
    public int getItemCount() {
        if (this.list != null)
            return this.list.size();
        else return 0;
    }

    public void setList(List<Customers> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void filterList(String search) {
        List<Customers> filteredList = new ArrayList<>();

        for (Customers customer : list) {
            if (customer.getName().toLowerCase().contains(search.toLowerCase())) {
                filteredList.add(customer);
            }
        }

        setList(filteredList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        int position;
        TextView tName;
        ImageView ivUpdate, ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tName = itemView.findViewById(R.id.tName);
            ivUpdate = itemView.findViewById(R.id.ivUpdate);
            ivDelete = itemView.findViewById(R.id.ivDelete);

        }

        public void setData(String name, int position) {
            tName.setText(name);
            this.position = position;
        }

        public void setListeners() {

            tName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickNameListener != null) {
                        onClickNameListener.onClickNameListener(list.get(position));
                    }
                }
            });

            ivUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onUpdateListener != null) {
                        onUpdateListener.onUpdateListener(list.get(position));
                    }
                }
            });

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onDeleteListener != null) {
                        onDeleteListener.onDeleteListener(list.get(position));
                    }
                }
            });
        }
    }

    public interface OnClickNameListener {
        void onClickNameListener(Customers customer);
    }

    public void setOnClickNameListener(OnClickNameListener onClickNameListener) {
        this.onClickNameListener = onClickNameListener;
    }

    public interface OnUpdateListener {
        void onUpdateListener(Customers customers);
    }

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public interface OnDeleteListener {
        void onDeleteListener(Customers customers);
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
