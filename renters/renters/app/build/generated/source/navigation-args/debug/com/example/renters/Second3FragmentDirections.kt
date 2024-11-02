package com.example.renters

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections

public class Second3FragmentDirections private constructor() {
  public companion object {
    public fun actionSecond3FragmentToFirst3Fragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_Second3Fragment_to_First3Fragment)
  }
}
