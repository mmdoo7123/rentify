package com.example.renters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LessorSign extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword;
    private Button signUpButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessorsignup);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        editTextName = findViewById(R.id.editTextName);  // Add this line
        editTextEmail = findViewById(R.id.editTextTextEmailAddress2);
        editTextPassword = findViewById(R.id.editTextPassword);
        signUpButton = findViewById(R.id.signUpButton);

        // Sign up button click listener
        signUpButton.setOnClickListener(view -> {
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LessorSign.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                signUpLessor(name, email, password);
            }
        });

        // Already have an account? button click listener
        findViewById(R.id.already_have_account).setOnClickListener(view -> {
            Intent intent = new Intent(LessorSign.this, LessorSignin.class);
            startActivity(intent);
        });
    }

    // Method to sign up lessor
    private void signUpLessor(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User created successfully, now save the lessor's name and email in Firestore
                            String userId = mAuth.getCurrentUser().getUid();
                            saveLessorInfo(userId, name, email);
                        } else {
                            // If sign up fails, display a message to the user
                            Toast.makeText(LessorSign.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to save lessor info in Firestore
    private void saveLessorInfo(String userId, String name, String email) {
        // Create a new Lessor object
        Map<String, Object> lessor = new HashMap<>();
        lessor.put("name", name);
        lessor.put("email", email);

        // Save the lessor's info in the "lessors" collection in Firestore
        db.collection("lessors").document(userId)
                .set(lessor)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Lessor info saved successfully
                            Toast.makeText(LessorSign.this, "Sign up successful!", Toast.LENGTH_SHORT).show();

                            // Redirect to lessor dashboard or another activity
                            Intent intent = new Intent(LessorSign.this, LessorProfileActivity.class);
                            startActivity(intent);
                            finish(); // Close the current activity
                        } else {
                            // Handle any errors
                            Toast.makeText(LessorSign.this, "Failed to save lessor info: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
