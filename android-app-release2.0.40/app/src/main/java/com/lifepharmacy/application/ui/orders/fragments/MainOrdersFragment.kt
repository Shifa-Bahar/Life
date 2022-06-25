package com.lifepharmacy.application.ui.orders.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentMainOrdersBinding
import com.lifepharmacy.application.model.payment.TransactionMainModel
import com.lifepharmacy.application.ui.orders.OrderTabsPagerAdapter
import com.lifepharmacy.application.ui.orders.viewmodels.OrdersViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class MainOrdersFragment : BaseFragment<FragmentMainOrdersBinding>(), ClickTool,
  ClickMainOrdersFragment {
  private val viewModel: OrdersViewModel by viewModels()

  var selectedTab: Int? = 0
  lateinit var onBackPressedCallback: OnBackPressedCallback

  companion object {
    fun getBundle(position: Int = 0): Bundle {
      val bundle = Bundle()
      position.let {
        bundle.putInt("position", position)
      }
      return bundle
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Override this method to customize back press

    onBackPressedCallback = object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        //some logic that needs to be run before fragment is destroyed
        try {
//          findNavController().navigateUp()
          findNavController().navigate(R.id.nav_home)
        } catch (e: Exception) {
          e.printStackTrace()
        }

      }
    }
//    requireActivity().onBackPressedDispatcher.addCallback(this) {
//      findNavController().navigate(R.id.nav_home)
////      navigate(R.id.nav_profile)
//    }
    requireActivity().onBackPressedDispatcher.addCallback(
      onBackPressedCallback
    )
    arguments?.let {
      selectedTab = it.getInt("position", 0)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    //unregister listener here
    try {
      onBackPressedCallback.isEnabled = false
      onBackPressedCallback.remove()
    } catch (e: java.lang.Exception) {
      e.printStackTrace()
    }
  }

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
    reselectionObserver()
    reselectionProfileObserver()
    return mView
  }

  private fun initUI() {
    binding.toolbarTitle.tvToolbarTitle.text = getString(R.string.orders)
    binding.toolbarTitle.click = this
    binding.click = this
  }

  fun reselectionObserver() {
    viewModel.appManager.loadingState.homeReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
          findNavController().navigate(R.id.nav_home)
          viewModel.appManager.loadingState.homeReselected.value = false
        }
      }
    }
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
  private fun initViewPager() {
    val fragmentList: ArrayList<Fragment> =
      arrayListOf(
        NonPrescriptionOrderFragment.newInstance(),
        PrescriptionOrderFragment.newInstance()
      )
    binding.pagerOrders.adapter = OrderTabsPagerAdapter(this, fragmentList)
    TabLayoutMediator(
      binding.tabsOrders,
      binding.pagerOrders,
      true,
      false
    ) { tab: TabLayout.Tab, position: Int ->
      when (position) {
        0 -> tab.setText(R.string.non_prescription)
        1 -> tab.setText(R.string.prescriptions)
      }
    }.attach()
    selectedTab?.let { selectPage(it) }
  }

  private fun selectPage(pageIndex: Int) {
    binding.tabsOrders.setScrollPosition(pageIndex, 0f, true)
    binding.pagerOrders.currentItem = pageIndex
  }

  private fun observers() {

    //Observing App token
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_main_orders
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickBack() {
    findNavController().navigate(R.id.nav_home)
//    findNavController().navigateUp()
  }


}
