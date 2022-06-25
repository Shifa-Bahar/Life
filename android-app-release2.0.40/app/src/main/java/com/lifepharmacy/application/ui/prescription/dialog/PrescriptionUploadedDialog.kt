package com.lifepharmacy.application.ui.prescription.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseDialogFragment
import com.lifepharmacy.application.databinding.DailogPrescriptionUploadBinding
import com.lifepharmacy.application.ui.orders.fragments.MainOrdersFragment
import com.lifepharmacy.application.ui.prescription.viewmodel.PrescriptionViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class PrescriptionUploadedDialog : BaseDialogFragment<DailogPrescriptionUploadBinding>(),
  ClickPrescriptionUploadDialog {
  private val viewModel: PrescriptionViewModel by activityViewModels()
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
    return R.layout.dailog_prescription_upload
  }

  override fun onClickContinue() {
    viewModel.resetPrescription()
    findNavController().navigateUp()
  }

  override fun onClickGotoOrders() {
    findNavController().navigate(R.id.nav_orders, MainOrdersFragment.getBundle(1))
  }

}
