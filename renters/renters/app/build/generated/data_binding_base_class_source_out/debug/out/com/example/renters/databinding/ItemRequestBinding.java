// Generated by view binder compiler. Do not edit!
package com.example.renters.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.renters.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemRequestBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button acceptButton;

  @NonNull
  public final Button denyButton;

  @NonNull
  public final TextView itemName;

  @NonNull
  public final TextView rentalDuration;

  @NonNull
  public final TextView renterName;

  @NonNull
  public final TextView status;

  private ItemRequestBinding(@NonNull ConstraintLayout rootView, @NonNull Button acceptButton,
      @NonNull Button denyButton, @NonNull TextView itemName, @NonNull TextView rentalDuration,
      @NonNull TextView renterName, @NonNull TextView status) {
    this.rootView = rootView;
    this.acceptButton = acceptButton;
    this.denyButton = denyButton;
    this.itemName = itemName;
    this.rentalDuration = rentalDuration;
    this.renterName = renterName;
    this.status = status;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemRequestBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemRequestBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_request, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemRequestBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.acceptButton;
      Button acceptButton = ViewBindings.findChildViewById(rootView, id);
      if (acceptButton == null) {
        break missingId;
      }

      id = R.id.denyButton;
      Button denyButton = ViewBindings.findChildViewById(rootView, id);
      if (denyButton == null) {
        break missingId;
      }

      id = R.id.itemName;
      TextView itemName = ViewBindings.findChildViewById(rootView, id);
      if (itemName == null) {
        break missingId;
      }

      id = R.id.rentalDuration;
      TextView rentalDuration = ViewBindings.findChildViewById(rootView, id);
      if (rentalDuration == null) {
        break missingId;
      }

      id = R.id.renterName;
      TextView renterName = ViewBindings.findChildViewById(rootView, id);
      if (renterName == null) {
        break missingId;
      }

      id = R.id.status;
      TextView status = ViewBindings.findChildViewById(rootView, id);
      if (status == null) {
        break missingId;
      }

      return new ItemRequestBinding((ConstraintLayout) rootView, acceptButton, denyButton, itemName,
          rentalDuration, renterName, status);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
