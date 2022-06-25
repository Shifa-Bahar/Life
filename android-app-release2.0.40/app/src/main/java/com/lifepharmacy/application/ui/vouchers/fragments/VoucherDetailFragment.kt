package com.lifepharmacy.application.ui.vouchers.fragments

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentVouchersDetailsBinding
import com.lifepharmacy.application.enums.VoucherStatus
import com.lifepharmacy.application.managers.analytics.voucherDetailScreenOpen
import com.lifepharmacy.application.model.vouchers.VoucherModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.vouchers.viewmodels.VouchersViewModel
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.DateTimeUtil
import com.lifepharmacy.application.utils.StatusUtil
import com.lifepharmacy.application.utils.universal.Extensions.currencyFormat
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeDouble
import com.pnuema.java.barcode.Barcode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_voucher_back.view.*
import kotlinx.android.synthetic.main.layout_voucher_front.view.*


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class VoucherDetailFragment : BaseFragment<FragmentVouchersDetailsBinding>(),
  ClickVoucherDetailFragment, ClickTool {
  private val viewModel: VouchersViewModel by activityViewModels()
  private var mSetRightOut: AnimatorSet? = null
  private var mSetLeftIn: AnimatorSet? = null
  private var mIsBackVisible = false
  private var voucherString = ""

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.voucherDetailScreenOpen()
    if (view == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      loadAnimations()
      changeCameraDistance()
      observers()
    }

    initUI()
    return mView
  }

  private fun changeCameraDistance() {
    val distance = 8000
    val scale = resources.displayMetrics.density * distance
    binding.cardFront.cameraDistance = scale
    binding.cardBack.cameraDistance = scale
  }

  private fun loadAnimations() {
    mSetRightOut =
      AnimatorInflater.loadAnimator(requireContext(), R.animator.out_animation) as AnimatorSet
    mSetLeftIn =
      AnimatorInflater.loadAnimator(requireContext(), R.animator.in_animation) as AnimatorSet
  }

  @SuppressLint("SetTextI18n")
  private fun initUI() {
    viewModel.selectedVoucher.value?.let { makeStatusVoucher(it) }
    binding.toolbarTitle.click = this
    binding.click = this
    binding.toolbarTitle.tvToolbarTitle.text = "Voucher Details"
    binding.cardFront.tv_amount.text =
      viewModel.selectedVoucher.value?.iSSUEVALUE?.stringToNullSafeDouble()?.currencyFormat()
    binding.cardFront.tv_status.text = "${getString(R.string.status_colon)} $voucherString"
    binding.cardFront.tv_number.text = viewModel.selectedVoucher.value?.cARDNUMBER
//    setVoucherValidity(
//      viewModel.selectedVoucher.value?.vALIDFROM,
//      viewModel.selectedVoucher.value?.eXPIRYDATE,
//      viewModel.selectedVoucher.value?: VoucherModel()
//    )

    setVoucherStartDate(
      binding.cardFront.tv_valid_start_date,
      viewModel.selectedVoucher.value?.vALIDFROM
    )
    setVoucherStartDate(
      binding.cardBack.tv_valid_start_date_back,
      viewModel.selectedVoucher.value?.vALIDFROM
    )
    setVoucherEndDate(
      binding.cardFront.tv_valid_end_date,
      viewModel.selectedVoucher.value?.eXPIRYDATE,
      viewModel.selectedVoucher.value ?: VoucherModel()
    )
    setVoucherEndDate(
      binding.cardBack.tv_valid_end_date_back,
      viewModel.selectedVoucher.value?.eXPIRYDATE,
      viewModel.selectedVoucher.value ?: VoucherModel()
    )

    binding.cardBack.tv_conditions.text = viewModel.termsAndConditions.value
    if (voucherString == "Valid") {
      binding.cardFront.generation_barcode_image.visibility = View.VISIBLE
      binding.cardFront.generation_barcode_image.setBarcodeFormat(BarcodeFormat.CODE_128)
      binding.cardFront.generation_barcode_image.setBarcodeText(viewModel.selectedVoucher.value?.cARDNUMBER)
      binding.cardFront.tv_number.visibility = View.VISIBLE

    } else {
      binding.cardFront.generation_barcode_image.visibility = View.GONE
      binding.cardFront.tv_number.visibility = View.GONE
    }
  }

  @SuppressLint("SetTextI18n")
  fun setVoucherStartDate(
    view: TextView,
    bindStartingDate: String?,
  ) {
    try {
      view.text =
        "${view.context.getString(R.string.valid_from)} ${
          DateTimeUtil.getCalenderStringFromStringDateOnlyWithoutTimeZone(
            bindStartingDate ?: ""
          )
        }"

    } catch (e: Exception) {
    }
  }

  @SuppressLint("SetTextI18n")
  fun setVoucherEndDate(
    view: TextView,
    bindEndDate: String?,
    voucher: VoucherModel?
  ) {
    try {
      when (voucher?.let { StatusUtil.voucherStatusEnum(it) }) {
        VoucherStatus.VALID -> {
          view.text =
            "${view.context.getString(R.string.expiring_on)}  ${
              DateTimeUtil.getFormatDateOnlyStringWithOutTimeZone(
                bindEndDate ?: ""
              )
            }"
        }
        VoucherStatus.EXPIRED -> {
          view.text =
            "${view.context.getString(R.string.expired_on)}  ${
              DateTimeUtil.getFormatDateOnlyStringWithOutTimeZone(
                bindEndDate ?: ""
              )
            }"
        }
        else -> {
          "  ${DateTimeUtil.getFormatDateOnlyStringWithOutTimeZone(bindEndDate ?: "")}"
        }
      }


    } catch (e: Exception) {
    }
  }

