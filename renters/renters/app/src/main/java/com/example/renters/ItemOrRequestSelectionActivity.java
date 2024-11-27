package com.example.renters;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ItemOrRequestSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_or_request_selection);

        Button manageItemsButton = findViewById(R.id.manageItemsButton);
        Button viewRequestsButton = findViewById(R.id.viewRequestsButton);

        // Handle Item Management Button Click
        manageItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Item Management Activity
                Intent intent = new Intent(ItemOrRequestSelectionActivity.this, LessorManageItemsActivity.class);
                startActivity(intent);
            }
        });

        // Handle View Renting Requests Button Click
        viewRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Renting Requests Activity
                Intent intent = new Intent(ItemOrRequestSelectionActivity.this, LessorRequestsActivity.class);
                startActivity(intent);
            }
        });
    }
}
