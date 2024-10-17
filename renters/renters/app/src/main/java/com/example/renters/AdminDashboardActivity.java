package com.example.renters;

import android.content.Intent;
import android.os.Bundle;
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
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        recyclerViewUsers.setAdapter(userAdapter);

        // Fetch users from Firestore and display them in the RecyclerView
        fetchUsersFromFirestore();

        // Initialize and handle the logout button
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the current user
                FirebaseAuth.getInstance().signOut();

                // Navigate back to the login or sign-in activity
                Intent intent = new Intent(AdminDashboardActivity.this, AdminSignin.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Method to fetch the admin's name from Firestore and display it
    private void fetchAdminName() {
        String adminId = mAuth.getCurrentUser().getUid(); // Get the current admin's UID

        // Fetch the admin's document from Firestore (stored in "admins" collection)
        db.collection("admins").document(adminId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String adminName = document.getString("name"); // Assuming the field is called "name"
                                textViewGreeting.setText("Hello Admin " + adminName);
                            } else {
                                textViewGreeting.setText("Hello Admin");
                            }
                        } else {
                            textViewGreeting.setText("Hello Admin");
                        }
                    }
                });
    }

    // Method to fetch the list of users from Firestore
    private void fetchUsersFromFirestore() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                userList.add(user);
                            }
                            userAdapter.notifyDataSetChanged();
                        } else {
                            // Handle any errors that occurred while fetching users
                        }
                    }
                });
    }
}
