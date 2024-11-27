package com.example.renters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> itemList; // Original full list of items
    private OnItemClickListener listener;

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ItemAdapter(List<Item> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Bind data from the filtered list
        Item item = itemList.get(position);
        holder.textViewName.setText(item.getName());
        holder.textViewDescription.setText(item.getDescription());
        holder.textViewFee.setText("Fee: $" + item.getFee());
        holder.textViewTimePeriod.setText("Duration: " + item.getTimePeriod());
        holder.textViewCategory.setText("Category: " + item.getCategory());
        // Set item click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position); // Pass the position
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public static class  ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewDescription, textViewFee, textViewTimePeriod, textViewCategory;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewFee = itemView.findViewById(R.id.textViewFee);
            textViewTimePeriod = itemView.findViewById(R.id.textViewTimePeriod);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);

        }
    }
}
