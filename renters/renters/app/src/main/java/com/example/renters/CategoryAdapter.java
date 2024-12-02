package com.example.renters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categoryList;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CategoryAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getName());
        holder.categoryDescription.setText(category.getDescription());

        // Edit Button Click Listener
        holder.btnEditCategory.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddEditCategory.class);
            intent.putExtra("categoryId", category.getId());
            intent.putExtra("categoryName", category.getName());
            intent.putExtra("categoryDescription", category.getDescription());
            context.startActivity(intent);
        });

        // Delete Button Click Listener
        holder.btnDeleteCategory.setOnClickListener(view -> {
            db.collection("categories").document(category.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show();
                        categoryList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, categoryList.size());
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete category", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView categoryName, categoryDescription;
        Button btnEditCategory, btnDeleteCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryDescription = itemView.findViewById(R.id.categoryDescription);
            btnEditCategory = itemView.findViewById(R.id.btnEditCategory);
            btnDeleteCategory = itemView.findViewById(R.id.btnDeleteCategory);
        }
    }
}
