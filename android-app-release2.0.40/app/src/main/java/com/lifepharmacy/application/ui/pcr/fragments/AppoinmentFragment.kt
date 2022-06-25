package com.lifepharmacy.application.ui.pcr.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentAppoinmentBinding
import com.lifepharmacy.application.databinding.FragmentWishlistBinding
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.wishListScreenOpen
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeProduct
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.orders.adapters.OrdersAdapter
import com.lifepharmacy.application.ui.orders.fragments.PrescriptionOrderFragment
import com.lifepharmacy.application.ui.pcr.adapters.AppoinmentAdapter
import com.lifepharmacy.application.ui.pcr.adapters.ClickAppoinmentDetails
import com.lifepharmacy.application.ui.pcr.viewmodel.PcrListingViewModel
import com.lifepharmacy.application.ui.productList.adapter.ProductAdapter
import com.lifepharmacy.application.ui.products.fragment.ProductFragment
import com.lifepharmacy.application.ui.products.viewmodel.ProductViewModel
import com.lifepharmacy.application.ui.rewards.adapters.PcrAdapter
import com.lifepharmacy.application.ui.rewards.fragment.RewardsDetailFragment
import com.lifepharmacy.application.ui.whishlist.viewmodel.WishListViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.vouchers.adapters.VouchersAdapter
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.universal.RecyclerPagingListener
import com.lifepharmacy.application.utils.universal.RecyclerViewPagingUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class AppoinmentFragment : BaseFragment<FragmentAppoinmentBinding>(), ClickTool,
  ClickAppoinmentDetails,
  RecyclerPagingListener {
  private val pcrviewModel: PcrListingViewModel by activityViewModels()
  lateinit var appoinmentAdapter: AppoinmentAdapter
  private var layoutManager: GridLayoutManager? = null

  private lateinit var recyclerViewPagingUtil: RecyclerViewPagingUtil

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      findNavController().navigate(R.id.nav_home)
//      navigate(R.id.nav_profile)
    }

  }


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
//    viewModel.appManager.analyticsManagers.wishListScreenOpen()
    if (view == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
    }
    resetSkip()
    observers()

    reselectionProfileObserver()
    return mView
  }

  private fun initUI() {
    binding.lifecycleOwner = this
    binding.toolbarTitle.click = this
    binding.toolbarTitle.tvToolbarTitle.text = getString(R.string.appoinment)
    initProductsListRV()
  }

  fun reselectionProfileObserver() {
    pcrviewModel.appManager.loadingState.profileReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
          findNavController().navigate(R.id.nav_profile)
          pcrviewModel.appManager.loadingState.profileReselected.value = false
        }
      }
    }
  }

  private fun initProductsListRV() {
    appoinmentAdapter = AppoinmentAdapter(requireActivity(), this, pcrviewModel.appManager)
    layoutManager = GridLayoutManager(requireContext(), 1)
    binding.rvItems.layoutManager = layoutManager
    recyclerViewPagingUtil = RecyclerViewPagingUtil(
      binding.rvItems,
      layoutManager!!, this
    )
    binding.rvItems.adapter = appoinmentAdapter
    binding.rvItems.addOnScrollListener(recyclerViewPagingUtil)
    binding.rvItems.post { // Call smooth scroll
      binding.rvItems.scrollToPosition(0)
    }
//    resetSkip()
  }

  //  private fun initUI() {
//    binding.toolbarTitle.click = this
//    binding.toolbarTitle.tvToolbarTitle.text = getString(R.string.appoinment)
//
//    productListAdapter = ProductAdapter(requireActivity(), this, this, viewModel.appManager,viewModel.appManager.storageManagers.config.backOrder ?: "Pre-Order")
//    binding.rvItems.adapter = productListAdapter
//  }
  private fun observers() {
    pcrviewModel.getAppoinmentList().observe(viewLifecycleOwner) {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            if (pcrviewModel.skip == 0) {
              binding.showEmpty = it.data?.data.isNullOrEmpty()
            }

            it.data?.data?.let { data ->
              recyclerViewPagingUtil.nextPageLoaded(data.size)
            }
            Log.e(TAG, "observers: sucess")
//            if (pcrviewModel.skip == 0) {
//              appoinmentAdapter.refreshData(it.data?.data)
//            } else {
//              appoinmentAdapter.setDataChanged(it.data?.data)
//            }
            appoinmentAdapter.setDataChanged(it.data?.data)
//            binding.showEmpty = appoinmentAdapter.arrayList.isNullOrEmpty()
//            binding.showEmpty = it.data?.data.isNullOrEmpty()
//            binding.showEmpty = false
            hideLoading()
          }
          Result.Status.ERROR -> {
//            binding.showEmpty = true
            Log.e(TAG, "observers: Error")
            it.message?.let { it1 ->
              AlertManager.showErrorMessage(
                requireActivity(),
                it1
              )
            }
            hideLoading()
          }
          Result.Status.LOADING -> {
            showLoading()
            recyclerViewPagingUtil.isLoading = true
          }
        }
      }
    }
  }

//  override fun onResume() {
//    super.onResume()
//    Log.e(TAG, "onResume:")
////    appoinmentAdapter.notifyDataSetChanged()
////    observers()
//
//  }
//  private fun observers() {
//    viewModel.appManager.wishListManager.wishListItemsMut.observe(viewLifecycleOwner, Observer {
//      it?.let {
//        binding.showEmpty = it.isNullOrEmpty()
//        productListAdapter.refreshData(it)
//      }
//    })
//  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_appoinment
  }

  override fun permissionGranted(requestCode: Int) {

  }

  companion object {
    @JvmStatic
    fun newInstance(isEditable: Boolean = false) =
      AppoinmentFragment().apply {
        arguments = Bundle().apply {
          putBoolean("is_editable", isEditable)
        }
      }
  }

  private fun resetSkip() {
    recyclerViewPagingUtil.skip = 0
    pcrviewModel.skip = 0
  }

  override fun onClickBack() {
    findNavController().navigate(R.id.nav_home)
  }


  override fun onNextPage(skip: Int, take: Int) {
    Log.e(TAG, "onNextPage: $skip $take")
    pcrviewModel.skip = skip
    pcrviewModel.take = take
    observers()
  }

  override fun onClickAppoinmentDetails(id: Int) {
    findNavController().navigate(
      R.id.appoinmentDetailFragment, AppoinmentDetailFragment.getAppoinmentListingBundle(id)
    )
  }
}
