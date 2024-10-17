package com.example.renters;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.renters.R; // Corrected the R import

public class LessorProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessor_page);

        findViewById(R.id.btnChangeItems).setOnClickListener(view -> {
            // Handle Change Items action
            // Replace with appropriate activity
        });

        findViewById(R.id.btnViewRequests).setOnClickListener(view -> {
            // Handle View Requests action
            // Replace with appropriate activity
        });

        findViewById(R.id.btnPaymentInfo).setOnClickListener(view -> {
            // Handle Payment Info action
            // Replace with appropriate activity
        });

        findViewById(R.id.btnLogout).setOnClickListener(view -> {
            Intent intent = new Intent(LessorProfileActivity.this, LessorSign.class);
            startActivity(intent);
            finish(); // Close the profile page
        });
    }
}
