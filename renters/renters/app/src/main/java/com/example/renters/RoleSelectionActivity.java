package com.example.renters;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.renters.R; // Corrected the R import

public class RoleSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roleselection);

        // Initialize buttons
        Button buttonSelectAdmin = findViewById(R.id.buttonSelectAdmin);
        Button buttonSelectLessor = findViewById(R.id.buttonSelectLessor);
        Button buttonSelectRenter = findViewById(R.id.buttonSelectRenter);

        // Handle Admin selection
        buttonSelectAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoleSelectionActivity.this, AdminSignin.class);
                startActivity(intent);
            }
        });

        // Handle Lessor selection
        buttonSelectLessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoleSelectionActivity.this, LessorSign.class);
                startActivity(intent);
            }
        });

        // Handle Renter selection
        buttonSelectRenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoleSelectionActivity.this, RenterSigninActivity.class);
                startActivity(intent);
            }
        });
    }
}
