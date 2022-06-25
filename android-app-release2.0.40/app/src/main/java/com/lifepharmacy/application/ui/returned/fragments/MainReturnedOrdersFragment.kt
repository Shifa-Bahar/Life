package com.lifepharmacy.application.ui.returned.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentReturnedMainOrdersBinding
import com.lifepharmacy.application.ui.orders.OrderTabsPagerAdapter
import com.lifepharmacy.application.ui.orders.viewmodels.OrdersViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class MainReturnedOrdersFragment : BaseFragment<FragmentReturnedMainOrdersBinding>(), ClickTool,
  ClickReturnedMainOrdersFragment {


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    if (view == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      initViewPager()
      observers()
    }

    return mView
  }

  private fun initUI() {
    binding.toolbarTitle.tvToolbarTitle.text = getString(R.string.returns)
    binding.toolbarTitle.click = this
    binding.click = this
  }

  private fun initViewPager() {
    val fragmentList: ArrayList<Fragment> =
      arrayListOf(RequestedFragment.newInstance(), ReturnedApprovedFragment.newInstance())
    binding.pagerOrders.adapter = OrderTabsPagerAdapter(this, fragmentList)
    TabLayoutMediator(
      binding.tabsOrders,
      binding.pagerOrders,
      true,
      false
    ) { tab: TabLayout.Tab, position: Int ->
      when (position) {
        0 -> tab.setText(R.string.requested)
        1 -> tab.setText(R.string.approved)
      }
    }.attach()
  }

  private fun observers() {

  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment__returned_main_orders
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }


}
