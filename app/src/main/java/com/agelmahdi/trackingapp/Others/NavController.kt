package com.agelmahdi.trackingapp.Others

import android.view.View
import androidx.navigation.fragment.NavHostFragment

import androidx.fragment.app.Fragment

import androidx.navigation.NavController
import androidx.navigation.Navigation
import java.lang.IllegalStateException


fun findNavController(fragment: Fragment): NavController {
    var findFragment: Fragment = fragment
    while (findFragment != null) {
        if (findFragment is NavHostFragment) {
            return (findFragment as NavHostFragment).navController
        }
        val primaryNavFragment: Fragment? = findFragment.getParentFragmentManager()
            .getPrimaryNavigationFragment()
        if (primaryNavFragment is NavHostFragment) {
            return (primaryNavFragment as NavHostFragment).navController
        }
        findFragment = findFragment.requireParentFragment()
    }
    // Try looking for one associated with the view instead, if applicable
    val view: View? = fragment.getView()
    if (view != null) {
        return Navigation.findNavController(view)
    }
    throw IllegalStateException(
        "Fragment " + fragment
                + " does not have a NavController set"
    )
}