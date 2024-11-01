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

public final class RoleselectionBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button buttonSelectAdmin;

  @NonNull
  public final Button buttonSelectLessor;

  @NonNull
  public final Button buttonSelectRenter;

  @NonNull
  public final TextView roleSelectionTitle;

  private RoleselectionBinding(@NonNull ConstraintLayout rootView,
      @NonNull Button buttonSelectAdmin, @NonNull Button buttonSelectLessor,
      @NonNull Button buttonSelectRenter, @NonNull TextView roleSelectionTitle) {
    this.rootView = rootView;
    this.buttonSelectAdmin = buttonSelectAdmin;
    this.buttonSelectLessor = buttonSelectLessor;
    this.buttonSelectRenter = buttonSelectRenter;
    this.roleSelectionTitle = roleSelectionTitle;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RoleselectionBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RoleselectionBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.roleselection, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RoleselectionBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonSelectAdmin;
      Button buttonSelectAdmin = ViewBindings.findChildViewById(rootView, id);
      if (buttonSelectAdmin == null) {
        break missingId;
      }

      id = R.id.buttonSelectLessor;
      Button buttonSelectLessor = ViewBindings.findChildViewById(rootView, id);
      if (buttonSelectLessor == null) {
        break missingId;
      }

      id = R.id.buttonSelectRenter;
      Button buttonSelectRenter = ViewBindings.findChildViewById(rootView, id);
      if (buttonSelectRenter == null) {
        break missingId;
      }

      id = R.id.roleSelectionTitle;
      TextView roleSelectionTitle = ViewBindings.findChildViewById(rootView, id);
      if (roleSelectionTitle == null) {
        break missingId;
      }

      return new RoleselectionBinding((ConstraintLayout) rootView, buttonSelectAdmin,
          buttonSelectLessor, buttonSelectRenter, roleSelectionTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}