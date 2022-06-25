package com.lifepharmacy.application.base

import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kaopiz.kprogresshud.KProgressHUD
import com.lifepharmacy.application.R
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.ui.utils.dailogs.LoadingDialog
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import javax.inject.Inject
import kotlin.math.roundToInt

abstract class BaseBottomUpRatioScreenSheet<DB : ViewDataBinding>(private val ration: Double = 0.5) :
  BottomSheetDialogFragment() {
  abstract fun permissionGranted(requestCode: Int)
  lateinit var permissions: Array<String>
  var Permisions = false
  var mainRequestCode: Int? = null
  lateinit var binding: DB
  private var dailog: KProgressHUD? = null
  var loadinDialog: LoadingDialog? = null

  @Inject
  lateinit var appManager: AppManager

  @LayoutRes
  abstract fun getLayoutRes(): Int
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog: Dialog = super.onCreateDialog(savedInstanceState)
    dialog.setOnShowListener(DialogInterface.OnShowListener { dialogInterface ->
      val bottomSheetDialog = dialogInterface as BottomSheetDialog
      setupFullHeight(bottomSheetDialog)
    })
    return dialog
  }

  private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
    val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
    val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!)
    val layoutParams = bottomSheet.layoutParams
    val windowHeight = getWindowHeight()
    if (layoutParams != null) {
      layoutParams.height = (windowHeight * ration).roundToInt()
    }
    bottomSheet.layoutParams = layoutParams
    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
  }

  private fun getWindowHeight(): Int {
    // Calculate window height for fullscreen use
    val displayMetrics = DisplayMetrics()
    requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
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
              AlertManager.showErrorMessage(
                requireActivity(),
                "Please Provide Permissions to use this feature"
              )
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