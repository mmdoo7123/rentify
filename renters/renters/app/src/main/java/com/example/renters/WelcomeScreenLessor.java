package com.example.renters;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class WelcomeScreenLessor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen_lessor);

        TextView userNameTextView = findViewById(R.id.userName);
        MaterialButton continueButton = findViewById(R.id.continueButton);

        // Retrieve the user's name passed from the login activity, if available
        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName != null) {
            userNameTextView.setText("Hello, " + userName + "!");
        } else {
            userNameTextView.setText("Hello, User!");
        }

        // Set up the Continue button to go to the main screen
        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeScreenLessor.this, CategoryManagementActivity .class);
            User currentUser = getCurrentUser();
            intent.putExtra("lessor", currentUser.getRole());
            startActivity(intent);
            finish();  // Optional: Finish WelcomeActivity so it doesn't stay in the back stack
        });
    }
    private User getCurrentUser() {
        // Example: user with email "example@email.com" and role "lessor"
        return new User("example@email.com", "lessor");
    }
}
