package com.lifepharmacy.application.ui.notifications.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentNotificationsBinding
import com.lifepharmacy.application.managers.analytics.notificationListScreenOpen
import com.lifepharmacy.application.model.NotificationsModel
import com.lifepharmacy.application.model.notifications.NotificationModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.notifications.viewmodels.NotificationsViewModel
import com.lifepharmacy.application.ui.notifications.adapters.ClickItemNotification
import com.lifepharmacy.application.ui.notifications.adapters.NotificationsAdapter
import com.lifepharmacy.application.ui.orders.fragments.OrderDetailFragment
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.AnalyticsUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>(), ClickItemNotification,
  ClickTool {
  private val viewModel: NotificationsViewModel by viewModels()
  lateinit var notificationAdapter: NotificationsAdapter

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.notificationListScreenOpen()
    if (view == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      observers()
    }
    reselectionProfileObserver()

    return mView
  }

  private fun initUI() {
    binding.toolbarTitle.click = this
    binding.toolbarTitle.tvToolbarTitle.text = getString(R.string.notifications)
    notificationAdapter = NotificationsAdapter(requireActivity(), this)
    binding.rvItems.adapter = notificationAdapter
  }

  fun reselectionProfileObserver() {
    viewModel.appManager.loadingState.profileReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
          findNavController().navigate(R.id.nav_profile)
          viewModel.appManager.loadingState.profileReselected.value = false
        }
      }
    }
  }

  private fun observers() {
    viewModel.getNotifications().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            notificationAdapter.setDataChanged(it.data?.data)
            binding.showEmpty = it.data?.data.isNullOrEmpty()
          }
          Result.Status.ERROR -> {
            hideLoading()
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
          }
          Result.Status.LOADING -> {
            showLoading()
          }
        }
      }
    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_notifications
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }

  override fun onClickNotification(notification: NotificationModel) {
    if (notification.type == "transactional") {
      if (notification.entity == "order") {
        requireActivity().findNavController(R.id.fragment).navigate(
          R.id.nav_order_details,
          OrderDetailFragment.getOrderDetailBundle(notification.entityId.toString())
        )
      }
    }
//    findNavController().navigate(R.id.notificationDetailFragment)
  }
}
