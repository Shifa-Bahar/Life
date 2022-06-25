package com.lifepharmacy.application.ui.in_app_popup

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseDialogFragment
import com.lifepharmacy.application.databinding.DailogInAppPopupBinding
import com.lifepharmacy.application.enums.PopupClicked
import com.lifepharmacy.application.model.notifications.InAppNotificationDataModel
import com.lifepharmacy.application.model.notifications.InAppNotificationMainModel
import com.lifepharmacy.application.ui.dashboard.viewmodel.DashboardViewModel
import com.lifepharmacy.application.ui.home.HomeNavigation
import com.lifepharmacy.application.ui.in_app_popup.adapters.ClickPopUpUI
import com.lifepharmacy.application.ui.in_app_popup.adapters.PopUpUIAdapter
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class InAppPopupDialog :
  BaseDialogFragment<DailogInAppPopupBinding>(), ClickTool, ClickPopUpUI {

  private lateinit var inAppPopupUIAdapter: PopUpUIAdapter
  private val dashboardViewModel: DashboardViewModel by activityViewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initLayout()
    observers()
    isCancelable = true


  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogTheme)
  }

  fun initLayout() {
    binding.click = this
    inAppPopupUIAdapter = PopUpUIAdapter(requireActivity(), this)
    binding.rvUi.adapter = inAppPopupUIAdapter

  }

  private fun observers() {
    dashboardViewModel.notificationModel.observe(viewLifecycleOwner, Observer {
      updateUI(it)
    })

  }

  private fun updateUI(notificationModel: InAppNotificationMainModel?) {
    notificationModel?.let {
      it.let {
        binding.isCloseable = it.dismissible
        when (it.mode) {
          "full-screen" -> {
            val layoutParams = binding.clMain?.layoutParams as ConstraintLayout.LayoutParams

            layoutParams.marginEnd = 0
            layoutParams.marginStart = 0
            layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            binding.clMain?.layoutParams = layoutParams
          }
          "popup" -> {
            val layoutParams = binding.clMain?.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.marginEnd = 20
            layoutParams.marginStart = 20
            layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.clMain?.layoutParams = layoutParams
          }

        }
        try {
          binding.clMain.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(it.backgroundColor ?: "#FFFFFF"))
        } catch (e: Exception) {
          e.printStackTrace()
          binding.clMain.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
        }


        inAppPopupUIAdapter.setDataChanged(it.data)

      }
    }

  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
  }

  override fun onCancel(dialog: DialogInterface) {
    super.onCancel(dialog)
  }

  override fun onPause() {
    super.onPause()
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = super.onCreateDialog(savedInstanceState)
    return dialog
  }


  override fun getLayoutRes(): Int {
    return R.layout.dailog_in_app_popup
  }

  override fun onClickBack() {
    dashboardViewModel.changeStatusOfMessage(
      status = "closed",
      id = dashboardViewModel.notificationModel.value?.id.toString()
    )
    if (!dashboardViewModel.notificationInAppList.isNullOrEmpty()) {
      dashboardViewModel.removeInAppPopupQueItem()
      dashboardViewModel.popupClicked.value = PopupClicked.CLOSE
      findNavController().navigateUp()
    } else {
      dismiss()
    }
  }

  override fun onClickButton(item: InAppNotificationDataModel) {
    dashboardViewModel.changeStatusOfMessage(
      status = "viewed",
      id = dashboardViewModel.notificationModel.value?.id.toString()
    )
    dashboardViewModel.notificationModel.value?.conpaignId?.let {
      dashboardViewModel.appManager.persistenceManager.saveCompianID(
        it
      )
    }

    dashboardViewModel.notificationInAppList.clear()
    val homeNavigation = HomeNavigation(requireContext())
    homeNavigation.triggerNavigation(
      key = item?.action?.key ?: "",
      value = item?.action?.value ?: "",
      heading = item?.action?.heading ?: ""
    )
//    dashboardViewModel.buttonNavigation.value = item
//    dashboardViewModel.popupClicked.value = PopupClicked.CLICKED
    findNavController().navigateUp()
  }


}