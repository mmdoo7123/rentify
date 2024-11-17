package com.example.renters;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessorManageItemsActivity extends AppCompatActivity {

    private EditText editTextItemName, editTextItemDescription, editTextFee, editTextTimePeriod;
    private Spinner spinnerCategories;
    private Button buttonAddItem, buttonEditItem, buttonDeleteItem;
    private RecyclerView recyclerViewItems;
    private ItemAdapter itemAdapter;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<String> categoriesList = new ArrayList<>();
    private List<Item> itemList = new ArrayList<>();
    private String selectedCategory = null;
    private int selectedItemPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_management);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Link UI components
        editTextItemName = findViewById(R.id.editTextItemName);
        editTextItemDescription = findViewById(R.id.editTextItemDescription);
        editTextFee = findViewById(R.id.editTextItemFee);
        editTextTimePeriod = findViewById(R.id.editTextRentalPeriod);
        spinnerCategories = findViewById(R.id.spinnerCategories);
        buttonAddItem = findViewById(R.id.buttonAddItem);
        buttonEditItem = findViewById(R.id.buttonEditItem);
        buttonDeleteItem = findViewById(R.id.buttonDeleteItem);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);

        // RecyclerView setup
        itemAdapter = new ItemAdapter(itemList, this::onItemSelected);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(itemAdapter);

        // Load categories and items
        loadCategories();
        loadItems();

        // Button listeners
        buttonAddItem.setOnClickListener(v -> addItem());
        buttonEditItem.setOnClickListener(v -> editItem());
        buttonDeleteItem.setOnClickListener(v -> deleteItem());
    }

    private void loadCategories() {
        db.collection("categories")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("LessorManageItems", "Error listening for category changes: " + error.getMessage(), error);
                        Toast.makeText(this, "Failed to load categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        categoriesList.clear();
                        for (QueryDocumentSnapshot document : snapshots) {
                            categoriesList.add(document.getString("name"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategories.setAdapter(adapter);

                        Log.d("LessorManageItems", "Categories updated: " + categoriesList);
                    }
                });
    }



    private void loadItems() {
        String lessorId = mAuth.getCurrentUser().getUid();
        db.collection("items").whereEqualTo("lessorId", lessorId).get();

        db.collection("items")
                .whereEqualTo("lessorId", lessorId)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("LessorManageItems", "Error listening for item changes: " + error.getMessage(), error);
                        Toast.makeText(this, "Failed to load items: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        itemList.clear(); // Clear the current list before adding updates
                        for (QueryDocumentSnapshot document : snapshots) {
                            try {
                                Item item = document.toObject(Item.class);
                                item.setFee(String.valueOf(document.getDouble("fee"))); // Convert to String
                                itemList.add(item);
                            } catch (Exception e) {
                                Log.e("LessorManageItems", "Error parsing item: " + document.getId(), e);
                            }
                        }
                        itemAdapter.notifyDataSetChanged();
                                   }
                });
    }
    private void addItem() {
        String name = editTextItemName.getText().toString().trim();
        String description = editTextItemDescription.getText().toString().trim();
        String feeInput = editTextFee.getText().toString().trim();
        String timePeriod = editTextTimePeriod.getText().toString().trim();
        selectedCategory = (String) spinnerCategories.getSelectedItem();

        if (name.isEmpty() || feeInput.isEmpty() || timePeriod.isEmpty() || selectedCategory == null) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        double fee;
        try {
            fee = Double.parseDouble(feeInput); // Convert to double
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Fee must be a valid number.", Toast.LENGTH_SHORT).show();
            return;
        }

        String lessorId = mAuth.getCurrentUser().getUid();

        Map<String, Object> itemData = new HashMap<>();
        itemData.put("name", name);
        itemData.put("description", description);
        itemData.put("fee", fee);
        itemData.put("timePeriod", timePeriod);
        itemData.put("category", selectedCategory);
        itemData.put("lessorId", lessorId);

        db.collection("items").add(itemData).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Item added successfully.", Toast.LENGTH_SHORT).show();
            loadItems();
            clearFields();
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to add item.", Toast.LENGTH_SHORT).show());
    }

    private void editItem() {
        if (selectedItemPosition == -1) {
            Toast.makeText(this, "Please select an item to edit.", Toast.LENGTH_SHORT).show();
            return;
        }

        Item selectedItem = itemList.get(selectedItemPosition);
        String itemId = selectedItem.getId();

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", editTextItemName.getText().toString().trim());
        updates.put("description", editTextItemDescription.getText().toString().trim());
        updates.put("fee", editTextFee.getText().toString().trim());
        updates.put("timePeriod", editTextTimePeriod.getText().toString().trim());
        updates.put("category", spinnerCategories.getSelectedItem());

        db.collection("items").document(itemId).update(updates).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Item updated successfully.", Toast.LENGTH_SHORT).show();
            loadItems();
            clearFields();
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to update item.", Toast.LENGTH_SHORT).show());
    }

    private void deleteItem() {
        if (selectedItemPosition == -1) {
            Toast.makeText(this, "Please select an item to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        Item selectedItem = itemList.get(selectedItemPosition);
        String itemId = selectedItem.getId();

        db.collection("items").document(itemId).delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Item deleted successfully.", Toast.LENGTH_SHORT).show();
            loadItems();
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to delete item.", Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        editTextItemName.setText("");
        editTextItemDescription.setText("");
        editTextFee.setText("");
        editTextTimePeriod.setText("");
        spinnerCategories.setSelection(0);
    }

    private void onItemSelected(int position) {
        selectedItemPosition = position;
        Item selectedItem = itemList.get(position);

        editTextItemName.setText(selectedItem.getName());
        editTextItemDescription.setText(selectedItem.getDescription());
        editTextFee.setText(String.valueOf(selectedItem.getFee()));
        editTextTimePeriod.setText(selectedItem.getTimePeriod());
        if (categoriesList.contains(selectedItem.getCategory())) {
            spinnerCategories.setSelection(categoriesList.indexOf(selectedItem.getCategory()));
        } else {
            spinnerCategories.setSelection(0); // Default to the first category if not found
        }
    }
}
