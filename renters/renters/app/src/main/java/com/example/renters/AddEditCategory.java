package com.example.renters;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEditCategory extends AppCompatActivity {

    private EditText categoryNameEditText, categoryDescriptionEditText;
    private Button saveCategoryButton;

    private FirebaseFirestore db;
    private String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_category);

        db = FirebaseFirestore.getInstance();

        categoryNameEditText = findViewById(R.id.editTextCategoryName);
        categoryDescriptionEditText = findViewById(R.id.editTextCategoryDescription);
        saveCategoryButton = findViewById(R.id.buttonSaveCategory);

        categoryId = getIntent().getStringExtra("categoryId");
        if (categoryId != null) {
            categoryNameEditText.setText(getIntent().getStringExtra("categoryName"));
            categoryDescriptionEditText.setText(getIntent().getStringExtra("categoryDescription"));
        }

        saveCategoryButton.setOnClickListener(view -> saveCategory());
    }

    private void saveCategory() {
        String name = categoryNameEditText.getText().toString().trim();
        String description = categoryDescriptionEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            categoryNameEditText.setError("Name is required");
            return;
        }

        Map<String, Object> category = new HashMap<>();
        category.put("name", name);
        category.put("description", description);

        if (categoryId == null) {
            db.collection("categories").add(category)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show());
        } else {
            db.collection("categories").document(categoryId)
                    .update(category)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Category updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update category", Toast.LENGTH_SHORT).show());
        }
    }
}
