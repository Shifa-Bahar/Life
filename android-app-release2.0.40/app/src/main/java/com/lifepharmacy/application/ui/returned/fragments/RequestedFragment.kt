package com.lifepharmacy.application.ui.returned.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentOrdersBinding
import com.lifepharmacy.application.enums.ScrollingState
import com.lifepharmacy.application.model.orders.OrderResponseModel
import com.lifepharmacy.application.model.orders.ReturnOrderModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.orders.adapters.ClickOrder
import com.lifepharmacy.application.ui.orders.adapters.OrdersAdapter
import com.lifepharmacy.application.ui.returned.adapters.ClickReturnOrderItem
import com.lifepharmacy.application.ui.returned.adapters.ReturnedShipmentAdapter
import com.lifepharmacy.application.ui.returned.viewmdols.ReturnOrdersViewModel
import com.lifepharmacy.application.ui.utils.ScrollStateListener
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.universal.Logger
import com.lifepharmacy.application.utils.universal.RecyclerPagingListener
import com.lifepharmacy.application.utils.universal.RecyclerViewPagingUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class RequestedFragment : BaseFragment<FragmentOrdersBinding>(), ClickReturnOrderItem, ClickOrder,
  RecyclerPagingListener, ClickTool {
  private val viewModel: ReturnOrdersViewModel by viewModels()
  lateinit var ordersAdapter: ReturnedShipmentAdapter
  private var layoutManager: GridLayoutManager? = null
  private lateinit var recyclerViewPagingUtil: RecyclerViewPagingUtil
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    if (view == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      resetSkip()
    }
    observers()
    reselectionProfileObserver()
    return mView
  }

  private fun initUI() {
    binding.toolbarTitle.tvToolbarTitle.text = getString(R.string.returns)
    binding.toolbarTitle.click = this
    binding.showTollBar = true
    binding.empty = getString(R.string.no_returns_found)
    initPagination()
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

  private fun initPagination(){
    ordersAdapter = ReturnedShipmentAdapter(requireActivity(), this)
    layoutManager = GridLayoutManager(requireContext(), 1)
    binding.rvOrders.layoutManager = layoutManager
    recyclerViewPagingUtil = RecyclerViewPagingUtil(
      binding.rvOrders,
      layoutManager!!, this
    )
    binding.rvOrders.adapter = ordersAdapter
    binding.rvOrders.addOnScrollListener(recyclerViewPagingUtil)
    binding.rvOrders.post { // Call smooth scroll
      binding.rvOrders.scrollToPosition(0)
    }

  }
  private fun observers() {
    viewModel.getReturnedRequestedOrderList().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            it.data?.data?.let { data ->
              recyclerViewPagingUtil.nextPageLoaded(data.size)
            }
            ordersAdapter.setDataChanged(it.data?.data)
            binding.showEmpty = ordersAdapter.arrayList.isNullOrEmpty()
          }
          Result.Status.ERROR -> {
            hideLoading()
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
          }
          Result.Status.LOADING -> {
            showLoading()
            recyclerViewPagingUtil.isLoading = true
          }
        }
      }
    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_orders
  }

  override fun permissionGranted(requestCode: Int) {

  }

  companion object {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    @JvmStatic
    fun newInstance(isEditable: Boolean = false) =
      RequestedFragment().apply {
        arguments = Bundle().apply {
          putBoolean("is_editable", isEditable)
        }
      }
  }

  override fun onClickOrder(orderModel: OrderResponseModel) {

  }

  override fun onClickViewOrder(orderModel: OrderResponseModel) {

  }

  override fun onClickReturnOrder(returnOrderItem: ReturnOrderModel) {
    findNavController().navigate(R.id.returnOrderBottomSheet,ReturnOrderBottomSheet.getReturnOrderBottomSheetBundle(returnOrderItem))
  }
  private fun resetSkip(){
    recyclerViewPagingUtil.skip = 0
    viewModel.skip = 0
  }

  override fun onNextPage(skip: Int, take: Int) {
    viewModel.skip = skip
    viewModel.take = take
    observers()
  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }

}
