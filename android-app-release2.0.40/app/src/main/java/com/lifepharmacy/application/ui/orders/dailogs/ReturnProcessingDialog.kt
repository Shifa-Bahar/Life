package com.lifepharmacy.application.ui.orders.dailogs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseDialogFragment
import com.lifepharmacy.application.databinding.DailogReturnProcessingBinding
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class ReturnProcessingDialog : BaseDialogFragment<DailogReturnProcessingBinding>(),
  ClickReturnProcessingDialog {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
    observers()
    isCancelable = false


  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_FRAME, R.style.FullScreenTransDialogTheme)
  }

  private fun initUI() {
    binding.click = this
    binding.click = this
    binding.lifecycleOwner = this
    binding.animationView.setAnimation("tick.json")
    binding.animationView.playAnimation()
  }


  private fun observers() {
  }


  override fun getLayoutRes(): Int {
    return R.layout.dailog_return_processing
  }

  override fun onClickView() {
    findNavController().navigate(R.id.nav_returns)
  }

}
