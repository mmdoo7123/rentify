package com.example.renters;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminManageUsersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        // Initialize RecyclerView and Firestore
        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, FirebaseFirestore.getInstance());
        recyclerView.setAdapter(userAdapter);

        db = FirebaseFirestore.getInstance();
        fetchUsers();
    }

    private void fetchUsers() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear(); // Clear the list to avoid duplicates
                        for (DocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            if (user != null) {
                                user.setUserId(document.getId());  // Ensure User has setUserId() method
                                userList.add(user);
                            }
                        }
                        userAdapter.notifyDataSetChanged(); // Update RecyclerView
                        Log.d("AdminManageUsers", "Users fetched: " + userList.size());
                    } else {
                        Log.e("Firestore", "Error getting users: ", task.getException());
                    }
                });
    }
}
