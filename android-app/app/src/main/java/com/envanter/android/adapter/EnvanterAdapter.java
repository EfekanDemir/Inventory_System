package com.envanter.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.envanter.android.R;
import com.envanter.android.model.ItemDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class EnvanterAdapter extends RecyclerView.Adapter<EnvanterAdapter.ViewHolder> {

    private List<ItemDTO> itemList = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(ItemDTO item);
        void onDeleteClick(ItemDTO item);
        void onMovementClick(ItemDTO item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

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

        boolean isLowStock = item.getQuantity() < item.getMinStockLevel();

        if (isLowStock) {
            holder.tvStockQuantity.setTextColor(0xFFDC2626);
            holder.tvStockStatus.setText("DÜŞÜK STOK");
            holder.tvStockStatus.setTextColor(0xFFDC2626);
            holder.cardStockStatus.setCardBackgroundColor(0xFFFEE2E2);
        } else {
            holder.tvStockQuantity.setTextColor(0xFF16A34A);
            holder.tvStockStatus.setText("YETERLİ");
            holder.tvStockStatus.setTextColor(0xFF16A34A);
            holder.cardStockStatus.setCardBackgroundColor(0xFFDCFCE7);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(item);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(item);
        });

        holder.btnMovement.setOnClickListener(v -> {
            if (listener != null) listener.onMovementClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemCode, tvItemName, tvStockQuantity, tvStockStatus;
        MaterialCardView cardStockStatus;
        MaterialButton btnEdit, btnDelete, btnMovement;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemCode = itemView.findViewById(R.id.tvItemCode);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvStockQuantity = itemView.findViewById(R.id.tvStockQuantity);
            tvStockStatus = itemView.findViewById(R.id.tvStockStatus);
            cardStockStatus = itemView.findViewById(R.id.cardStockStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnMovement = itemView.findViewById(R.id.btnMovement);
        }
    }
}
