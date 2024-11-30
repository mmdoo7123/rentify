package com.example.renters;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SigninActivity extends AppCompatActivity {

    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Retrieve user type from Intent
        userType = getIntent().getStringExtra("user_type");

        // Login button click listener
        findViewById(R.id.loginButton).setOnClickListener(view -> {
            navigateToProfile();
        });

        // Back to signup button click listener
        findViewById(R.id.goToSignUp).setOnClickListener(view -> {
            Intent intent = new Intent(SigninActivity.this, LessorSignUp.class);
            startActivity(intent);
        });
    }

    private void navigateToProfile() {
        Intent intent;
        if ("lessor".equals(userType)) {
            intent = new Intent(SigninActivity.this, LessorProfileActivity.class);
        } else {
            // Placeholder for other user types
            intent = new Intent(SigninActivity.this, LessorProfileActivity.class); // Replace with relevant profile
        }
        startActivity(intent);
        finish(); // Close the login page
    }
}

