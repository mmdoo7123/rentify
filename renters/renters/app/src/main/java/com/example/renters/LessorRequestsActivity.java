package com.example.renters;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LessorRequestsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private List<Request> requestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessor_requests);

        recyclerView = findViewById(R.id.requestsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set the adapter
        adapter = new RequestAdapter(requestList, new RequestAdapter.OnRequestActionListener() {
            @Override
            public void onAccept(Request request) {
                acceptRequest(request);
            }

            @Override
            public void onDeny(Request request) {
                denyRequest(request);
            }
        });
        recyclerView.setAdapter(adapter);

        // Fetch requests from Firestore
        fetchRequests();
    }


    private void fetchRequests() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("renting_requests")
                .whereEqualTo("status", "Pending")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            requestList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String itemId = document.getString("itemId");
                                String itemName = document.getString("itemName");
                                String renterName = document.getString("renterName");
                                String renterId = document.getString("renterId");
                                String rentalDuration = document.getString("rentalDuration");
                                String status = document.getString("status");
                                String timestamp = document.getString("timestamp");  // Read timestamp as a String

                                // Assign Firestore's generated document ID to requestId
                                String requestId = document.getId();  // Get the document ID directly from Firestore

                                // Add to request list
                                requestList.add(new Request(requestId, itemId, itemName, renterName, renterId, rentalDuration, status, timestamp));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    private void acceptRequest(Request request) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String requestId = request.getRequestId();
        Log.d("TAG", "Attempting to accept request with ID: " + requestId);

        db.collection("renting_requests").document(requestId)
                .get()  // Try to fetch the document first
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        // Document exists, so we can update it
                        db.collection("renting_requests").document(requestId)
                                .update("status", "Accepted")
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("TAG", "Request accepted successfully.");
                                    // Show success Toast
                                    Toast.makeText(LessorRequestsActivity.this, "Request accepted", Toast.LENGTH_SHORT).show();

                                    // Update the request status in the local list and notify the adapter
                                    request.setStatus("Accepted");
                                    adapter.notifyItemChanged(requestList.indexOf(request));  // Update the specific item
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("TAG", "Error updating status", e);
                                    Toast.makeText(LessorRequestsActivity.this, "Failed to accept request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Log.e("TAG", "No document found with ID: " + requestId);
                        Toast.makeText(LessorRequestsActivity.this, "No document found with ID: " + requestId, Toast.LENGTH_SHORT).show();
                    }
                });
    }




    private void denyRequest(Request request) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String requestId = request.getRequestId();
        Log.d("TAG", "Attempting to deny request with ID: " + requestId);

        db.collection("renting_requests").document(requestId)
                .get()  // Try to fetch the document first
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        // Document exists, so we can update it
                        db.collection("renting_requests").document(requestId)
                                .update("status", "Denied")
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("TAG", "Request denied successfully.");
                                    // Show success Toast
                                    Toast.makeText(LessorRequestsActivity.this, "Request denied", Toast.LENGTH_SHORT).show();

                                    // Update the request status in the local list and notify the adapter
                                    request.setStatus("Denied");
                                    adapter.notifyItemChanged(requestList.indexOf(request));  // Update the specific item
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("TAG", "Error updating status", e);
                                    Toast.makeText(LessorRequestsActivity.this, "Failed to deny request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Log.e("TAG", "No document found with ID: " + requestId);
                        Toast.makeText(LessorRequestsActivity.this, "No document found with ID: " + requestId, Toast.LENGTH_SHORT).show();
                    }
                });
    }






    // Method to save a new request with a manually set timestamp
    private void saveRequestToFirestore(String itemId, String itemName, String renterName, String renterId, String rentalDuration) {
        // Get the current date and time in a specific format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());  // Current date and time as a string

        // Prepare the request data without setting requestId manually
        Map<String, Object> request = new HashMap<>();
        request.put("itemId", itemId);
        request.put("itemName", itemName);
        request.put("renterName", renterName);
        request.put("renterId", renterId);
        request.put("rentalDuration", rentalDuration);
        request.put("status", "Pending");
        request.put("timestamp", currentTime);  // Manually set timestamp as a string

        // Save request to Firestore and let Firestore generate the document ID
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("renting_requests")
                .add(request)  // Firestore will automatically generate the document ID
                .addOnSuccessListener(documentReference -> {
                    String requestId = documentReference.getId();  // This is the document ID generated by Firestore
                    Log.d("TAG", "Request added with ID: " + requestId);
                    // Optionally, update your local Request object with the generated requestId
                })
                .addOnFailureListener(e -> {
                    Log.w("TAG", "Error adding document", e);
                });
    }


}