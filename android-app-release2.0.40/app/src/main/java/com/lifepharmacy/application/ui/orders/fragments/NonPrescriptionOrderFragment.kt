package com.lifepharmacy.application.ui.orders.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentOrdersBinding
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.nonPrescriptionOrderScreenOpen
import com.lifepharmacy.application.model.orders.OrderResponseModel
import com.lifepharmacy.application.model.orders.SubOrderItem
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.orders.adapters.ClickOrder
import com.lifepharmacy.application.ui.orders.adapters.ClickOrderShipmentItem
import com.lifepharmacy.application.ui.orders.adapters.OrdersAdapter
import com.lifepharmacy.application.ui.orders.viewmodels.OrderDetailViewModel
import com.lifepharmacy.application.ui.orders.viewmodels.OrdersViewModel
import com.lifepharmacy.application.ui.rating.fragments.MainRatingFragment
import com.lifepharmacy.application.utils.universal.RecyclerPagingListener
import com.lifepharmacy.application.utils.universal.RecyclerViewPagingUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class NonPrescriptionOrderFragment : BaseFragment<FragmentOrdersBinding>(), ClickOrder,
  RecyclerPagingListener, ClickOrderShipmentItem {
  private val viewModel: OrdersViewModel by activityViewModels()
  private val viewModelOrderDetail: OrderDetailViewModel by activityViewModels()
  lateinit var ordersAdapter: OrdersAdapter
  private var layoutManager: GridLayoutManager? = null

  private lateinit var recyclerViewPagingUtil: RecyclerViewPagingUtil

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.nonPrescriptionOrderScreenOpen()
    if (view == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      observers()
    }
    resetSkip()
    return mView
  }

  private fun initUI() {
    resultRecyclerView()
    binding.showTollBar = false
    binding.empty = getString(R.string.no_orders_found)
  }

  private fun resultRecyclerView() {
    ordersAdapter = OrdersAdapter(requireActivity(), this, this)
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
    viewModel.getOrders().observe(viewLifecycleOwner, {
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
    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_orders
  }

  override fun permissionGranted(requestCode: Int) {

  }

  companion object {
    @JvmStatic
    fun newInstance(isEditable: Boolean = false) =
      NonPrescriptionOrderFragment().apply {
        arguments = Bundle().apply {
          putBoolean("is_editable", isEditable)
        }
      }
  }

  override fun onClickOrder(orderModel: OrderResponseModel) {

  }

  override fun onClickViewOrder(orderModel: OrderResponseModel) {
//    var bundle = Bundle()
//    bundle.putParcelable("order", orderModel)
    findNavController().navigate(
      R.id.nav_order_details,
      OrderDetailFragment.getOrderDetailBundle(orderModel.id.toString())
    )

  }

  private fun resetSkip() {
    recyclerViewPagingUtil.skip = 0
    viewModel.skip = 0
  }

  override fun onNextPage(skip: Int, take: Int) {
    viewModel.skip = skip
    viewModel.take = take
    observers()
  }

  override fun onClickSubOrderViewDetail(orderModel: SubOrderItem, orderId: String) {
    viewModelOrderDetail.selectedSubOrderItem.value = orderModel
    findNavController().navigate(
      R.id.nav_order_details,
      OrderDetailFragment.getOrderDetailBundle(orderModel.id.toString(), orderId)
    )
  }

  override fun onClickRating(orderModel: SubOrderItem, orderId: String, rating: Float) {
    findNavController().navigate(
      R.id.nav_rating,
      MainRatingFragment.getBundle(orderModel.id.toString(), orderModel.subOrderId.toString(),rating)
    )

  }
}
