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
import com.lifepharmacy.application.model.orders.PrescriptionOrder
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.orders.adapters.ClickPrescriptionOrderItem
import com.lifepharmacy.application.ui.orders.adapters.PrescriptionOrderAdapter
import com.lifepharmacy.application.ui.orders.viewmodels.OrdersViewModel
import com.lifepharmacy.application.utils.universal.RecyclerPagingListener
import com.lifepharmacy.application.utils.universal.RecyclerViewPagingUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class PrescriptionOrderFragment : BaseFragment<FragmentOrdersBinding>(),
  ClickPrescriptionOrderItem, RecyclerPagingListener {
  private val viewModel: OrdersViewModel by activityViewModels()
  lateinit var ordersAdapter: PrescriptionOrderAdapter
  private var layoutManager: GridLayoutManager? = null

  private lateinit var recyclerViewPagingUtil: RecyclerViewPagingUtil

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    if (view == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
    }
    resetSkip()
    observers()
    return mView
  }

  private fun initUI() {
    resultRecyclerView()
    binding.showTollBar = false
    binding.empty = getString(R.string.no_prescription_request_found)
  }
  private fun resultRecyclerView() {
    ordersAdapter = PrescriptionOrderAdapter(requireActivity(), this)
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
    viewModel.getPrescriptionOrders().observe(viewLifecycleOwner) {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            it.data?.data?.let { data ->
              recyclerViewPagingUtil.nextPageLoaded(data.size)
            }
            ordersAdapter.setDataChanged(it.data?.data)
            hideLoading()
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
    }
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_orders
  }

  override fun permissionGranted(requestCode: Int) {

  }

  companion object {
    @JvmStatic
    fun newInstance(isEditable: Boolean = false) =
      PrescriptionOrderFragment().apply {
        arguments = Bundle().apply {
          putBoolean("is_editable", isEditable)
        }
      }
  }


  override fun onClickPrescriptionItem(orderPrescription: PrescriptionOrder, position: Int) {
    viewModel.selectedOrder.value = orderPrescription
    findNavController().navigate(R.id.prescriptionOrderDetailFragment)
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
}