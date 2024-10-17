package com.example.renters;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.renters.R; // Corrected the R import

public class ProfilesLoginActivity extends AppCompatActivity {

    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles_login);

        // Retrieve action (signup or signin) from Intent
        action = getIntent().getStringExtra("action");

        // Lessor button click listener
        findViewById(R.id.lessorButton).setOnClickListener(view -> handleUserSelection("lessor"));

        // Renter button click listener
        findViewById(R.id.renterButton).setOnClickListener(view -> handleUserSelection("renter"));

        // Admin button click listener
        findViewById(R.id.adminButton).setOnClickListener(view -> handleUserSelection("admin"));
    }

    private void handleUserSelection(String userType) {
        if ("signup".equals(action)) {
            // Navigate directly to the profile page if it's a signup
            navigateToProfile(userType);
        } else {
            // Navigate to the SignInActivity if it's a signin
            Intent intent = new Intent(ProfilesLoginActivity.this, SigninActivity.class);
            intent.putExtra("user_type", userType);  // Pass selected user type
            startActivity(intent);
        }
    }

    private void navigateToProfile(String userType) {
        Intent intent;
        if ("lessor".equals(userType)) {
            intent = new Intent(this, LessorProfileActivity.class);
        } else {
            // Placeholder for other user profiles (e.g., Renter or Admin)
            intent = new Intent(this, LessorProfileActivity.class); // Replace with relevant activity
        }
        startActivity(intent);
    }
}

