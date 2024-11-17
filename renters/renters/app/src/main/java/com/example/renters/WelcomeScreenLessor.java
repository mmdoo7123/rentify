package com.example.renters;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WelcomeScreenLessor extends AppCompatActivity {

    private static final String TAG = "WelcomeScreenLessor";
    private FirebaseFirestore firestore; // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen_lessor);

        TextView userNameTextView = findViewById(R.id.userName);
        MaterialButton continueButton = findViewById(R.id.continueButton);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Retrieve the user's ID passed from the login activity
        String userId = getIntent().getStringExtra("USER_ID");

        if (userId != null) {
            // Fetch the lessor's name from Firestore
            fetchLessorName(userId, userNameTextView);

        } else {
            userNameTextView.setText("Hello, User!");
        }

        // Set up the Continue button to go to the main screen
        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeScreenLessor.this, LessorManageItemsActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Fetch the lessor's name from Firestore using their user ID.
     *
     * @param userId          The ID of the user (lessor).
     * @param userNameTextView The TextView to display the user's name.
     */
    private void fetchLessorName(String userId, TextView userNameTextView) {
        Log.d(TAG, "Fetching name for userId: " + userId);

        firestore.collection("lessors")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            Log.d(TAG, "Document data: " + document.getData());

                            String name = document.getString("name");
                            if (name != null) {
                                userNameTextView.setText("Hello, " + name + "!");
                            } else {
                                Log.e(TAG, "Name field is missing in Firestore document");
                                userNameTextView.setText("Hello, User!");
                            }
                        } else {
                            Log.e(TAG, "Document not found for userId: " + userId);
                            userNameTextView.setText("Hello, User!");
                        }
                    } else {
                        Log.e(TAG, "Firestore error: ", task.getException());
                        userNameTextView.setText("Hello, User!");
                    }
                });
    }
}
