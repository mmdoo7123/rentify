package com.example.renters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminSignin extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonGoToSignUp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Authentication and Firestore
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoToSignUp = findViewById(R.id.buttonGoToSignUp);

        // Add TextWatcher to detect "admin" input
        editTextEmail.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if the input is "admin" (case insensitive)
                if (s.toString().equalsIgnoreCase("admin")) {
                    editTextEmail.setText("admin2@gmail.com");
                    editTextEmail.setSelection(editTextEmail.getText().length()); // Move cursor to end
                    Toast.makeText(AdminSignin.this, "Email automatically set to admin@gmail.com", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // No action needed after text changes
            }
        });
        // Handle login button click
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(AdminSignin.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(email, password);
                }
            }
        });

        // Handle sign-up button click (navigates to sign-up page)
        buttonGoToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSignin.this, AdminSignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            validateAdminRole(user); // Check if the user is an admin
                        }
                    } else {
                        // Sign-in failed
                        Toast.makeText(AdminSignin.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void validateAdminRole(FirebaseUser user) {
        String userId = user.getUid();

        // Check if the user is in the "admins" collection
        db.collection("admins").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Admin role validated, navigate to Admin Dashboard
                            Toast.makeText(AdminSignin.this, "Welcome, Admin!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AdminSignin.this, AdminDashboardActivity.class);
                            startActivity(intent);
                            finish(); // Close the login activity
                        } else {
                            // Not an admin, sign out the user
                            Toast.makeText(AdminSignin.this, "Access denied. You are not an admin.", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    } else {
                        // Error checking admin role
                        Toast.makeText(AdminSignin.this, "Error validating admin role: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
