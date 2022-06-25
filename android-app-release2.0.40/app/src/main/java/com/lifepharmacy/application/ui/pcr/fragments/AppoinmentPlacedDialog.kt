package com.lifepharmacy.application.ui.pcr.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseDialogFragment
import com.lifepharmacy.application.databinding.DailogAppoinmentPlacedBinding
import com.lifepharmacy.application.databinding.DailogOrderPlacedBinding
import com.lifepharmacy.application.databinding.DailogPrescriptionUploadBinding
import com.lifepharmacy.application.ui.cart.fragmets.ClickOrderPlacedDialog
import com.lifepharmacy.application.ui.cart.viewmodel.CartViewModel
import com.lifepharmacy.application.ui.orders.fragments.MainOrdersFragment
import com.lifepharmacy.application.ui.prescription.viewmodel.PrescriptionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class AppoinmentPlacedDialog : BaseDialogFragment<DailogAppoinmentPlacedBinding>(),
  ClickOrderPlacedDialog {
  private val viewModel: CartViewModel by activityViewModels()
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
    observers()
    isCancelable = false
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogTheme)
  }

  private fun initUI() {
    binding.click = this
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    binding.animationView.setAnimation("tick.json")
    binding.animationView.playAnimation()
  }


  private fun observers() {
  }


  override fun getLayoutRes(): Int {
    return R.layout.dailog_appoinment_placed
  }

  override fun onClickGotoOrders() {
//    try {
//      findNavController().navigate(
//        R.id.appoinmentFragment,
//      )
//    } catch (e: Exception) {
//      e.printStackTrace()
//    }
    requireActivity().findNavController(R.id.fragment).navigate(R.id.nav_appoinment)
  }

  override fun onClickContinueShopping() {
    requireActivity().findNavController(R.id.fragment).navigate(R.id.nav_home)
  }


}
