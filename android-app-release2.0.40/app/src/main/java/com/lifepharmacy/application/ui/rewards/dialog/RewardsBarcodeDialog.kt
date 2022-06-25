package com.lifepharmacy.application.ui.rewards.dialog

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseDialogFragment
import com.lifepharmacy.application.databinding.DailogRewardBarcodeBinding
import com.lifepharmacy.application.ui.orders.fragments.MainOrdersFragment
import com.lifepharmacy.application.ui.prescription.viewmodel.PrescriptionViewModel
import com.lifepharmacy.application.ui.rewards.viewmodels.RewardsViewModel
import com.lifepharmacy.application.ui.utils.dailogs.ClickAnimationComboActionDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dailog_reward_barcode.view.*
import kotlinx.android.synthetic.main.layout_voucher_front.view.*


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class RewardsBarcodeDialog : BaseDialogFragment<DailogRewardBarcodeBinding>(),
  ClickRewardsDialog, ClickAnimationComboActionDialog {
  var reward_code: String = ""

  companion object {
    fun getDialogBarcodeBundle(
      reward_code: String?
    ): Bundle {
      val bundle = Bundle()
      reward_code?.let {
        bundle.putString("reward_code", reward_code)
      }
      return bundle
    }
  }

  private val viewModel: RewardsViewModel by activityViewModels()
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    isCancelable = true

    arguments?.let {
      if (it.containsKey("reward_code")) {
        reward_code = it.getString("reward_code").toString()
      } else
        Log.e("reward_code --- ", "onViewCreated: false")
    }
    initUI()
    observers()
    Log.e("onViewCreated ---", "")
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogTheme)

    Log.e("onCreate ---", "")

  }

  private fun initUI() {
    binding.click = this
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    binding.tvCode.text = reward_code
    binding.generationRewardBarcodeImage.setBarcodeFormat(BarcodeFormat.CODE_128)
    binding.generationRewardBarcodeImage.setBarcodeText(reward_code)
//    binding.generation_reward_barcode_image.setBarcodeText(viewModel.selectedVoucher.value?.reward_code)

//    binding.toolbarTitle.tvToolbarTitle.text = "Rewards Details"
//    binding.animationView.setAnimation("tick.json")
//    binding.animationView.playAnimation()
  }


  private fun observers() {
  }


  override fun getLayoutRes(): Int {
    return R.layout.dailog_reward_barcode
  }

  override fun onClickContinue() {
    findNavController().navigateUp()
  }

  override fun onClickGotoOrders() {
//    TODO("Not yet implemented")
  }
//  override fun onClickView() {
//    findNavController().navigateUp()
//  }
//  override fun onClickGotoOrders() {
//    findNavController().navigate(R.id.nav_orders, MainOrdersFragment.getBundle(1))
//  }

  override fun onClickLater() {
    findNavController().navigateUp()
  }

  override fun onClickClaim() {
    findNavController().navigateUp()
//    requireActivity().findNavController(R.id.fragment).navigate(R.id.nav_offers)
  }

}
