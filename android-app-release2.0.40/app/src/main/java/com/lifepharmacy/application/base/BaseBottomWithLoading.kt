package com.lifepharmacy.application.base

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kaopiz.kprogresshud.KProgressHUD
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.ui.utils.dailogs.LoadingDialog
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import javax.inject.Inject

abstract class BaseBottomWithLoading<DB : ViewDataBinding> :
  BottomSheetDialogFragment() {
  abstract fun permissionGranted(requestCode: Int)
  lateinit var permissions: Array<String>
  var Permisions = false
  var mainRequestCode: Int? = null
  lateinit var binding: DB
  private var dailog: KProgressHUD? = null
  var loadinDialog: LoadingDialog? = null
  abstract fun getLoadingLayout(): ConstraintLayout

  @Inject
  lateinit var appManager: AppManager

  @LayoutRes
  abstract fun getLayoutRes(): Int

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
    appManager.loadingState.getLoadingState().observe(this, Observer {
      it?.let {
        if (it) {
          showLoadingAnimation()
//          getLoadingLayout().visibility = View.VISIBLE
        } else {
          stopLoading()
//          getLoadingLayout().visibility = View.GONE
        }
      }
    })
    return binding.root
  }

  fun requestSpecificPermission(requestCode: Int?, permissions: Array<String>) {
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
            } else {
              Permisions = true
              permissionGranted(requestCode)
            }
            i++
          }
        }
      }
    }
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

  open fun showLoadingAnimation() {

    getLoadingLayout().visibility = View.VISIBLE
//    showDailog()
  }

  open fun stopLoading() {

    getLoadingLayout().visibility = View.GONE
//    dismisDailog()
  }

  protected fun showLoading() {
    appManager.loadingState.setLoadingState(true)
//    if(loadinDialog == null){
//      loadinDialog = LoadingDialog.newInstance()
//      loadinDialog?.show(
//        childFragmentManager,
//        ImageSelectBottomSheet.TAG
//      )
//    }else{
//      loadinDialog?.show(childFragmentManager, ImageSelectBottomSheet.TAG)
//    }
//    dailog = KProgressHUD.create(requireContext())
//      .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//      .setCancellable(false)
//      .setAnimationSpeed(2)
//      .setDimAmount(0.1f)
//      .show()
  }

  protected fun hideLoading() {
    appManager.loadingState.setLoadingState(false)
//    loadinDialog?.dismiss()
//    dailog?.dismiss()
  }
}