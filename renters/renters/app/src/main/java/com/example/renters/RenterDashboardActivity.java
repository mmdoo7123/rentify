package com.example.renters;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class RenterDashboardActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private Button buttonSearch, logoutButton;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_dashboard);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        recyclerView = findViewById(R.id.recyclerView);
        logoutButton = findViewById(R.id.logoutButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up RecyclerView
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList, position -> {
            Item selectedItem = itemList.get(position);
            Toast.makeText(RenterDashboardActivity.this, "Clicked: " + selectedItem.getName(), Toast.LENGTH_SHORT).show();
            // Navigate to ItemDetailActivity and pass the selected item data
            Intent intent = new Intent(RenterDashboardActivity.this, ItemDetailActivity.class);
            intent.putExtra("itemId", selectedItem.getId());
            intent.putExtra("name", selectedItem.getName());
            intent.putExtra("description", selectedItem.getDescription());
            intent.putExtra("fee", selectedItem.getFee());
            intent.putExtra("lessorId", selectedItem.getLessorId());
            intent.putExtra("category", selectedItem.getCategory());
            intent.putExtra("timePeriod", selectedItem.getTimePeriod());
            startActivity(intent);
        });
        recyclerView.setAdapter(itemAdapter);
        db.collection("renters").document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String role = document.getString("role");
                        Log.d("RenterDashboard", "User role: " + role);
                    }
                });

        loadAllItems();
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No implementation needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No implementation needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    loadAllItems();
                }
            }
        });
        // Handle search button click
        buttonSearch.setOnClickListener(v -> {
            String queryText = editTextSearch.getText().toString().trim();
            if (TextUtils.isEmpty(queryText)) {
                Toast.makeText(RenterDashboardActivity.this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            } else {
                searchItems(queryText);
            }
        });

        // Handle logout button click
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadAllItems() {
        String renterId = mAuth.getCurrentUser().getUid();
        if (renterId == null) {
            Log.e("LoadItems", "No authenticated user.");
            return;
        }

        // Renters can only see all items (no filtering by lessorId)
        db.collection("items")
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
                                Log.e("RenterDashboard", "Error parsing item: " + document.getId(), e);
                            }
                        }
                        runOnUiThread(() -> itemAdapter.notifyDataSetChanged());
                    } else {
                        Log.d("FirestoreListener", "No items found.");
                    }
                });
    }

    private void searchItems(String queryText) {
        // Ensure the queryText is not null or empty
        if (TextUtils.isEmpty(queryText)) {
            Toast.makeText(this, "Search query cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query Firestore for case-sensitive partial matches
        Query queryByName = db.collection("items")
                .orderBy("name") // Sort by the "name" field
                .startAt(queryText)
                .endAt(queryText + "\uf8ff"); // "\uf8ff" ensures all matches starting with queryText are included

        Query queryByCategory = db.collection("items")
                .orderBy("category") // Sort by the "category" field
                .startAt(queryText)
                .endAt(queryText + "\uf8ff");

        // Execute both queries
        Task<QuerySnapshot> nameTask = queryByName.get();
        Task<QuerySnapshot> categoryTask = queryByCategory.get();

        Tasks.whenAllComplete(nameTask, categoryTask)
                .addOnSuccessListener(tasks -> {
                    itemList.clear(); // Clear the list before adding new results
                    for (Task<?> task : tasks) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = (QuerySnapshot) task.getResult();
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                Item item = document.toObject(Item.class);
                                item.setId(document.getId());
                                itemList.add(item);
                                Log.d("SearchItems", "Item found: " + item.getName());
                            }
                        }
                    }

                    // Update RecyclerView on the main thread
                    runOnUiThread(() -> {
                        itemAdapter.notifyDataSetChanged();
                        if (itemList.isEmpty()) {
                            Toast.makeText(this, "No items match your search.", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("SearchItems", "Search failed", e);
                    Toast.makeText(this, "Search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
