package com.example.renters;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LessorSignin extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonGoToSignUp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Firestore instance for validation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessor_sign_in);

        // Initialize Firebase
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
                    Toast.makeText(LessorSignin.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(email, password);
                }
            }
        });

        // Handle sign-up button click
        buttonGoToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Lessor Sign-Up Activity
                Intent intent = new Intent(LessorSignin.this, LessorSignUp.class);
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
                            validateLessorAccess(mAuth.getCurrentUser().getUid(), email);
                        } else {
                            Exception e = task.getException();
                            if (e != null) {
                                Log.e("FirebaseAuth", "Login failed: ", e);
                                Toast.makeText(LessorSignin.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void validateLessorAccess(String uid, String email) {
        db.collection("lessors").document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists() && "lessor".equals(document.getString("role"))) {
                            Log.d("Firestore", "Lessor document retrieved: " + document.getData());
                            Intent intent = new Intent(LessorSignin.this, ItemOrRequestSelectionActivity.class);
                            intent.putExtra("USER_EMAIL", email);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e("Firestore", "Access Denied: Incorrect role or no document found for UID: " + uid);
                            Toast.makeText(LessorSignin.this, "Access Denied: You are not registered as a Lessor.", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    } else {
                        Log.e("Firestore", "Error fetching Lessor document: " + task.getException().getMessage());
                        Toast.makeText(LessorSignin.this, "An error occurred while validating access.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

