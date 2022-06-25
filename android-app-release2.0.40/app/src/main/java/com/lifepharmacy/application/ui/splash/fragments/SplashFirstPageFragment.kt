package com.lifepharmacy.application.ui.splash.fragments

import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieCompositionFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentSplashFirstPageBinding
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.LocationCustomManager
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.auth.AuthActivity
import com.lifepharmacy.application.ui.dashboard.DashboardActivity
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.splash.viewmodel.SplashViewModel
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.lifepharmacy.application.utils.universal.GpsStatus
import com.lifepharmacy.application.utils.universal.IntentAction
import com.lifepharmacy.application.utils.universal.Logger
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.*
import java.lang.Exception
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class SplashFirstPageFragment : BaseFragment<FragmentSplashFirstPageBinding>(),
  LocationCustomManager.CustomeLocationCallback {
  private val viewModel: SplashViewModel by viewModels()
  private val viewModelProfile: ProfileViewModel by activityViewModels()

  private val requestPhotoPermissions =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
      var result = true
      granted.entries.forEach {
        if (!it.value) {
          result = false
          return@forEach
        }
      }
      if (result) {
        checkAndShowAnimation()
      }

    }

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


  var opened = false
  lateinit var permissionX: List<String>

  private var locationCustomManager: LocationCustomManager? = null
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    mView = super.onCreateView(inflater, container, savedInstanceState)
//        requestSpecificPermission(
//            PERMISSION_LOCATIONS_REQUEST_CODE,
//            Constants.RequiredPermissionsLocations
//        )Build.VERSION.SDK_INT >= Build.VERSION_CODES.P


    checkAndShowAnimation()
    hideLoading()
//    CoroutineScope(Dispatchers.Main.immediate).launch {
//      delay(500)
//      requestPermissions()
//    }
    checkToken()

    requestPhotoPermissions.launch(ConstantsUtil.RequiredPermissionsPicture)

    return mView
  }

  private fun checkAndShowAnimation() {
    if (appManager.storageManagers.getDownloadedFile() != null) {
      Logger.d("animationFilePath", appManager.storageManagers.getDownloadedFile().toString())
      setAnimationFromLocal(appManager.storageManagers.getDownloadedFile())
    } else {
      playLocationAnimation()
    }

  }

  private fun playLocationAnimation() {
    binding.animationView.setAnimation("splash_small.json")
    showAnimations()
  }

  private fun showAnimations() {
    binding.animationView.visibility = View.VISIBLE
    binding.animationView.repeatCount = 0
    binding.animationView.playAnimation()
    binding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
      override fun onAnimationStart(animation: Animator?) {
        Log.e("Animation:", "start")
      }

      override fun onAnimationEnd(animation: Animator?) {
        Log.e("Animation:", "end")
        //Your code for remove the fragment
        CoroutineScope(Dispatchers.Main.immediate).launch {
          delay(200)
          requestLocationPermissions.launch(ConstantsUtil.getLocationListNormal())
        }
      }

      override fun onAnimationCancel(animation: Animator?) {
        Log.e("Animation:", "cancel")
      }

      override fun onAnimationRepeat(animation: Animator?) {
        Log.e("Animation:", "repeat")
      }
    })
  }

  //  private fun hideAnimations() {
//    binding.animationView.visibility = View.GONE
//  }
//  private fun requestPermissions() {
//    PermissionX.init(requireActivity())
//      .permissions(ConstantsUtil.getLocationList())
//      .request(RequestCallback { allGranted: Boolean, list: List<String?>?, list1: List<String?>? ->
//        if (allGranted) {
//          onStartFunction()
//          locationCustomManager = LocationCustomManager(requireActivity())
//          locationCustomManager?.setLocationCallbackListener(this)
//          locationCustomManager?.startLocationServices()
//
//        } else {
//          Toast.makeText(
//            requireActivity(),
//            "Please provide  permission ",
//            Toast.LENGTH_LONG
//          ).show()
//        }
//      })
//  }


  private fun checkToken() {


  }

  private fun onStartFunction() {
    viewModel.getFilters()
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      checkUserToken()
    } else {
      viewModelProfile.isLoggedIn.set(false)
      AuthActivity.open(requireActivity())
    }
    observers()
    viewModel.gpsStatusListener.observe(viewLifecycleOwner, Observer { gpsStatus ->
      when (gpsStatus) {
        is GpsStatus.Enabled -> {
          locationCustomManager?.startLocationServices()

        }
        is GpsStatus.Disabled -> {
          locationCustomManager?.stopLocationServices()
        }
      }
    })


//        Handler(Looper.getMainLooper()).postDelayed({
//            //Do something after 100ms\
//            startApp()
//        }, 100)


//        val handler = Handler(Looper.getMainLooper())
//        handler.postDelayed({
//            //Do something after 100ms
//        }, 100)
//        startApp()
//        startApp()
//        Thread(Runnable {
//            doWork()
//            startApp()
//        }).start()
//        CoroutineScope(Dispatchers.IO).launch {
////            delay(1000)
//            CoroutineScope(Dispatchers.Main.immediate).launch {
//                delay(1000)
//        val handler = Handler(Looper.getMainLooper())
//        handler.postDelayed({
//            //Do something after 100ms
//            AuthActivity.open(requireActivity())
//        }, 100)

