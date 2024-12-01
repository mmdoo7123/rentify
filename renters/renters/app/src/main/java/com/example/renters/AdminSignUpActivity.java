package com.example.renters;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminSignUpActivity extends AppCompatActivity {

    private EditText editTextAdminEmail, editTextAdminPassword;
    private Button buttonAdminSignUp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminsignup);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        editTextAdminEmail = findViewById(R.id.editTextAdminEmail);
        editTextAdminPassword = findViewById(R.id.editTextAdminPassword);
        buttonAdminSignUp = findViewById(R.id.buttonAdminSignUp);
        // Add TextWatcher to detect "admin" input
        editTextAdminEmail.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if the input is "admin" (case insensitive)
                if (s.toString().equalsIgnoreCase("admin")) {
                    editTextAdminEmail.setText("admin2@gmail.com");
                    editTextAdminEmail.setSelection(editTextAdminEmail.getText().length()); // Move cursor to end
                    Toast.makeText(AdminSignUpActivity.this, "Email automatically set to admin@gmail.com", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // No action needed after text changes
            }
        });
        // Set click listener for the sign-up button
        buttonAdminSignUp.setOnClickListener(v -> {
            String email = editTextAdminEmail.getText().toString().trim();
            String password = editTextAdminPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(AdminSignUpActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(AdminSignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                createAdminAccount(email, password);
            }
        });
    }

    // Create the admin account with Firebase Auth and Firestore
    private void createAdminAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-up success
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Save admin info in Firestore
                        if (user != null) {
                            saveAdminAccount(user.getUid(), email);
                        }

                        Toast.makeText(AdminSignUpActivity.this, "Admin account created!", Toast.LENGTH_SHORT).show();

                        // Redirect to admin dashboard
                        Intent intent = new Intent(AdminSignUpActivity.this, AdminDashboardActivity.class);
                        startActivity(intent);
                        finish(); // Close the sign-up activity
                    } else {
                        Toast.makeText(AdminSignUpActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Save the admin account in Firestore
    private void saveAdminAccount(String userId, String email) {
        // Create a map to store admin data
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("email", email);
        adminData.put("role", "admin"); // Assign the admin role
        adminData.put("uid", userId);

        // Save the admin data in the "admins" collection with the UID as the document ID
        db.collection("admins").document(userId)
                .set(adminData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminSignUpActivity.this, "Admin role assigned successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminSignUpActivity.this, "Failed to save admin role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
