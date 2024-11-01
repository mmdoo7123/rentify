package com.example.renters;

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

    private List<User> userList;
    private FirebaseFirestore db;

    public UserAdapter(List<User> userList, FirebaseFirestore db) {
        this.userList = userList;
        this.db = db;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textViewUserName.setText(user.getName()); // Ensure User has a getName() method

        // Disable user button
        holder.buttonDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users").document(user.getUserId()) // Assuming User has getUserId()
                        .update("status", "disabled");
            }
        });

        // Delete user button
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users").document(user.getUserId()).delete();
                userList.remove(position); // Remove the user from the list after deletion
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName;
        Button buttonDisable;
        Button buttonDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            buttonDisable = itemView.findViewById(R.id.buttonDisable);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
