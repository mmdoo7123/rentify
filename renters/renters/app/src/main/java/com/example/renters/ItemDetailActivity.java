package com.example.renters;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
public class ItemDetailActivity extends AppCompatActivity {
    private String itemId, itemName, itemDescription, lessorId, category, timePeriod;
    private double fee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail_activity);

        // Retrieve data from intent
        itemId = getIntent().getStringExtra("itemId");
        itemName = getIntent().getStringExtra("name");
        itemDescription = getIntent().getStringExtra("description");
        lessorId = getIntent().getStringExtra("lessorId");
        category = getIntent().getStringExtra("category");
        timePeriod = getIntent().getStringExtra("timePeriod");
        fee = getIntent().getDoubleExtra("fee", 0);
        // Bind data to UI components
        // Bind data to UI components
        TextView itemNameText = findViewById(R.id.itemName);
        TextView itemDescriptionText = findViewById(R.id.itemDescription);
        TextView itemFeeText = findViewById(R.id.itemFee);
        TextView itemCategoryText = findViewById(R.id.itemCategory);
        TextView itemTimePeriodText = findViewById(R.id.itemTimePeriod);

        itemNameText.setText(itemName);
        itemDescriptionText.setText(itemDescription);
        itemFeeText.setText("Fee: $" + fee);
        itemCategoryText.setText("Category: " + category);
        itemTimePeriodText.setText("Duration: " + timePeriod + " days");

        // Handle Create Request Button
        Button createRequestButton = findViewById(R.id.createRequestButton);
        createRequestButton.setOnClickListener(v -> createRentRequest());

        // Handle Cancel Button
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> finish());
    }
    private void createRentRequest() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String renterId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Prepare request data
        Map<String, Object> request = new HashMap<>();
        request.put("itemId", itemId);
        request.put("itemName", itemName);
        request.put("description", itemDescription);
        request.put("category", category);
        request.put("timePeriod", timePeriod);
        request.put("fee", fee);
        request.put("lessorId", lessorId);
        request.put("renterId", renterId);
        request.put("status", "Pending");

        // Save to Firestore
        db.collection("renting_requests")
                .add(request)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Request created successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to create request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
}
