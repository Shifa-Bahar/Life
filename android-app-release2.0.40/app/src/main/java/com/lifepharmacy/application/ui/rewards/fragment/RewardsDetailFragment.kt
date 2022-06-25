package com.lifepharmacy.application.ui.rewards.fragment

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.BarcodeFormat
import com.lifepharmacy.application.Constants
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentRewardsDetailsBinding
import com.lifepharmacy.application.databinding.FragmentVouchersDetailsBinding
import com.lifepharmacy.application.enums.VoucherStatus
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.RewardsListScreenOpen
import com.lifepharmacy.application.managers.analytics.voucherDetailScreenOpen
import com.lifepharmacy.application.model.vouchers.VoucherModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.rewards.dialog.RewardsBarcodeDialog
import com.lifepharmacy.application.ui.rewards.viewmodels.RewardsViewModel
import com.lifepharmacy.application.ui.splash.SplashActivity
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.vouchers.viewmodels.VouchersViewModel
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.DateTimeUtil
import com.lifepharmacy.application.utils.StatusUtil
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeDouble
import com.pnuema.java.barcode.Barcode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dailog_reward_barcode.view.*
import kotlinx.android.synthetic.main.fragment_splash_first_page.*
import kotlinx.android.synthetic.main.layout_rewards_front.view.*
import kotlinx.android.synthetic.main.layout_voucher_back.view.*
import kotlinx.android.synthetic.main.layout_voucher_front.view.*


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class RewardsDetailFragment : BaseFragment<FragmentRewardsDetailsBinding>(),
  ClickRewardsDetailFragment, ClickTool {

  companion object {
    fun getRewardListingBundle(
      id: Int?
    ): Bundle {
      val bundle = Bundle()
      id?.let {
        bundle.putInt("id", id)
      }
      return bundle
    }
  }

  private val viewModel: RewardsViewModel by activityViewModels()
  private var mSetRightOut: AnimatorSet? = null
  private var mSetLeftIn: AnimatorSet? = null
  private var mIsBackVisible = false
  private var voucherString = ""
  private var imageUrl: String = ""
  private var reward_code: String = ""

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    arguments?.let {
      viewModel.id = it.getInt("id")
    }
    viewModel.appManager.analyticsManagers.RewardsListScreenOpen()
    if (view == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      observers()
//      initUI()

    }

    return mView
  }


  @SuppressLint("SetTextI18n")
  private fun initUI() {
    //   viewModel.selectedVoucher.value?.let { makeStatusVoucher(it) }
    binding.toolbarTitle.click = this
    binding.click = this
    binding.lifecycleOwner = this
    binding.viewModel = viewModel
    binding.toolbarTitle.tvToolbarTitle.text = "Reward"
    binding.cardFrontReward.textView777.text = viewModel.selectedVoucher.value?.amount
    var validity: String? =
      DateTimeUtil.getStringDateFromStringWithoutTimeZoneAndMilSec(viewModel.endDate.value.toString())

    binding.cardFrontReward.tv_statusdate.text = "valid till $validity"



    binding.cardFrontReward.tv_termscondition.text =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(viewModel.termsAndConditions.value.toString(), Html.FROM_HTML_MODE_COMPACT)
      } else {
        Html.fromHtml(viewModel.termsAndConditions.value.toString())
      }

    binding.btnTerms.text = "REDEEM NOW"

    try {
      imageUrl = viewModel.imageBanner.value?.banner.toString()
      activity?.let { Glide.with(it).load(imageUrl).into(binding.cardFrontReward.imageBanner) }
    } catch (e: Exception) {
    }

  }

  private fun observers() {
    viewModel.getRewardsDetails().observe(viewLifecycleOwner, Observer {
      it.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            it.let {
              viewModel.termsAndConditions.value = it.data?.data?.reward_campaign?.terms
              viewModel.imageBanner.value = it.data?.data?.reward_campaign
              viewModel.reward_code.value = it.data?.data?.reward_code
              reward_code = it.data?.data?.reward_code.toString()
              Log.e("shifa -- ", "rewd: $reward_code")
              viewModel.endDate.value = it.data?.data?.reward_campaign?.end_at

              viewModel.selectedVoucher.value = it.data?.data
              initUI()

              // RewardsAdapter.setDataChanged(it.data?.data)
            }
          }
          Result.Status.ERROR -> {
            it.message?.let { it1 ->
              AlertManager.showErrorMessage(
                requireActivity(),
                it1
              )
            }
//              hideLoading()
//            viewModel.loading.value = false
//            viewModel.text.value = "Error"
          }

        }
      }
    })


  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_rewards_details
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }


  override fun onClickFlip() {

    findNavController().navigate(
      R.id.dialogRewardBarcode, RewardsBarcodeDialog.getDialogBarcodeBundle(reward_code)
    )
//    findNavController().navigate(R.id.dialogRewardBarcode)
  }
//    val image = ImageView(activity)
//    image.setImageResource.setBarcodeText(viewModel.selectedVoucher.value?.reward_code)
//    Toast.makeText(context, "REWARDS DETAIL", Toast.LENGTH_LONG).show()
//    MaterialAlertDialogBuilder(
//      requireActivity(),
//      R.style.ThemeOverlay_App_MaterialInfoDialog
//    )
//      .setTitle("1234567789")
//      .setMessage(getString(R.string.are_you_sure))
//      .show()

//    mIsBackVisible = if (!mIsBackVisible) {
//      mSetRightOut?.setTarget(binding.cardFront)
//      mSetLeftIn?.setTarget(binding.cardBack)
//      mSetRightOut?.start()
//      mSetLeftIn?.start()
//      binding.btnTerms.text = getString(R.string.back)
//      true
//    } else {
//      mSetRightOut?.setTarget(binding.cardBack)
//      mSetLeftIn?.setTarget(binding.cardFront)
//      mSetRightOut?.start()
//      mSetLeftIn?.start()
//      binding.btnTerms.text = getString(R.string.terms_and_condition)
//
//      false
//    }

}
