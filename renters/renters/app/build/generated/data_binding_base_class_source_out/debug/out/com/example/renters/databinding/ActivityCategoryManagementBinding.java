// Generated by view binder compiler. Do not edit!
package com.example.renters.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.renters.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityCategoryManagementBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final FloatingActionButton fabAddCategory;

  @NonNull
  public final RecyclerView recyclerViewCategories;

  @NonNull
  public final Toolbar toolbar;

  private ActivityCategoryManagementBinding(@NonNull ConstraintLayout rootView,
      @NonNull FloatingActionButton fabAddCategory, @NonNull RecyclerView recyclerViewCategories,
      @NonNull Toolbar toolbar) {
    this.rootView = rootView;
    this.fabAddCategory = fabAddCategory;
    this.recyclerViewCategories = recyclerViewCategories;
    this.toolbar = toolbar;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityCategoryManagementBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityCategoryManagementBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_category_management, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityCategoryManagementBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.fabAddCategory;
      FloatingActionButton fabAddCategory = ViewBindings.findChildViewById(rootView, id);
      if (fabAddCategory == null) {
        break missingId;
      }

      id = R.id.recyclerViewCategories;
      RecyclerView recyclerViewCategories = ViewBindings.findChildViewById(rootView, id);
      if (recyclerViewCategories == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      return new ActivityCategoryManagementBinding((ConstraintLayout) rootView, fabAddCategory,
          recyclerViewCategories, toolbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
