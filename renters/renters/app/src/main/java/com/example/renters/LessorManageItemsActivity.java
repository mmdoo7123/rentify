package com.example.renters;

import static android.widget.Toast.LENGTH_SHORT;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessorManageItemsActivity extends AppCompatActivity {

    private EditText editTextItemName, editTextItemDescription, editTextFee, editTextTimePeriod;
    private Spinner spinnerCategories;
    private RecyclerView recyclerViewItems;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<String> categoriesList = new ArrayList<>();
    private List<Item> itemList = new ArrayList<>();
    private ItemAdapter itemAdapter;

    private String selectedCategory;
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
        initUIComponents();

        // Set up RecyclerView
        setupRecyclerView();

        // Load categories and items
        loadCategories();
        loadItems();

        // Set button actions
        setupButtonActions();
    }

    private void initUIComponents() {
        editTextItemName = findViewById(R.id.editTextItemName);
        editTextItemDescription = findViewById(R.id.editTextItemDescription);
        editTextFee = findViewById(R.id.editTextItemFee);
        editTextTimePeriod = findViewById(R.id.editTextRentalPeriod);
        spinnerCategories = findViewById(R.id.spinnerCategories);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
    }

    private void setupRecyclerView() {
        itemAdapter = new ItemAdapter(itemList, this::onItemSelected);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(itemAdapter);
    }

    private void setupButtonActions() {
        Button buttonAddItem = findViewById(R.id.buttonAddItem);
        Button buttonEditItem = findViewById(R.id.buttonEditItem);
        Button buttonDeleteItem = findViewById(R.id.buttonDeleteItem);

        buttonAddItem.setOnClickListener(v -> addItem());
        buttonEditItem.setOnClickListener(v -> editItem());
        buttonDeleteItem.setOnClickListener(v -> deleteItem());
    }

    private void loadCategories() {
        db.collection("categories")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        showToast("Failed to load categories: " + error.getMessage());
                        return;
                    }
                    categoriesList.clear();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot document : snapshots) {
                            categoriesList.add(document.getString("name"));
                        }
                        updateCategorySpinner();
                    }
                });
    }

    private void updateCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(adapter);
    }

    private void loadItems() {
        String lessorId = mAuth.getCurrentUser().getUid();
        if (lessorId == null) return;

        db.collection("items")
                .whereEqualTo("lessorId", lessorId)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        showToast("Failed to load items: " + error.getMessage());
                        return;
                    }
                    if (snapshots != null) {
                        itemList.clear(); // Clear the current list before adding updates
                        for (QueryDocumentSnapshot document : snapshots) {
                            try {
                                Item item = document.toObject(Item.class);
                                item.setId(document.getId()); // Set the document ID as the item's ID
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
            Toast.makeText(this, "Please fill in all required fields.", LENGTH_SHORT).show();
            return;
        }

        double fee;
        try {
            fee = Double.parseDouble(feeInput);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Fee must be a valid number.", LENGTH_SHORT).show();
            return;
        }

        String lessorId = mAuth.getCurrentUser().getUid();

        if (lessorId == null || lessorId.isEmpty()) {
            Toast.makeText(this, "Authentication error. Please log in again.", LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> itemData = new HashMap<>();
        itemData.put("name", name);
        itemData.put("description", description);
        itemData.put("fee", fee);
        itemData.put("timePeriod", timePeriod);
        itemData.put("category", selectedCategory);
        itemData.put("lessorId", lessorId);
        itemData.put("role", "lessor"); // Add role for validation in Firestore rules

        db.collection("items")
                .add(itemData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Item added successfully.", LENGTH_SHORT).show();
                    loadItems();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Log.e("LessorManageItems", "Failed to add item to Firestore: ", e);
                    Toast.makeText(this, "Failed to add item: " + e.getMessage(), LENGTH_SHORT).show();
                });
    }

    private double parseDouble(String feeInput) {
        try {
            return Double.parseDouble(feeInput);
        } catch (NumberFormatException e) {
            showToast("Fee must be a valid number.");
            return -1;
        }
    }

    private String getCurrentUserId() {
        if (mAuth.getCurrentUser() == null) {
            showToast("Authentication error. Please log in again.");
            return null;
        }
        return mAuth.getCurrentUser().getUid();
    }

    private Map<String, Object> createItemData(String name, String description, double fee, String timePeriod, String lessorId) {
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("name", name);
        itemData.put("description", description);
        itemData.put("fee", fee);
        itemData.put("timePeriod", timePeriod);
        itemData.put("category", selectedCategory);
        itemData.put("lessorId", lessorId);
        return itemData;
    }


    private void editItem() {
        if (selectedItemPosition == -1) {
            showToast("Please select an item to edit.");
            return;
        }

        Item selectedItem = itemList.get(selectedItemPosition);
        String itemId = selectedItem.getId();

        Map<String, Object> updates = createItemData(
                editTextItemName.getText().toString().trim(),
                editTextItemDescription.getText().toString().trim(),
                parseDouble(editTextFee.getText().toString().trim()),
                editTextTimePeriod.getText().toString().trim(),
                getCurrentUserId()
        );

        db.collection("items").document(itemId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    showToast("Item updated successfully.");
                    loadItems();
                    clearFields();
                })
                .addOnFailureListener(e -> showToast("Failed to update item."));
    }

    private void deleteItem() {
        if (selectedItemPosition == -1) {
            showToast("Please select an item to delete.");
            return;
        }

        String itemId = itemList.get(selectedItemPosition).getId();
        Log.d("DeleteItem", "Deleting item with ID: " + itemId);
        db.collection("items").document(itemId).delete()
                .addOnSuccessListener(aVoid -> {
                    showToast("Item deleted successfully." + itemId);
                    loadItems();
                })
                .addOnFailureListener(e -> showToast("Failed to delete item."));
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
        spinnerCategories.setSelection(categoriesList.indexOf(selectedItem.getCategory()));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, LENGTH_SHORT).show();
    }
}
