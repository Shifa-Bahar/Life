package com.lifepharmacy.application.ui.cart.fragmets

import android.app.Dialog
import android.content.DialogInterface.OnShowListener
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpRatioScreenSheet
import com.lifepharmacy.application.base.BaseBottomUpSheet
import com.lifepharmacy.application.databinding.BottomSheetShipmentProductListBinding
import com.lifepharmacy.application.model.address.AddressTypeModel
import com.lifepharmacy.application.ui.address.adapters.ClickItemAddressType
import com.lifepharmacy.application.ui.cart.adapter.CartProductDetailAdapter
import com.lifepharmacy.application.ui.cart.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class ShipmentProductListBottomSheet : BaseBottomUpSheet<BottomSheetShipmentProductListBinding>(),
  ClickItemAddressType {
  private val viewModel: CartViewModel by activityViewModels()
  private lateinit var cartProductDetailAdapter: CartProductDetailAdapter
  private lateinit var behavior: BottomSheetBehavior<LinearLayout>
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    isCancelable = true
    initLayout()
    observers()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
  }

  private fun initLayout() {
//    behavior = BottomSheetBehavior.from(binding.llMain)
//    behavior.isFitToContents = true
//    behavior.halfExpandedRatio = 0.6f
    cartProductDetailAdapter = CartProductDetailAdapter(requireActivity())
    binding.rvProductList.adapter = cartProductDetailAdapter
  }

  private fun observers() {
    when (viewModel.shipmentSelected) {
      1 -> {
        viewModel.offersManagers.instantProductsMut.observe(viewLifecycleOwner, Observer {
          it?.let {
            cartProductDetailAdapter.setDataChanged(it)
          }
        })
      }
      2 -> {
        viewModel.offersManagers.expressProductsMut.observe(viewLifecycleOwner, Observer {
          it?.let {
            cartProductDetailAdapter.setDataChanged(it)
          }
        })
      }
      3 -> {
        viewModel.offersManagers.freeGiftProductArrayMut.observe(viewLifecycleOwner, Observer {
          it?.let {
            cartProductDetailAdapter.setDataChanged(it)
          }
        })
      }
    }


  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_sheet_shipment_product_list
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickAddress(addressTypeModel: AddressTypeModel) {
    findNavController().navigateUp()
  }


}