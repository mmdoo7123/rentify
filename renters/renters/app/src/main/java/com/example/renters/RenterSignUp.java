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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

public class RenterSignUp extends AppCompatActivity {

    private EditText editTextFullName, editTextEmail, editTextPassword, editTextPhone, editTextInterests;
    private Button buttonSignUp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rentor_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        // Handle sign-up button click
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = editTextFullName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RenterSignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.length() < 6) {
                        Toast.makeText(RenterSignUp.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    } else {
                        createRenterAccount(fullName, email, password);
                    }
                }
            }
        });
    }

    private void createRenterAccount(String fullName, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-up success
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveRenterAccount(user.getUid(), fullName, email);
                        }
                        Toast.makeText(RenterSignUp.this, "Renter account created!", Toast.LENGTH_SHORT).show();

                        // Automatically log the user in and redirect to the renter dashboard
                        Intent intent = new Intent(RenterSignUp.this, RenterDashboardActivity.class);
                        startActivity(intent);
                        finish(); // Close the sign-up activity
                    } else {
                        Toast.makeText(RenterSignUp.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveRenterAccount(String userId, String fullName, String email) {
        // Create a map to store renter data
        Map<String, Object> renterData = new HashMap<>();
        renterData.put("fullName", fullName);
        renterData.put("email", email);
        renterData.put("role", "renter"); // Assign the renter role
        renterData.put("uid", userId);

        // Save the renter data in the "renters" collection with the UID as the document ID
        db.collection("renters").document(userId)
                .set(renterData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RenterSignUp.this, "Renter information saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RenterSignUp.this, "Failed to save renter information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
