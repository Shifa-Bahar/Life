package com.lifepharmacy.application.ui.orders.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentReturningProductsBinding
import com.lifepharmacy.application.managers.analytics.returningProductsScreenOpen
import com.lifepharmacy.application.model.orders.OrderItem
import com.lifepharmacy.application.model.orders.SubOrderProductItem
import com.lifepharmacy.application.ui.orders.adapters.*
import com.lifepharmacy.application.ui.orders.viewmodels.OrderDetailViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class ReturningProductsFragment : BaseFragment<FragmentReturningProductsBinding>(),
  ClickReturnProductItem, ClickTool, ClickOrderDetailFragment {
  private val viewModel: OrderDetailViewModel by activityViewModels()
  lateinit var returnProductsAdapter: ReturnProductAdapter
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.returningProductsScreenOpen()
    if (view == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      observers()
    }


    return mView
  }

  private fun initUI() {
    binding.toolbar.click = this
    binding.toolbar.tvToolbarTitle.text = getString(R.string.return_items)
    binding.click = this
    viewModel.isAnyProductToReturn.value = false
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    returnProductsAdapter = ReturnProductAdapter(requireActivity(), this, viewModel)
    binding.rvProducts.adapter = returnProductsAdapter
  }


  private fun observers() {
    viewModel.subOrderItemMut.observe(viewLifecycleOwner, Observer {
      it?.let {
        returnProductsAdapter.setDataChanged(it.items)
      }
    })
    viewModel.returnArrayMut.observe(viewLifecycleOwner, Observer {
      it?.let {
        viewModel.isAnyProductToReturn.value = it.isNotEmpty()
//        returnProductsAdapter.notifyDataSetChanged()
      }
    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_returning_products
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickPlus(orderModel: SubOrderProductItem, qty: Int) {
    viewModel.plusReturnItem(orderModel, qty)
  }

  override fun onClickMinus(orderModel: SubOrderProductItem, qty: Int) {
    viewModel.minusReturnItem(orderModel, qty)
  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }

  override fun onClickReturn() {
    findNavController().navigate(R.id.returnReasonBottomSheet)
  }

}