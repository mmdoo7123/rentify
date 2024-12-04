package com.example.renters;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminDashboardActivity extends AppCompatActivity {

    private Button buttonLogout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView textViewGreeting;

    private Button buttonManageUsers;
    private Button buttonManageCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize TextView for greeting the admin
        textViewGreeting = findViewById(R.id.textViewGreeting);

        // Fetch and display the admin's name
        fetchAdminName();

        // Initialize and handle the logout button
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(AdminDashboardActivity.this, AdminSignin.class);
            startActivity(intent);
            finish();
        });

        // Initialize and handle the manage categories button
        buttonManageCategories = findViewById(R.id.buttonManageCategories);
        buttonManageCategories.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, CategoryManagementActivity.class);
            startActivity(intent);
        });

        // Initialize and handle the manage users button
        buttonManageUsers = findViewById(R.id.buttonManageUsers);
        buttonManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminManageUsersActivity.class);
            startActivity(intent);
        });
    }

    private void fetchAdminName() {
        String adminId = mAuth.getCurrentUser().getUid(); // Get the current admin's UID

        // Fetch the admin's document from Firestore (stored in "admins" collection)
        db.collection("admins").document(adminId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String adminName = task.getResult().getString("name");
                        textViewGreeting.setText("Hello Admin ");
                    } else {
                        textViewGreeting.setText("Hello Admin");
                    }
                });
    }
}
