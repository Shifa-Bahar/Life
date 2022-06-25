package com.lifepharmacy.application.ui.notifications.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentNotificationsDetailsBinding
import com.lifepharmacy.application.managers.analytics.notificationDetailScreenOpen
import com.lifepharmacy.application.ui.notifications.viewmodels.NotificationsViewModel
import com.lifepharmacy.application.ui.whishlist.adapters.ClickItemWish
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.AnalyticsUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class NotificationDetailFragment : BaseFragment<FragmentNotificationsDetailsBinding>(),
  ClickItemWish, ClickTool {
  private val viewModel: NotificationsViewModel by viewModels()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.notificationDetailScreenOpen()
    if (view == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      observers()
    }

    return mView
  }

  private fun initUI() {
    binding.toolbarTitle.click = this
    binding.toolbarTitle.tvToolbarTitle.text = getString(R.string.notifications)
  }


  private fun observers() {

  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_notifications_details
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }
}
