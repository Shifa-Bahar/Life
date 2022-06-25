package com.lifepharmacy.application.ui.splash

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseApp
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseActivity
import com.lifepharmacy.application.databinding.ActivitySplashActivtiyBinding
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.LocationCustomManager
import com.lifepharmacy.application.managers.analytics.addToCart
import com.lifepharmacy.application.managers.analytics.cartScreenOpen
import com.lifepharmacy.application.managers.analytics.splashScreenOpen
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.auth.AuthActivity
import com.lifepharmacy.application.ui.cart.fragmets.AnimationOfferCompleteCouponDialog
import com.lifepharmacy.application.ui.dashboard.DashboardActivity
import com.lifepharmacy.application.ui.dashboard.DashboardWithNativeBottomActivity
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.splash.viewmodel.SplashViewModel
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.GpsStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashActivtiyBinding>(),
  LocationCustomManager.CustomeLocationCallback {
  private val viewModel: SplashViewModel by viewModels()
  private var locationCustomManager: LocationCustomManager? = null

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
        AlertManager.permissionRequestPopup(this)
      }

    }

  companion object {
    fun open(activity: Activity) {
      val intent = Intent(activity, SplashActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      activity.startActivity(intent)
//            activity.overridePendingTransition(R.anim.up_from_bottom, R.anim.fade_out)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
//    setTheme(R.style.splashTheme_Launcher)
//    window.decorView.systemUiVisibility =
//      View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    super.onCreate(savedInstanceState)
    FirebaseApp.initializeApp(this)
//        var navController = Navigation.findNavController(this, R.id.fragment)
    viewModel.storageManagers.getSettings()
    viewModel.storageManagers.getNewAvailableSlots()
    requestLocationPermissions.launch(ConstantsUtil.getLocationListNormal())
//    viewModel.appManager.persistenceManager.getLoggedInUser()?.let { AnalyticsUtil.setIdentify(it) }
    CoroutineScope(Dispatchers.Main.immediate).launch {
      delay(500)
      onStartFunction()
    }
    val action: String? = intent?.action
    val data: Uri? = intent?.data

//  var test= AppsFlyerLib.getInstance().
//    Log.e(TAG, "onCreate:testing utf $test")
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//        //If the draw over permission is not available open the settings screen
//        //to grant the permission.
//        val intent = Intent(
//          Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//          Uri.parse("package:$packageName")
//        )
//        startActivityForResult(intent, 2084)
//      } else {
//        App.getInstance().initAndzu()

    viewModel.appManager.analyticsManagers.splashScreenOpen()
  }

//    }
//  override fun onActivityResult(
//    requestCode: Int,
//    resultCode: Int,
//    data: Intent?
//  ) {
//    if (requestCode == 2084) {
//      //Check if the permission is granted or not.
//      if (resultCode == Activity.RESULT_OK) {
//        App.getInstance().initAndzu()
//      } else { //Permission is not available
//      }
//    } else {
//      super.onActivityResult(requestCode, resultCode, data)
//    }
//  }
//    private fun startApp() {
//        AuthActivity.open(this)
////        val intent = Intent(this, AuthActivity::class.java)
////        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
////        startActivity(intent)
//    }

  override fun getLayoutRes(): Int {

    return R.layout.activity_splash_activtiy
  }


  override fun permissionGranted(requestCode: Int) {

  }

  private fun locationPermissionGranted() {
    locationCustomManager = LocationCustomManager(this)
    locationCustomManager?.setLocationCallbackListener(this)
    locationCustomManager?.startLocationServices()
  }

  override fun getLoadingLayout(): ConstraintLayout {
    return binding.llLoading.clLoading
  }

  override fun onChange(location: Location?) {

  }

  override fun address(address: Address?) {
    address?.let {
      viewModel.appManager.storageManagers.saveLatLng(
        LatLng(
          it.latitude,
          it.longitude
        )
      )
      try {
        viewModel.appManager.persistenceManager.saveCountry(it.countryCode?.toLowerCase(Locale.ROOT))
      } catch (e: Exception) {
        viewModel.appManager.persistenceManager.saveCountry("ae")
      }

//            if (viewModel.appManager.persistenceManager.isLoggedIn()) {
//                DashboardActivity.open(requireActivity())
//            } else {
//                AuthActivity.open(requireActivity())
//            }
    }

    locationCustomManager?.stopLocationServices()
  }

  private fun onStartFunction() {
    viewModel.getFilters()
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      checkUserToken()
    } else {
      AuthActivity.open(this)
    }
    viewModel.gpsStatusListener.observe(this) { gpsStatus ->
      when (gpsStatus) {
        is GpsStatus.Enabled -> {
          locationCustomManager?.startLocationServices()

        }
        is GpsStatus.Disabled -> {
          locationCustomManager?.stopLocationServices()
        }
      }
    }

  }


  private fun checkUserToken() {
    viewModel.checkToken()
      .observe(this) {
        when (it.status) {
          Result.Status.SUCCESS -> {
            it.data?.data?.token?.let { token ->
              viewModel.appManager.persistenceManager.saveToken(
                token
              )
            }
            it.data?.data?.user?.let { user ->
              viewModel.appManager.persistenceManager.saveLoggedInUser(user)
            }
            DashboardActivity.open(this)
          }
          Result.Status.ERROR -> {
            AuthActivity.open(this)

          }
          Result.Status.LOADING -> {
//            showLoading()
          }
        }
      }
  }
}