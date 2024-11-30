package com.example.renters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
    private List<Request> requestList;
    private OnRequestActionListener onRequestActionListener;

    public interface OnRequestActionListener {
        void onAccept(Request request);
        void onDeny(Request request);
    }

    public RequestAdapter(List<Request> requestList, OnRequestActionListener onRequestActionListener) {
        this.requestList = requestList;
        this.onRequestActionListener = onRequestActionListener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestList.get(position);
        holder.itemNameText.setText(request.getItemName());
        holder.renterNameText.setText(request.getRenterName());
        holder.rentalDurationText.setText(request.getRentalDuration());
        holder.statusText.setText(request.getStatus());

        // Handle Accept or Deny buttons
        holder.acceptButton.setOnClickListener(v -> onRequestActionListener.onAccept(request));
        holder.denyButton.setOnClickListener(v -> onRequestActionListener.onDeny(request));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameText, renterNameText, rentalDurationText, statusText;
        Button acceptButton, denyButton;

        public RequestViewHolder(View itemView) {
            super(itemView);
            itemNameText = itemView.findViewById(R.id.itemName);
            renterNameText = itemView.findViewById(R.id.renterName);
            rentalDurationText = itemView.findViewById(R.id.rentalDuration);
            statusText = itemView.findViewById(R.id.status);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            denyButton = itemView.findViewById(R.id.denyButton);
        }
    }
}