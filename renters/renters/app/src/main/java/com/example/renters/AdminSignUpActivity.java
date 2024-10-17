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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
public class AdminSignUpActivity extends AppCompatActivity {


        private EditText editTextAdminEmail, editTextAdminPassword;
        private Button buttonAdminSignUp;
        private FirebaseAuth mAuth;
        private FirebaseFirestore db;

        private static final String ADMIN_COLLECTION = "adminData"; // Firestore collection for admin
        private static final String ADMIN_DOCUMENT = "adminAccount"; // Document to store the single admin

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Initialize Firebase
            FirebaseApp.initializeApp(this);

            setContentView(R.layout.activity_adminsignup);

            // Initialize Firebase Authentication instance
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            // Initialize UI components
            editTextAdminEmail = findViewById(R.id.editTextAdminEmail);
            editTextAdminPassword = findViewById(R.id.editTextAdminPassword);
            buttonAdminSignUp = findViewById(R.id.buttonAdminSignUp);

            // Check if an admin account already exists
            checkAdminAccountExists();

            // Set click listener for the sign-up button
            buttonAdminSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = editTextAdminEmail.getText().toString().trim();
                    String password = editTextAdminPassword.getText().toString().trim();

                    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                        Toast.makeText(AdminSignUpActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                    } else if (password.length() < 6) {
                        Toast.makeText(AdminSignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    } else {
                        createAdminAccount(email, password);
                    }
                }
            });
        }

        // Check if the admin account exists in Firestore
        private void checkAdminAccountExists() {
            DocumentReference adminRef = db.collection(ADMIN_COLLECTION).document(ADMIN_DOCUMENT);
            adminRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Toast.makeText(AdminSignUpActivity.this, "Admin account already exists!", Toast.LENGTH_LONG).show();
                            finish(); // Close the activity if admin account already exists
                        }
                    } else {
                        Toast.makeText(AdminSignUpActivity.this, "Error checking admin status", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Create the admin account with Firebase Auth and Firestore
        private void createAdminAccount(String email, String password) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign up success
                                FirebaseUser user = mAuth.getCurrentUser();

                                // Save admin info in Firestore to mark the account creation
                                saveAdminAccount(user.getUid(), email);

                                Toast.makeText(AdminSignUpActivity.this, "Admin account created!", Toast.LENGTH_SHORT).show();

                                // Redirect to admin dashboard or login screen
                                Intent intent = new Intent(AdminSignUpActivity.this, AdminDashboardActivity.class);
                                startActivity(intent);
                                finish(); // Close the sign-up activity
                            } else {
                                Toast.makeText(AdminSignUpActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        // Save the admin account in Firestore
        private void saveAdminAccount(String userId, String email) {
            DocumentReference adminRef = db.collection(ADMIN_COLLECTION).document(ADMIN_DOCUMENT);

            adminRef.set(new Admin(userId, email)) // Admin is a custom class representing admin details
                    .addOnSuccessListener(aVoid -> {
                        // Admin data saved successfully
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AdminSignUpActivity.this, "Failed to save admin account", Toast.LENGTH_SHORT).show();
                    });
        }

        // Admin data class
        class Admin {
            private String userId;
            private String email;

            public Admin(String userId, String email) {
                this.userId = userId;
                this.email = email;
            }

            public String getUserId() {
                return userId;
            }

            public String getEmail() {
                return email;
            }
        }
}


