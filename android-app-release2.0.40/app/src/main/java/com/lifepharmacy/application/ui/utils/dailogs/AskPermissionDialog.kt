package com.lifepharmacy.application.ui.utils.dailogs

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseDialogFragment
import com.lifepharmacy.application.databinding.DailogAnimationTitleDescReturnBinding
import com.lifepharmacy.application.databinding.DailogAskPermissionBinding
import com.lifepharmacy.application.databinding.DailogInputActionBinding
import com.lifepharmacy.application.ui.dashboard.viewmodel.DashboardViewModel
import com.lifepharmacy.application.ui.orders.viewmodels.OrderDetailViewModel
import com.lifepharmacy.application.utils.DateTimeUtil
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class AskPermissionDialog : BaseDialogFragment<DailogAskPermissionBinding>(),
  ClickAskPermissionDialog {
  var onClickAskPermissionDialog: ClickAskPermissionDialog? = null
  fun setInputDialogActionListener(dialogResult: ClickAskPermissionDialog) {
    onClickAskPermissionDialog = dialogResult
  }

  lateinit var title: String

  private val dashboardViewModel: DashboardViewModel by activityViewModels()

  private val requestLocationPermission =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
      var result = true
      granted.entries.forEach {
        if (!it.value) {
          result = false
          return@forEach
        }
      }
      if (result) {
        findNavController().navigateUp()
      } else {
        findNavController().navigateUp()
      }

    }

  companion object {
    const val TAG = "InputActionDialog"
    var isAlreadyOpen: Boolean = false
    fun newInstance(
      title: String,
    ): AskPermissionDialog {

      val dialog = AskPermissionDialog()
      val bundle = Bundle()
      bundle.putString("title", title)
      dialog.arguments = bundle
      isAlreadyOpen = true
      return dialog
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
    isCancelable = false
    dashboardViewModel.appManager.persistenceManager.saveAskPermissionOpenDateTime(
      DateTimeUtil.getUTCStringFromDate(DateTimeUtil.getCurrentDateObject())
    )

  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.FullScreenTransDialogTheme)
    arguments?.let {
      title = it.getString("title").toString()
    }

  }

  private fun initUI() {
    binding.click = this
  }

  override fun getLayoutRes(): Int {
    return R.layout.dailog_ask_permission
  }

  override fun onClickEnablePermission() {
    requestLocationPermission.launch(ConstantsUtil.getLocationListNormal())
  }

}
