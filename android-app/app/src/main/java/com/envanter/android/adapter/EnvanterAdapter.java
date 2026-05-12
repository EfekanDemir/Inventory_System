package com.envanter.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.envanter.android.R;
import com.envanter.android.model.ItemDTO;

import java.util.ArrayList;
import java.util.List;

public class EnvanterAdapter extends RecyclerView.Adapter<EnvanterAdapter.ViewHolder> {

    private List<ItemDTO> itemList = new ArrayList<>();

    public void setItems(List<ItemDTO> items) {
        this.itemList = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_envanter_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemDTO item = itemList.get(position);
        holder.tvItemCode.setText(item.getItemCode());
        holder.tvItemName.setText(item.getName());
        holder.tvStockQuantity.setText(String.valueOf(item.getQuantity()));
        
        // Stok durumuna gore TextView rengini dinamik degistir
        if (item.getQuantity() < item.getMinStockLevel()) {
            holder.tvStockQuantity.setTextColor(0xFFF44336); // Kritik (Kirmizi)
        } else {
            holder.tvStockQuantity.setTextColor(0xFF4CAF50); // Yeterli (Yesil)
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemCode, tvItemName, tvStockQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemCode = itemView.findViewById(R.id.tvItemCode);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvStockQuantity = itemView.findViewById(R.id.tvStockQuantity);
        }
    }
}
