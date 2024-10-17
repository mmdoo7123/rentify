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

public class MainActivity extends AppCompatActivity {

    private EditText editTextFullName, editTextEmail, editTextPassword, editTextPhone, editTextInterests;
    private Button buttonSignUp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextInterests = findViewById(R.id.editTextInterests);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        // Handle sign-up button click
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = editTextFullName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String phone = editTextPhone.getText().toString().trim();
                String interests = editTextInterests.getText().toString().trim();

                if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                        || TextUtils.isEmpty(phone) || TextUtils.isEmpty(interests)) {
                    Toast.makeText(MainActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.length() < 6) {
                        Toast.makeText(MainActivity.this, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show();
                    } else {
                        createRenterAccount(fullName, email, password, phone, interests);
                    }
                }
            }
        });
    }

    private void createRenterAccount(String fullName, String email, String password, String phone, String interests) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign-up success
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveRenterInfo(user.getUid(), fullName, email, phone, interests);

                            Toast.makeText(MainActivity.this, "Inscription réussie!", Toast.LENGTH_SHORT).show();

                            // Automatically log the user in and redirect to the renter dashboard
                            Intent intent = new Intent(MainActivity.this, RenterDashboardActivity.class);
                            startActivity(intent);
                            finish(); // Close the sign-up activity
                        } else {
                            Toast.makeText(MainActivity.this, "Échec de l'authentification: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveRenterInfo(String userId, String fullName, String email, String phone, String interests) {
        // Reference to the 'renters' collection
        DocumentReference renterRef = db.collection("renters").document(userId);

        // Create a map to store user details
        Map<String, Object> renter = new HashMap<>();
        renter.put("fullName", fullName);
        renter.put("email", email);
        renter.put("phone", phone);
        renter.put("interests", interests);

        // Save to Firestore
        renterRef.set(renter)
                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Informations enregistrées!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show());
    }
}
