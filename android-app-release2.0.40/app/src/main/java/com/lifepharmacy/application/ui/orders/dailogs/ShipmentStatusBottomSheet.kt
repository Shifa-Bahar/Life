package com.lifepharmacy.application.ui.orders.dailogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpSheet
import com.lifepharmacy.application.databinding.BottomSheetShipmentStatusBinding
import com.lifepharmacy.application.ui.orders.adapters.ShipmentStatusAdapter
import com.lifepharmacy.application.ui.orders.viewmodels.OrderDetailViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class ShipmentStatusBottomSheet : BaseBottomUpSheet<BottomSheetShipmentStatusBinding>() {


  companion object {
    var isAlreadyOpen: Boolean = false
    fun getRatingBottomSheetBundle(
      orderID: Int,
      subOrderID: Int,
      shipmentId: Int,
      rating: Float
    ): Bundle {
      val bundle = Bundle()
      bundle.putInt("orderID", orderID)
      bundle.putInt("subOrderID", subOrderID)
      bundle.putFloat("rating", rating)
      bundle.putInt("shipmentId", shipmentId)
      return bundle
    }
  }

  private val viewOrderDetailViewModel: OrderDetailViewModel by activityViewModels()
  private lateinit var statusAdapter: ShipmentStatusAdapter
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    isCancelable = true
    initLayout()
    observers()
    isAlreadyOpen = true
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
  }

  private fun initLayout() {

  }

  private fun observers() {
    viewOrderDetailViewModel.selectedShipmentMut.observe(viewLifecycleOwner, Observer {
      it?.let {
        statusAdapter = ShipmentStatusAdapter(
          requireActivity(),
          subOrderItem = it,
          true
        )
        binding.rvStatus.adapter = statusAdapter
        statusAdapter.setDataChanged(it.timeLines)
      }
    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_sheet_shipment_status
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    isAlreadyOpen = false
  }

  override fun onCancel(dialog: DialogInterface) {
    super.onCancel(dialog)
    isAlreadyOpen = false
  }


}