//
//  fun setVoucherValidity(
//    bindStartingDate: String?,
//    bindEndDate: String?,
//    voucher: VoucherModel,
//  ) {
//    try {
//      val validity =
//        when(StatusUtil.voucherStatusEnum(voucher)){
//          VoucherStatus.EXPIRED ->{
//            "${getString(R.string.valid_from)} ${
//              DateTimeUtil.getCalenderStringFromStringDateOnlyWithoutTimeZone(
//                bindStartingDate ?: ""
//              )
//            } ${getString(R.string.expired_on)}  ${
//              DateTimeUtil.getFormatDateOnlyStringWithOutTimeZone(
//                bindEndDate ?: ""
//              )
//            }"
//          }
//          VoucherStatus.VALID ->{
//            "${getString(R.string.valid_from)} ${
//              DateTimeUtil.getCalenderStringFromStringDateOnlyWithoutTimeZone(
//                bindStartingDate ?: ""
//              )
//            } ${getString(R.string.expiring_on)}  ${
//              DateTimeUtil.getFormatDateOnlyStringWithOutTimeZone(
//                bindEndDate ?: ""
//              )
//            }"
//          }
//          else -> {
//            "${getString(R.string.valid_from)} ${
//              DateTimeUtil.getCalenderStringFromStringDateOnlyWithoutTimeZone(
//                bindStartingDate ?: ""
//              )
//            } ${getString(R.string.until)}  ${
//              DateTimeUtil.getFormatDateOnlyStringWithOutTimeZone(
//                bindEndDate ?: ""
//              )
//            }"
//          }
//        }
//      binding.cardFront.tv_valid.text = validity
//
//      binding.cardBack.tv_validity.text = validity
//    } catch (e: Exception) {
//    }
//  }

  private fun makeStatusVoucher(voucher: VoucherModel) {
    when {
      voucher.mANUALEXPIRY == "1" -> {
        voucherString = getString(R.string.expired)
      }
      voucher.sTATUS == "2" -> {
        voucherString = getString(R.string.redeemed)
      }
      else -> {
        val fromDate =
          DateTimeUtil.getCalenderObjectFromStringDateOnlyWithoutTimeZone(
            voucher.vALIDFROM ?: ""
          )?.time
        val toDate =
          DateTimeUtil.getCalenderObjectFromStringWithoutTimeZone(voucher.eXPIRYDATE ?: "")?.time
        val currentDate = DateTimeUtil.getCurrentDateAndTime().time
        fromDate?.let {
          toDate?.let {
            when {
              fromDate > currentDate -> {
                voucherString = getString(R.string.not_yet_available)
              }
              toDate < currentDate -> {
                voucherString = getString(R.string.expired)
              }
              fromDate.compareTo(currentDate) * currentDate.compareTo(toDate) > 0
              -> {
                voucherString = getString(R.string.valid)
              }
            }
          }
        }
      }
    }
  }


  private fun observers() {
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_vouchers_details
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }


  override fun onClickFlip() {
    mIsBackVisible = if (!mIsBackVisible) {
      mSetRightOut?.setTarget(binding.cardFront)
      mSetLeftIn?.setTarget(binding.cardBack)
      mSetRightOut?.start()
      mSetLeftIn?.start()
      binding.btnTerms.text = getString(R.string.back)
      true
    } else {
      mSetRightOut?.setTarget(binding.cardBack)
      mSetLeftIn?.setTarget(binding.cardFront)
      mSetRightOut?.start()
      mSetLeftIn?.start()
      binding.btnTerms.text = getString(R.string.terms_and_condition)

      false
    }
  }
}
