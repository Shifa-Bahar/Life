package com.lifepharmacy.application.ui.utils.dailogs

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseDialogFragment
import com.lifepharmacy.application.databinding.DailogLoadingBinding
import com.lifepharmacy.application.ui.orders.dailogs.ClickReturnProcessingDialog
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class LoadingDialog : BaseDialogFragment<DailogLoadingBinding>(),
  ClickReturnProcessingDialog {

  companion object {
    const val TAG = "ImageSelectBottomSheet"
    fun newInstance(): LoadingDialog {
      return LoadingDialog()
    }
  }




  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
    isCancelable = false
    binding.animationView.setAnimation("loading_main.json")
    binding.animationView.repeatCount = ValueAnimator.INFINITE
    binding.animationView.playAnimation()
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_TITLE, R.style.FullScreenLoading)

  }

  private fun initUI() {
    binding.click = this

  }

  override fun getLayoutRes(): Int {
    return R.layout.dailog_loading
  }

  override fun onClickView() {
    findNavController().navigateUp()
  }

}
