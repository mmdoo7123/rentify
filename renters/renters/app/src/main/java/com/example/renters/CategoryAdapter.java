package com.example.renters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<CategoryItem> categoryList;
    private final OnCategorySelectedListener onCategorySelectedListener;
    private int selectedCategoryPosition = -1;
    // Interface pour gérer la sélection de catégories
    public interface OnCategorySelectedListener {
        void onCategorySelected(int position);
    }

    // Constructeur
    public CategoryAdapter(List<CategoryItem> categoryList, OnCategorySelectedListener onCategorySelectedListener) {
        this.categoryList = categoryList;
        this.onCategorySelectedListener = onCategorySelectedListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryItem category = categoryList.get(position);
        holder.bind(category, position);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setSelectedCategoryPosition(int position) {
        selectedCategoryPosition = position;
        notifyDataSetChanged(); // Refresh the list to update UI
    }

    // ViewHolder pour les éléments de catégorie
    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewCategoryName;
        private final TextView textViewCategoryDescription;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            textViewCategoryDescription = itemView.findViewById(R.id.textViewCategoryDescription);
        }

        public void bind(CategoryItem category, int position) {
            textViewCategoryName.setText(category.getName());
            textViewCategoryDescription.setText(category.getDescription());

            // Highlight the selected category
            if (selectedCategoryPosition == position) {
                itemView.setBackgroundColor(Color.LTGRAY); // Change to your preferred color
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT); // Reset color for non-selected items
            }

            itemView.setOnClickListener(v -> {
                if (onCategorySelectedListener != null) {
                    onCategorySelectedListener.onCategorySelected(position);
                }
            });
        }
    }
}