//
//            if (viewModel.appManager.persistenceManager.isLoggedIn()){
//                HomeActivity.open(requireActivity())
//            }else{
//                AuthActivity.open(requireActivity())
//            }

//            }
//        }
//        TimeUnit.SECONDS.sleep(1);

//        Thread(Runnable {
//            doWork()
//            startApp()
//        }).start()
//        startApp()
  }

  private fun observers() {}

  //Observing App token
//    viewModel.getFilters()
//      .observe(viewLifecycleOwner, Observer {
//        when (it.status) {
//          Result.Status.SUCCESS -> {
////                        hideLoading()
//            it.data?.data?.filters?.let { it1 ->
//              viewModel.filtersManager.updateAllFilters(
//                it1
//              )
//            }
//            if (viewModel.appManager.persistenceManager.isLoggedIn()) {
//              checkUserToken()
//            } else {
//              viewModelProfile.isLoggedIn.set(false)
//              AuthActivity.open(requireActivity())
//            }
//          }
//          Result.Status.ERROR -> {
//            hideLoading()
//            it.message?.let { it1 ->
//              AlertManager.showErrorMessage(
//                requireActivity(),
//                it1
//              )
//            }
//          }
//          Result.Status.LOADING -> {
//            showLoading()
//          }
//        }
//      })
//  }

//    private fun doWork() {
//        var progress = 0
//        while (progress < 100) {
//            try {
//                Thread.sleep(500)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            progress += 20
//        }
//    }

//    private fun startApp() {
//        if (viewModel.appManager.persistenceManager.isLoggedIn()) {
//            DashboardActivity.open(requireActivity())
//        } else {
//            AuthActivity.open(requireActivity())
//        }
////        val intent = Intent(requireContext(), AuthActivity::class.java)
////        startActivity(intent)
//    }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_splash_first_page
  }

  override fun permissionGranted(requestCode: Int) {
    if (requestCode == ConstantsUtil.PERMISSION_PICTURE_REQUEST_CODE) {

    }
  }

  private fun locationPermissionGranted() {
    locationCustomManager = LocationCustomManager(requireActivity())
    locationCustomManager?.setLocationCallbackListener(this)
    locationCustomManager?.startLocationServices()
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
      viewModel.appManager.persistenceManager.saveCountry(it.countryCode.toLowerCase(Locale.ROOT))
//            if (viewModel.appManager.persistenceManager.isLoggedIn()) {
//                DashboardActivity.open(requireActivity())
//            } else {
//                AuthActivity.open(requireActivity())
//            }
    }

    locationCustomManager?.stopLocationServices()
//        if (!opened){
//            CoroutineScope(Dispatchers.Main.immediate).launch {
//                delay(500)
//                opened = true
//                if (viewModel.appManager.persistenceManager.isLoggedIn()) {
//                    DashboardActivity.open(requireActivity())
//                } else {
//                    AuthActivity.open(requireActivity())
//                }
//            }
//        }

  }

  private fun checkUserToken() {
    viewModel.checkToken()
      .observe(viewLifecycleOwner, Observer {
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
            hideLoading()
            viewModelProfile.isLoggedIn.set(true)
            DashboardActivity.open(requireActivity())
          }
          Result.Status.ERROR -> {
            hideLoading()
            it.message?.let { it1 ->
              AlertManager.showErrorMessage(
                requireActivity(),
                it1
              )
            }
            DashboardActivity.open(requireActivity())

//            viewModel.appManager.persistenceManager.clearPrefs()
//            AuthActivity.open(requireActivity())
          }
          Result.Status.LOADING -> {
//            showLoading()
          }
        }
      })
  }

  private fun setAnimationFromLocal(filePath: String?) {
    try {
      val BUFFER_SIZE = 1024
      var file = File(filePath)
      Logger.d("animationFilePath", file.absolutePath.toString())
      var uri = file.toURI()
      Logger.d("animationFilePath2", uri.path.toString())
      var localString = uri.path.toString().replace("/file:", "")
      Logger.d("animationFilePath2", localString)
      val inputStream = BufferedInputStream(FileInputStream(localString), BUFFER_SIZE)
      val task = LottieCompositionFactory.fromJsonInputStream(inputStream, null)
      task.addListener {
        binding.animationView.setComposition(it)
        showAnimations()
      }.addFailureListener {
        // do something on fail
        playLocationAnimation()
      }
    } catch (e: Exception) {
      e.printStackTrace()
      playLocationAnimation()
    }


//    val localFile = File(filePath)
//    val inputStream: InputStream? = null
//    if (localFile.exists()) {
//      if (localFile.absolutePath.endsWith(".json")) {
//        binding.animationView.setCompositionTask(
//          LottieCompositionFactory.fromJsonInputStream(
//            FileInputStream(filePath),
//            localFile.name
//          )
//        )
//      } else if (localFile.absolutePath.endsWith(".zip")) {
//        setCompositionTask(
//          LottieCompositionFactory.fromZipStream(
//            ZipInputStream(
//              FileInputStream(
//                filePath
//              )
//            ), localFile.name
//          )
//        )
//      } else {
//        L.debug("file extention only be .zip or .json")
//      }
//    } else {
//      L.debug("setAnimationFromLocal but file not exist")
//    }
  }
}
