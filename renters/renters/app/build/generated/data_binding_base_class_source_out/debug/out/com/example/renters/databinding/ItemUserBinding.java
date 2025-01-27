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

public final class ItemUserBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button buttonDelete;

  @NonNull
  public final Button buttonDisable;

  @NonNull
  public final TextView textViewEmail;

  @NonNull
  public final TextView textViewRole;

  @NonNull
  public final TextView textViewUserName;

  private ItemUserBinding(@NonNull ConstraintLayout rootView, @NonNull Button buttonDelete,
      @NonNull Button buttonDisable, @NonNull TextView textViewEmail,
      @NonNull TextView textViewRole, @NonNull TextView textViewUserName) {
    this.rootView = rootView;
    this.buttonDelete = buttonDelete;
    this.buttonDisable = buttonDisable;
    this.textViewEmail = textViewEmail;
    this.textViewRole = textViewRole;
    this.textViewUserName = textViewUserName;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemUserBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemUserBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_user, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemUserBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonDelete;
      Button buttonDelete = ViewBindings.findChildViewById(rootView, id);
      if (buttonDelete == null) {
        break missingId;
      }

      id = R.id.buttonDisable;
      Button buttonDisable = ViewBindings.findChildViewById(rootView, id);
      if (buttonDisable == null) {
        break missingId;
      }

      id = R.id.textViewEmail;
      TextView textViewEmail = ViewBindings.findChildViewById(rootView, id);
      if (textViewEmail == null) {
        break missingId;
      }

      id = R.id.textViewRole;
      TextView textViewRole = ViewBindings.findChildViewById(rootView, id);
      if (textViewRole == null) {
        break missingId;
      }

      id = R.id.textViewUserName;
      TextView textViewUserName = ViewBindings.findChildViewById(rootView, id);
      if (textViewUserName == null) {
        break missingId;
      }

      return new ItemUserBinding((ConstraintLayout) rootView, buttonDelete, buttonDisable,
          textViewEmail, textViewRole, textViewUserName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
