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

//Imports to handle photo upload
import android.content.Intent;
import android.net.Uri;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


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

    //Private variable for image upload
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageReference storageReference;


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

        //initialize firebase storage for images
        storageReference = FirebaseStorage.getInstance().getReference("item_photos");
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
        Button buttonUploadPhoto = findViewById(R.id.buttonUploadPhoto);

        buttonAddItem.setOnClickListener(v -> addItem());
        buttonEditItem.setOnClickListener(v -> editItem());
        buttonDeleteItem.setOnClickListener(v -> deleteItem());
        buttonUploadPhoto.setOnClickListener(v -> openFileChooser());
    }

    private void addItem() {
        // Retrieve inputs from UI components
        String name = editTextItemName.getText().toString().trim();
        String description = editTextItemDescription.getText().toString().trim();
        String feeInput = editTextFee.getText().toString().trim();
        String timePeriod = editTextTimePeriod.getText().toString().trim();
        String lessorId = getValidLessorId();
        selectedCategory = (String) spinnerCategories.getSelectedItem();

        // Validate inputs
        if (name.isEmpty() || feeInput.isEmpty() || timePeriod.isEmpty() ||description.isEmpty()|| selectedCategory == null) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        double fee;
        try {
            fee = Double.parseDouble(feeInput);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Fee must be a valid number.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (lessorId == null || lessorId.isEmpty()) {
            Toast.makeText(this, "Authentication error. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if a photo has been uploaded
        if (imageUri == null) {
            // No image uploaded, save item without photo URL
            saveItemToFirestore(name, description, fee, timePeriod, selectedCategory, lessorId, null);
        } else {


            // Upload the photo to Firebase Storage
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // Photo uploaded successfully, save the item details with the photo URL
                                String photoUrl = uri.toString();
                                saveItemToFirestore(name, description, fee, timePeriod, selectedCategory, lessorId, photoUrl);
                            }))
                    .addOnFailureListener(e -> {
                        // Handle photo upload failure
                        Log.e("LessorManageItems", "Failed to upload photo: ", e);
                        Toast.makeText(this, "Failed to upload photo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private String getValidLessorId() {
        String lessorId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (lessorId == null || lessorId.isEmpty()) {
            showToast("Authentication error. Please log in again.");
            return null;
        }
        return lessorId;
    }


    private void saveItemToFirestore(String name, String description, double fee, String timePeriod, String category, String lessorId, String photoUrl) {
            // Create a map to store item data
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("name", name);
            itemData.put("description", description);
            itemData.put("fee", fee);
            itemData.put("timePeriod", timePeriod);
            itemData.put("category", category);
            itemData.put("lessorId", lessorId);

        if (photoUrl != null) {
                itemData.put("photoUrl", photoUrl);
            }

            // Save the item to Firestore
            db.collection("items")
                    .add(itemData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Item added successfully.", Toast.LENGTH_SHORT).show();
                        loadItems(); // Refresh the item list
                        clearFields(); // Clear input fields
                    })
                    .addOnFailureListener(e -> {
                        Log.e("LessorManageItems", "Failed to add item to Firestore: ", e);
                        Toast.makeText(this, "Failed to add item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

    //for image handling
    private void openFileChooser () {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadPhoto();
        }
    }

    //upload photo to firebase storage
    private void uploadPhoto () {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String photoUrl = uri.toString();
                                savePhotoUrlToItem(photoUrl);
                                showToast("Photo uploaded successfully.");
                            }))
                    .addOnFailureListener(e -> showToast("Failed to upload photo: " + e.getMessage()));
        } else {
            showToast("No file selected.");
        }
    }


    private void loadCategories () {
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

    private void updateCategorySpinner () {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(adapter);
    }

    private void loadItems() {
        String lessorId = mAuth.getCurrentUser().getUid();
        if (lessorId == null){
            Log.e("LoadItems", "No authenticated user.");
            return;
        }

        db.collection("items")
                .whereEqualTo("lessorId", lessorId)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("FirestoreListener", "Error fetching items: " + error.getMessage());
                        return;
                    }
                    if (snapshots != null) {
                        Log.d("FirestoreListener", "Fetched items: " + snapshots.size());
                       itemList.clear();
                        for (QueryDocumentSnapshot document : snapshots) {
                            try {
                                Item item = document.toObject(Item.class);
                                item.setId(document.getId()); // Set the document ID as the item's ID
                                itemList.add(item);
                                Log.d("FirestoreListener", "Item added: " + item.getName());
                            } catch (Exception e) {
                                Log.e("LessorManageItems", "Error parsing item: " + document.getId(), e);
                            }
                        }
                        runOnUiThread(() -> itemAdapter.notifyDataSetChanged());
                    } else {
                        Log.d("FirestoreListener", "No items found.");
                    }
                });
    }

    private void savePhotoUrlToItem (String photoUrl){
        String name = editTextItemName.getText().toString().trim();
        String description = editTextItemDescription.getText().toString().trim();
        String feeInput = editTextFee.getText().toString().trim();
        String timePeriod = editTextTimePeriod.getText().toString().trim();
        selectedCategory = (String) spinnerCategories.getSelectedItem();

        if (name.isEmpty() || feeInput.isEmpty() || timePeriod.isEmpty() || selectedCategory == null) {
            showToast("Please fill in all required fields.");
            return;
        }

        double fee;
        try {
            fee = Double.parseDouble(feeInput);
        } catch (NumberFormatException e) {
            showToast("Fee must be a valid number.");
            return;
        }

        String lessorId = mAuth.getCurrentUser().getUid();
        if (lessorId == null || lessorId.isEmpty()) {
            showToast("Authentication error. Please log in again.");
            return;
        }

        Map<String, Object> itemData = new HashMap<>();
        itemData.put("name", name);
        itemData.put("description", description);
        itemData.put("fee", fee);
        itemData.put("timePeriod", timePeriod);
        itemData.put("category", selectedCategory);
        itemData.put("lessorId", lessorId);
        itemData.put("photoUrl", photoUrl);

        db.collection("items")
                .add(itemData)
                .addOnSuccessListener(documentReference -> {
                    showToast("Item added successfully.");
                    loadItems();
                    clearFields();
                })
                .addOnFailureListener(e -> showToast("Failed to add item: " + e.getMessage()));
    }


    private double parseDouble (String feeInput){
        try {
            return Double.parseDouble(feeInput);
        } catch (NumberFormatException e) {
            showToast("Fee must be a valid number.");
            return -1;
        }
    }

    private String getCurrentUserId () {
        if (mAuth.getCurrentUser() == null) {
            showToast("Authentication error. Please log in again.");
            return null;
        }
        return mAuth.getCurrentUser().getUid();
    }

    private Map<String, Object> createItemData (String name, String description,
                                                double fee, String timePeriod, String lessorId){
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("name", name);
        itemData.put("description", description);
        itemData.put("fee", fee);
        itemData.put("timePeriod", timePeriod);
        itemData.put("category", selectedCategory);
        itemData.put("lessorId", lessorId);
        return itemData;
    }


    private void editItem () {
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

    private void deleteItem () {
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

    private void clearFields () {
        editTextItemName.setText("");
        editTextItemDescription.setText("");
        editTextFee.setText("");
        editTextTimePeriod.setText("");
        spinnerCategories.setSelection(0);
    }

    private void onItemSelected ( int position){
        selectedItemPosition = position;
        Item selectedItem = itemList.get(position);

        editTextItemName.setText(selectedItem.getName());
        editTextItemDescription.setText(selectedItem.getDescription());
        editTextFee.setText(String.valueOf(selectedItem.getFee()));
        editTextTimePeriod.setText(selectedItem.getTimePeriod());
        spinnerCategories.setSelection(categoriesList.indexOf(selectedItem.getCategory()));
    }

    private void showToast (String message){
        Toast.makeText(this, message, LENGTH_SHORT).show();
    }

}



