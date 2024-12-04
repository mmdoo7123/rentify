package com.example.renters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private FirebaseFirestore db;

    public UserAdapter(Context context, List<User> userList, FirebaseFirestore db) {
        this.context = context;
        this.userList = userList;
        this.db = db;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each user item
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Get the current user
        User user = userList.get(position);

        // Set user data to the views
        holder.textViewUserName.setText(user.getName());
        holder.textViewEmail.setText(user.getEmail());
        holder.textViewRole.setText("Role: " + user.getRole());

        // Set click listeners for the buttons (Disable and Delete)
        holder.buttonDisable.setOnClickListener(v -> {
            // Log to check if button is clicked
            Log.d("UserAdapter", "Disable button clicked for user: " + user.getUserId());

            // Logic for disabling the user (e.g., update the status in Firestore)
            disableUser(user.getUserId());
        });

        holder.buttonDelete.setOnClickListener(v -> {
            // Log to check if button is clicked
            Log.d("UserAdapter", "Delete button clicked for user: " + user.getUserId());

            // Logic for deleting the user (e.g., delete from Firestore)
            deleteUser(user.getUserId());
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    // ViewHolder class to hold the views for each user item
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName;
        TextView textViewEmail;
        TextView textViewRole;
        Button buttonDisable;
        Button buttonDelete;

        public UserViewHolder(View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            textViewRole = itemView.findViewById(R.id.textViewRole);
            buttonDisable = itemView.findViewById(R.id.buttonDisable);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }

    // Method to disable a user (you can update the status field in Firestore)
    private void disableUser(String userId) {
        db.collection("users").document(userId)
                .update("status", "disabled") // Example field to mark as disabled
                .addOnSuccessListener(aVoid -> {
                    // Log success
                    Log.d("UserAdapter", "User disabled: " + userId);

                    // Optionally, you could remove the user from the list and update the UI
                    // userList.removeIf(user -> user.getUserId().equals(userId));
                    // notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Log error if operation fails
                    Log.e("UserAdapter", "Error disabling user: ", e);
                });
    }


    // Method to delete a user from Firestore
    private void deleteUser(String userId) {
        db.collection("users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Log success
                    Log.d("UserAdapter", "User deleted: " + userId);

                    // Optionally, remove user from list and notify adapter to update UI
                    userList.removeIf(user -> user.getUserId().equals(userId));
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Log error if operation fails
                    Log.e("UserAdapter", "Error deleting user: ", e);
                });
    }

}
