package com.example.renters

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections

public class First3FragmentDirections private constructor() {
  public companion object {
    public fun actionFirst3FragmentToSecond3Fragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_First3Fragment_to_Second3Fragment)
  }
}
