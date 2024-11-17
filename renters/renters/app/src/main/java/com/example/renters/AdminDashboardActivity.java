package com.example.renters;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private Button buttonLogout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList;
    private TextView textViewGreeting;

    private Button buttonManageUsers;
    private Button buttonManageCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize TextView for greeting the admin
        textViewGreeting = findViewById(R.id.textViewGreeting);

        // Fetch and display the admin's name
        fetchAdminName();

        // Initialize RecyclerView and set up the layout manager and adapter
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        if (recyclerViewUsers != null) {
            recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
            // Initialize the user list and adapter
            userList = new ArrayList<>();
            userAdapter = new UserAdapter(userList, db); // Pass Firestore reference for disable/delete
            recyclerViewUsers.setAdapter(userAdapter);

            // Fetch users from Firestore and display them in the RecyclerView
            fetchUsersFromFirestore();
        } else {
            Log.e("AdminDashboard", "RecyclerView is null. Check your layout XML.");
        }

        // Initialize and handle the logout button
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(v -> {
            // Sign out the current user
            mAuth.signOut();

            // Navigate back to the login or sign-in activity
            Intent intent = new Intent(AdminDashboardActivity.this, AdminSignin.class);
            startActivity(intent);
            finish();
        });

        // Initialize and handle the manage categories button
        buttonManageCategories = findViewById(R.id.buttonManageCategories);
        buttonManageCategories.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, CategoryManagementActivity.class);
            startActivity(intent);
        });

        // Initialize and handle the manage users button
        buttonManageUsers = findViewById(R.id.buttonManageUsers);
        buttonManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageUsersActivity.class);
            startActivity(intent);
        });
    }

    // Method to fetch the admin's name from Firestore and display it
    private void fetchAdminName() {
        String adminId = mAuth.getCurrentUser().getUid(); // Get the current admin's UID

        // Fetch the admin's document from Firestore (stored in "admins" collection)
        db.collection("admins").document(adminId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String adminName = document.getString("name"); // Assuming the field is called "name"
                            textViewGreeting.setText("Hello Admin " + adminName);
                        } else {
                            textViewGreeting.setText("Hello Admin");
                        }
                    } else {
                        textViewGreeting.setText("Hello Admin");
                    }
                });
    }

    // Method to fetch the list of users from Firestore
    private void fetchUsersFromFirestore() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            if (user != null) {
                                user.setUserId(document.getId()); // Ensure User has a setUserId() method to store document ID
                                userList.add(user);
                            }
                        }
                        userAdapter.notifyDataSetChanged(); // Update the RecyclerView with new data
                    } else {
                        Log.e("AdminDashboard", "Error fetching users: ", task.getException());
                    }
                });
    }
}
