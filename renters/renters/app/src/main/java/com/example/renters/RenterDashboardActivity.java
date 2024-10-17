package com.example.renters;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class RenterDashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Firestore instance
    private TextView welcomeTextView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_dashboard);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Initialize UI components
        welcomeTextView = findViewById(R.id.welcomeTextView);
        logoutButton = findViewById(R.id.logoutButton);

        // Check if the user is logged in
        if (currentUser != null) {
            // Fetch and display the renter's name from Firestore
            loadRenterDetails(currentUser.getUid());
        }

        // Handle logout button click
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the current user
                mAuth.signOut();

                // Show a toast message
                Toast.makeText(RenterDashboardActivity.this, "You have logged out successfully.", Toast.LENGTH_SHORT).show();

                // Redirect to the sign-in page
                Intent intent = new Intent(RenterDashboardActivity.this, RenterSigninActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });
    }

    private void loadRenterDetails(String userId) {
        // Reference to the 'renters' collection in Firestore
        DocumentReference renterRef = db.collection("renters").document(userId);

        // Get the document with the user ID
        renterRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get the full name from the Firestore document
                        String fullName = document.getString("fullName");

                        // Set the welcome message with the role and full name
                        String welcomeMessage = "Welcome, Renter " + fullName + "!";
                        welcomeTextView.setText(welcomeMessage);
                    } else {
                        Toast.makeText(RenterDashboardActivity.this, "Failed to retrieve user details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RenterDashboardActivity.this, "Error fetching details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
