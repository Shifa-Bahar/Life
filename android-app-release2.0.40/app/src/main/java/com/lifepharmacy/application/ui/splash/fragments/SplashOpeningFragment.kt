package com.lifepharmacy.application.ui.splash.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentSplashFirstPageBinding
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.ui.auth.AuthActivity
import com.lifepharmacy.application.ui.auth.viewmodel.AuthViewModel
import com.lifepharmacy.application.ui.dashboard.DashboardActivity
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.ConstantsUtil.PERMISSION_LOCATIONS_REQUEST_CODE
import dagger.hilt.android.AndroidEntryPoint
import me.ibrahimsn.lib.PhoneNumberKit


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class SplashOpeningFragment : BaseFragment<FragmentSplashFirstPageBinding>() {
  private val viewModel: AuthViewModel by activityViewModels()
  private val requestLocationPermissions =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
      var result = true
      granted.entries.forEach {
        if (!it.value) {
          result = false
          return@forEach
        }
      }
      if (result) {
        locationPermissionGranted()
      } else {
        AlertManager.permissionRequestPopup(requireActivity())
      }

    }
  lateinit var phoneNumberKit: PhoneNumberKit
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    mView = super.onCreateView(inflater, container, savedInstanceState)

    initUI()
    observers()
    requestLocationPermissions.launch(ConstantsUtil.getLocationListNormal())
    return mView
  }

  @SuppressLint("ClickableViewAccessibility")
  fun initUI() {


  }


  private fun observers() {

  }

  private fun startApp() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      DashboardActivity.open(requireActivity())
    } else {
      AuthActivity.open(requireActivity())
    }
//        val intent = Intent(requireContext(), AuthActivity::class.java)
//        startActivity(intent)
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_splash_first_page
  }

  override fun permissionGranted(requestCode: Int) {
    if (requestCode == PERMISSION_LOCATIONS_REQUEST_CODE) {
//            CoroutineScope(Dispatchers.Main.immediate).launch {
//            delay(1000)
//                startApp()
//            }

    }
  }

  private fun locationPermissionGranted() {

  }


}
