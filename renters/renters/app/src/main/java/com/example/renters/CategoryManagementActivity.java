package com.example.renters;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CategoryManagementActivity extends AppCompatActivity {
    private EditText editTextCategoryName, editTextCategoryDescription;
    private Button buttonAddCategory, buttonEditCategory, buttonDeleteCategory;
    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;
    private List<CategoryItem> categoryList;
    private int selectedCategoryPosition = -1; // Pour suivre la catégorie sélectionnée

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        // Link views with IDs
        editTextCategoryName = findViewById(R.id.editTextCategoryName);
        editTextCategoryDescription = findViewById(R.id.editTextCategoryDescription);
        buttonAddCategory = findViewById(R.id.buttonAddCategory);
        buttonEditCategory = findViewById(R.id.buttonEditCategory);
        buttonDeleteCategory = findViewById(R.id.buttonDeleteCategory);
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);

        // Initialize the category list and adapter
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryList, this::onCategorySelected);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCategories.setAdapter(categoryAdapter);

        // Set button listeners
        buttonAddCategory.setOnClickListener(v -> addCategory());
        buttonEditCategory.setOnClickListener(v -> editCategory());
        buttonDeleteCategory.setOnClickListener(v -> deleteCategory());
    }

    // Methode to handle category selection
    private void addCategory() {
        String name = editTextCategoryName.getText().toString().trim();
        String description = editTextCategoryDescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a category name.", Toast.LENGTH_SHORT).show();
            return;
        }

        CategoryItem newCategory = new CategoryItem(name, description);
        categoryList.add(newCategory);
        categoryAdapter.notifyDataSetChanged();
        clearFields();
        Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show();
        Log.d("CategoryManagement", "Categories size: " + categoryList.size());
        for (CategoryItem item : categoryList) {
            Log.d("CategoryManagement", "Category: " + item.getName());
        }
    }

    // Méthode pour modifier une catégorie
    private void editCategory() {
        if (selectedCategoryPosition == -1) {
            Toast.makeText(this, "Please select a category to edit.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = editTextCategoryName.getText().toString().trim();
        String description = editTextCategoryDescription.getText().toString().trim();

        CategoryItem selectedCategory = categoryList.get(selectedCategoryPosition);
        selectedCategory.setName(name);
        selectedCategory.setDescription(description);
        categoryAdapter.notifyItemChanged(selectedCategoryPosition);
        clearFields();
        selectedCategoryPosition = -1; // Réinitialiser la sélection
        Toast.makeText(this, "Modified category.", Toast.LENGTH_SHORT).show();
    }

    // Méthode pour supprimer une catégorie
    private void deleteCategory() {
        if (selectedCategoryPosition == -1) {
            Toast.makeText(this, "Please select a category to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        categoryList.remove(selectedCategoryPosition);
        categoryAdapter.notifyItemRemoved(selectedCategoryPosition);
        clearFields();
        selectedCategoryPosition = -1; // Réinitialiser la sélection
        Toast.makeText(this, "Category deleted.", Toast.LENGTH_SHORT).show();
    }

    // Méthode pour effacer les champs d'entrée
    private void clearFields() {
        editTextCategoryName.setText("");
        editTextCategoryDescription.setText("");
    }

    // Méthode pour gérer la sélection de la catégorie
    private void onCategorySelected(int position) {
        selectedCategoryPosition = position;
        categoryAdapter.setSelectedCategoryPosition(position);
        CategoryItem selectedCategory = categoryList.get(position);
        editTextCategoryName.setText(selectedCategory.getName());
        editTextCategoryDescription.setText(selectedCategory.getDescription());
    }
}