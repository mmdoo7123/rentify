package com.example.renters;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton addCategoryButton;
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        recyclerView = findViewById(R.id.recyclerViewCategories);
        addCategoryButton = findViewById(R.id.fabAddCategory);

        db = FirebaseFirestore.getInstance();
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(categoryList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadCategories();

        // Add new category
        addCategoryButton.setOnClickListener(view -> {
            Intent intent = new Intent(CategoryManagementActivity.this, AddEditCategory.class);
            startActivity(intent);
        });
    }

    private void loadCategories() {
        db.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoryList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Category category = document.toObject(Category.class);
                        category.setId(document.getId()); // Save document ID
                        categoryList.add(category);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show());
    }
}
