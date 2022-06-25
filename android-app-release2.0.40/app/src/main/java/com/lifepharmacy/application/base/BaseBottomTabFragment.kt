package com.lifepharmacy.application.base

import android.os.Bundle
import androidx.activity.addCallback
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.utils.popBackStackAllInstances

abstract class BaseBottomTabFragment<DB : ViewDataBinding> : BaseFragment<DB>() {

  var isNavigated = false






  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  fun navigate(resId: Int, bundle: Bundle? = null) {
    isNavigated = true
    findNavController().navigate(resId, bundle)
  }


  override fun onDestroyView() {
    super.onDestroyView()
    if (!isNavigated)
      requireActivity().onBackPressedDispatcher.addCallback(this) {
        val navController = findNavController()
        if (navController.currentBackStackEntry?.destination?.id != null) {
          findNavController().popBackStackAllInstances(
            navController.currentBackStackEntry?.destination?.id!!,
            true
          )
        } else
          navController.popBackStack()
      }
  }
}