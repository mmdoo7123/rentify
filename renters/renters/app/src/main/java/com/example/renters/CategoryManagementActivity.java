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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        checkUserRole();

        loadCategories();
    }

    private void loadCategories() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Listen for real-time updates from the "categories" collection
        db.collection("categories")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("CategoryManagement", "Error listening to category changes", error);
                        Toast.makeText(this, "Error listening to category changes.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        categoryList.clear();
                        for (QueryDocumentSnapshot document : snapshots) {
                            String id = document.getId();  // Firestore document ID
                            String name = document.getString("name");
                            String description = document.getString("description");

                            // Add the category to the list
                            categoryList.add(new CategoryItem(id, name, description));
                            Log.d("CategoryManagement", "Loaded category: " + name);
                        }
                        categoryAdapter.notifyDataSetChanged();
                    }
                });
    }
    private void checkUserRole() {
        // Get the current user (this method depends on your app's logic for fetching the current user)
        String userRole = getIntent().getStringExtra("USER_ROLE");

        // If the user is a lessor, hide the "Add Category" button
        if (userRole != null && userRole.equals("lessor")) {
            // Hide the "Add Category" button for lessors
            buttonAddCategory.setVisibility(View.GONE);
        } else {
            // Show the "Add Category" button for admins
            buttonAddCategory.setVisibility(View.VISIBLE);
        }
    }

    // Methode to handle category selection
    // Admin adds category to Firestore
    private void addCategory() {
        String name = editTextCategoryName.getText().toString().trim();
        String description = editTextCategoryDescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a category name.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get a reference to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new category document
        Map<String, String> categoryData = new HashMap<>();
        categoryData.put("name", name);
        categoryData.put("description", description);

        // Add the category to Firestore
        db.collection("categories").add(categoryData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("CategoryManagement", "Category added with ID: " + documentReference.getId());
                    Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Log.e("CategoryManagement", "Error adding category", e);
                    Toast.makeText(this, "Error adding category", Toast.LENGTH_SHORT).show();
                });
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
        String categoryId = selectedCategory.getId();  // Get the unique ID of the category

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("description", description);

        db.collection("categories").document(categoryId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Update the category in the list
                    categoryList.get(selectedCategoryPosition).setName(name);
                    categoryList.get(selectedCategoryPosition).setDescription(description);
                    categoryAdapter.notifyItemChanged(selectedCategoryPosition);
                    Toast.makeText(this, "Category updated.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error updating category", Toast.LENGTH_SHORT).show());
    }



    // Méthode pour supprimer une catégorie
    private void deleteCategory() {
        if (selectedCategoryPosition == -1) {
            Toast.makeText(this, "Please select a category to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        CategoryItem selectedCategory = categoryList.get(selectedCategoryPosition);
        String categoryId = selectedCategory.getId();  // Assuming you have a unique ID for each category

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("categories").document(categoryId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    categoryList.remove(selectedCategoryPosition);
                    categoryAdapter.notifyItemRemoved(selectedCategoryPosition);
                    Toast.makeText(this, "Category deleted.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error deleting category", Toast.LENGTH_SHORT).show());
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