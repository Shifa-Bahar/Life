package com.lifepharmacy.application.base

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.kaopiz.kprogresshud.KProgressHUD
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import com.airbnb.lottie.LottieAnimationView
import com.appsflyer.AppsFlyerLib
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.ui.cart.fragmets.AnimationOfferCompleteCouponDialog
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet
import com.lifepharmacy.application.ui.utils.dailogs.LoadingDialog
import javax.inject.Inject

abstract class BaseFragment<DB : ViewDataBinding
//    ,loading:ConstraintLayout
    > : Fragment() {

  @Inject
  lateinit var  appManager: AppManager

  lateinit var binding: DB
//  lateinit var loading: loading
  var mView: View? = null
  var Permisions = true
  var intent: Intent? = null
  lateinit var permissions: Array<String>
  var mainRequestCode: Int? = null
  private var dailog: KProgressHUD? = null
  var animationView:LottieAnimationView? = null
  var loadingDialog: LoadingDialog? = null

  @LayoutRes
  abstract fun getLayoutRes(): Int

//  abstract fun getLoadingLayout():ConstraintLayout

  abstract fun permissionGranted(requestCode: Int)


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DataBindingUtil.inflate(
      inflater,
      getLayoutRes(), container, false
    )

    val action: String? = intent?.action
    val data: Uri? = intent?.data

    hideLoading()
    var test= AppsFlyerLib.getInstance()
    Log.e(DocTypSelectionBottomSheet.TAG, "onCreate:testing utf base$test")
    return binding.root
  }

  fun requestSpecificPermission(requestCode: Int?, permissions: Array<String>) {
    Permisions = true
    mainRequestCode = requestCode
    this.permissions = permissions

    if (mainRequestCode == ConstantsUtil.PERMISSION_LOCATIONS_REQUEST_CODE) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        this.permissions = ConstantsUtil.RequiredPermissionsLocationsAboveQ

      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        this.permissions = ConstantsUtil.RequiredPermissionsLocationsAboveP

      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.permissions = ConstantsUtil.RequiredPermissionsLocations
      }
    }

    if (!checkPermissionStatus(permissions)) {
      requestCode?.let {
        requestPermissions(
          this.permissions,
          it
        )
      }
    } else {
      requestCode?.let {
        permissionGranted(it)
      }
    }


  }

  fun checkPermissionStatus(permissions: Array<String>): Boolean {
    var has = false
    var cases = 0
    for (item in permissions) {
      if ((ContextCompat.checkSelfPermission(
          requireContext(),
          item
        ) == PackageManager.PERMISSION_GRANTED)
      ) {
        cases++
      }
    }

    if (cases == permissions.size) {
      has = true
    }

    return has
  }


  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String?>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    when (requestCode) {
      mainRequestCode -> {
        if (grantResults.isNotEmpty()) {
          var i = 0
          while (i < grantResults.size) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
              Permisions = false
              AlertManager.showErrorMessage(requireActivity(),"Please Provide Permissions to use this feature")
            }
            i++
          }
          if (Permisions){
            permissionGranted(requestCode)
          }
        }
      }
    }
  }
  ///PERMISSION X TRY


//    /////REQUEST PERMISSIONS///////////////////
//    private fun requestPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            PermissionX.init(requireActivity())
//                .permissions(Constants.RequiredPermissionsAboveQ)
//                .request(RequestCallback { allGranted: Boolean, list: List<String?>?, list1: List<String?>? ->
//                    if (allGranted) {
//
//                    } else {
//                        Toast.makeText(
//                            requireActivity(),
//                            "Please provide  permission",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                })
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//
//            PermissionX.init(requireActivity())
//                .permissions(Constants.RequiredPermissionsAboveP)
//                .request(RequestCallback { allGranted: Boolean, list: List<String?>?, list1: List<String?>? ->
//                    if (allGranted) {
//
//                    } else {
//                        Toast.makeText(
//                            requireActivity(),
//                            "Please provide  permission",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                })
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            PermissionX.init(requireActivity())
//                .permissions(Constants.RequiredPermissions)
//                .request(RequestCallback { allGranted: Boolean, list: List<String?>?, list1: List<String?>? ->
//                    if (allGranted) {
//
//                    } else {
//                        Toast.makeText(
//                            requireActivity(),
//                            "Please provide  permission ",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                })
//        }
//    }
//
//    fun requestSpecificPermission(requestCode: Int?, permissions: List<String>) {
//        mainRequestCode = requestCode
//        this.permissions = permissions
//        if (mainRequestCode == Constants.PERMISSION_LOCATIONS_REQUEST_CODE) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                this.permissions = Constants.RequiredPermissionsLocationsAboveQ
//
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                this.permissions = Constants.RequiredPermissionsLocationsAboveP
//
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                this.permissions = Constants.RequiredPermissionsLocations
//            }
//        }
//
//        PermissionX.init(requireActivity())
//            .permissions(this.permissions)
//            .request(RequestCallback { allGranted: Boolean, list: List<String?>?, list1: List<String?>? ->
//                if (allGranted) {
//                    requestCode?.let {
//                        permissionGranted(it)
//                    }
//                } else {
//                    Toast.makeText(
//                        requireActivity(),
//                        "Please provide  permission ",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            })
//
//
//    }

  protected fun showLoading() {
    appManager.loadingState.setLoadingState(true)
//    loading.visibility = View.VISIBLE
//    animationView = LottieAnimationView(requireContext())
//    animationView?.setAnimation("loading_main.json")
//    animationView?.repeatCount = 5
//    animationView?.visibility =  View.VISIBLE
//    animationView?.playAnimation()


//    if(loadingDialog == null){
//      loadingDialog = LoadingDialog.newInstance()
//      loadingDialog?.show(
//        childFragmentManager,
//        ImageSelectBottomSheet.TAG
//      )
//    }else{
//      loadingDialog?.show(childFragmentManager,ImageSelectBottomSheet.TAG)
//    }


//    binding.bottomNavigation.llCart.animationView.setAnimation(list[lastIndex].unselectedAnimation)
//    binding.bottomNavigation.llCart.animationView.playAnimation()
//    dailog = KProgressHUD.create(requireContext())
//      .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//      .setCancellable(false)
//      .setAnimationSpeed(2)
//      .setDimAmount(0.1f)
//      .show()
  }

  protected fun hideLoading() {
    appManager.loadingState.setLoadingState(false)
//    loadingDialog?.dismiss()
//    loading.visibility = View.GONE
//    animationView?.pauseAnimation()
//    animationView?.visibility =  View.GONE
//    dailog?.dismiss()
  }


  fun showMessageOKCancel(
    message: String,
    okListener: (Any, Any) -> Unit,
    cancelListener: (Any, Any) -> Unit
  ) {
    AlertDialog.Builder(requireContext())
      .setMessage(message)
      .setPositiveButton("OK", okListener)
      .setNegativeButton("Cancel", cancelListener)
      .create()
      .show()
  }

  override fun onStart() {
    super.onStart()
  }

  override fun onDestroy() {
    super.onDestroy()
  }
}