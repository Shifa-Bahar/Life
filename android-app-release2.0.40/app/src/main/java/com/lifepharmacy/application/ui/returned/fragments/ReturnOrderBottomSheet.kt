package com.lifepharmacy.application.ui.returned.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseBottomUpSheet
import com.lifepharmacy.application.databinding.BottomSheetReturnOrderBinding
import com.lifepharmacy.application.model.orders.ReturnOrderModel
import com.lifepharmacy.application.ui.returned.adapters.ReturnProductsAdapter
import com.lifepharmacy.application.ui.returned.viewmdols.ReturnOrdersViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Zahid Ali
 */
@AndroidEntryPoint
class ReturnOrderBottomSheet : BaseBottomUpSheet<BottomSheetReturnOrderBinding>() {
  private val viewModel: ReturnOrdersViewModel by viewModels()
  private lateinit var returnProductAdapter: ReturnProductsAdapter

  companion object {
    fun getReturnOrderBottomSheetBundle(
      returnOrder: ReturnOrderModel,
    ): Bundle {
      val bundle = Bundle()
      bundle.putParcelable("returnOrder", returnOrder)
      return bundle
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      viewModel.selectReturnedOrderMut.value = it.getParcelable("returnOrder")
    }
  }




  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    isCancelable = true
    initLayout()
    observers()
  }

  private fun initLayout() {
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    returnProductAdapter = ReturnProductsAdapter(requireActivity())
    binding.rvProducts.adapter = returnProductAdapter
  }

  private fun observers() {
    viewModel.selectReturnedOrderMut.observe(viewLifecycleOwner, Observer {
      it?.let {
        returnProductAdapter.setDataChanged(it.items)
      }
    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.bottom_sheet_return_order
  }

  override fun permissionGranted(requestCode: Int) {

  }
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog: Dialog = super.onCreateDialog(savedInstanceState)
    dialog.setOnShowListener { dialogInterface ->
      val bottomSheetDialog = dialogInterface as BottomSheetDialog
      setupFullHeight(bottomSheetDialog)
    }
    return dialog
  }

  private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
    val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
    val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!)
    val layoutParams = bottomSheet.layoutParams
    val windowHeight = getWindowHeight()
    if (layoutParams != null) {
      layoutParams.height = (windowHeight * 0.9).toInt()
    }
    bottomSheet.layoutParams = layoutParams
    behavior.state = BottomSheetBehavior.STATE_EXPANDED
  }

  private fun getWindowHeight(): Int {
    // Calculate window height for fullscreen use
    val displayMetrics = DisplayMetrics()
    requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
  }
}