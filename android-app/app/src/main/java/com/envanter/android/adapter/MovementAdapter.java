package com.envanter.android.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.envanter.android.R;
import com.envanter.android.model.StockMovementDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MovementAdapter extends RecyclerView.Adapter<MovementAdapter.ViewHolder> {

    private List<StockMovementDTO> movementList = new ArrayList<>();

    // Renk sabitleri — onBind'da her seferinde parseColor çağrısını önler
    private static final int COLOR_GREEN = Color.parseColor("#4CAF50");
    private static final int COLOR_RED = Color.parseColor("#F44336");

    /**
     * DiffUtil ile güncelleme — notifyDataSetChanged() yerine yalnızca değişen satırlar
     * yeniden çizilir. Büyük hareket listelerinde UI donmasını önler.
     */
    public void setMovements(List<StockMovementDTO> newMovements) {
        List<StockMovementDTO> newList = newMovements != null ? newMovements : new ArrayList<>();
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() { return movementList.size(); }

            @Override
            public int getNewListSize() { return newList.size(); }

            @Override
            public boolean areItemsTheSame(int oldPos, int newPos) {
                return Objects.equals(movementList.get(oldPos).getId(), newList.get(newPos).getId());
            }

            @Override
            public boolean areContentsTheSame(int oldPos, int newPos) {
                StockMovementDTO oldItem = movementList.get(oldPos);
                StockMovementDTO newItem = newList.get(newPos);
                return Objects.equals(oldItem.getType(), newItem.getType())
                        && oldItem.getQuantity() == newItem.getQuantity()
                        && Objects.equals(oldItem.getCreatedAt(), newItem.getCreatedAt());
            }
        });
        this.movementList = new ArrayList<>(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movement_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StockMovementDTO movement = movementList.get(position);
        
        String itemName = movement.getItemName() != null ? movement.getItemName() : "Bilinmeyen Ürün";
        String reason = movement.getReason() != null ? movement.getReason() : "Belirtilmedi";
        
        holder.tvReason.setText(itemName + " - " + reason);
        
        // Tarih formatini duzenleyelim (Backend'den gelen: 2026-05-14T16:50...)
        String date = movement.getCreatedAt();
        if (date != null && date.contains("T")) {
            date = date.replace("T", " ").substring(0, Math.min(date.length(), 16));
        }
        holder.tvDate.setText(date);
        
        if ("IN".equalsIgnoreCase(movement.getType())) {
            holder.tvQuantity.setText("+" + movement.getQuantity());
            holder.tvQuantity.setTextColor(COLOR_GREEN);
            holder.ivType.setImageResource(android.R.drawable.ic_input_add);
            holder.ivType.setColorFilter(COLOR_GREEN);
        } else {
            holder.tvQuantity.setText("-" + movement.getQuantity());
            holder.tvQuantity.setTextColor(COLOR_RED);
            holder.ivType.setImageResource(android.R.drawable.ic_delete);
            holder.ivType.setColorFilter(COLOR_RED);
        }
        // Atama (Zimmet) ve Iade Tarihi Gosterimi
        if (movement.getAssignedTo() != null && !movement.getAssignedTo().isEmpty()) {
            holder.tvAssignedTo.setVisibility(View.VISIBLE);
            holder.tvAssignedTo.setText("Kime: " + movement.getAssignedTo());
        } else {
            holder.tvAssignedTo.setVisibility(View.GONE);
        }

        String returnDate = movement.getFormattedReturnDate();
        if (returnDate != null && !returnDate.equals("Belirtilmedi") && !returnDate.equals("Tarih Belirtilmedi")) {
            holder.tvReturnDate.setVisibility(View.VISIBLE);
            holder.tvReturnDate.setText("İade/Teslim: " + returnDate);
        } else {
            holder.tvReturnDate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return movementList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReason, tvDate, tvQuantity, tvAssignedTo, tvReturnDate;
        ImageView ivType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReason = itemView.findViewById(R.id.tvMovementReason);
            tvDate = itemView.findViewById(R.id.tvMovementDate);
            tvQuantity = itemView.findViewById(R.id.tvMovementQuantity);
            ivType = itemView.findViewById(R.id.ivMovementType);
            tvAssignedTo = itemView.findViewById(R.id.tvAssignedTo);
            tvReturnDate = itemView.findViewById(R.id.tvReturnDate);
        }
    }
}
