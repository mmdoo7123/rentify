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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign-in success, navigate to admin dashboard
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(AdminSignin.this, "Login successful!", Toast.LENGTH_SHORT).show();

                            // Save user information in Firestore (Optional, if needed)
                            saveUserInfoInFirestore(user);

                            // Redirect to the admin dashboard
                            Intent intent = new Intent(AdminSignin.this, AdminDashboardActivity.class);
                            startActivity(intent);
                            finish(); // Close the login activity
                        } else {
                            // Sign-in failed, display an error message
                            Toast.makeText(AdminSignin.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Save user information in Firestore
    private void saveUserInfoInFirestore(FirebaseUser user) {
        // Create a user data map
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", user.getEmail());
        userData.put("uid", user.getUid());

        // Add user data to Firestore under a "users" collection
        db.collection("users").document(user.getUid()).set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AdminSignin.this, "User data saved in Firestore!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminSignin.this, "Failed to save user data in Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
