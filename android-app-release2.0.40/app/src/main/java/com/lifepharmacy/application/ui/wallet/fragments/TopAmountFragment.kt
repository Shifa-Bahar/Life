package com.lifepharmacy.application.ui.wallet.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.*
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.topUpAmountScreenOpen
import com.lifepharmacy.application.model.general.GeneralResponseModel
import com.lifepharmacy.application.model.payment.CardMainModel
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.model.payment.Urls
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.card.ClickCard
import com.lifepharmacy.application.ui.cart.viewmodel.CartViewModel
import com.lifepharmacy.application.ui.payment.WebViewPaymentActivity
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.wallet.viewmodels.TopViewModel
import com.lifepharmacy.application.ui.wallet.viewmodels.WalletViewModel
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class TopAmountFragment : BaseFragment<FragmentTopAmountBinding>(),
  ClickTopAmountFragment, ClickCard, ClickTool {
  private val viewModel: TopViewModel by activityViewModels()
  private val cartViewModel: CartViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()
  private val walletViewModel: WalletViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    walletViewModel.appManager.analyticsManagers.topUpAmountScreenOpen()
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      observers()
    }

    return mView
  }

  private fun initUI() {
    binding.lyTopBar.click = this
    binding.lyTopBar.tvToolbarTitle.text = getString(R.string.top_up)
    binding.click = this
    binding.lyAmount.click = this
    binding.amount = viewModel.amount
    binding.lyAmount.amount = viewModel.amount
    viewModel.amount.setEditText(binding.lyAmount.edAmount)
  }


  private fun observers() {
  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_top_amount
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onCardSelect(position: Int?, cardMainModel: CardMainModel,pg:Int) {

  }

  override fun onDeleteCard(position: Int?, cardMainModel: CardMainModel) {

  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }

  override fun onClickTopUp() {
    viewModel.selectedPaymentType = cartViewModel.selectedPaymentType
    viewModel.topUp(viewModel.getTransactionModer())
      .observe(viewLifecycleOwner, Observer {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            viewModel.selectedPaymentType = cartViewModel.selectedPaymentType
            it.data?.let { it1 -> handleTransactionResult(it1) }
//            if (viewModel.viaNewCard) {
//              it.data?.data?.urls?.let { it1 ->
//                openActivityForPayment(it1)
//              }
//            } else {
//              if (it.data?.data?.status == 0) {
//                it.message?.let { it1 ->
//                  AlertManager.showErrorMessage(
//                    requireActivity(),
//                    it1
//                  )
//                }
//              } else {
//                topUpIsDon()
//              }
//            }

          }
          Result.Status.ERROR -> {
            hideLoading()
            it.message?.let { it1 ->
              AlertManager.showErrorMessage(
                requireActivity(),
                it1
              )
            }
          }
          Result.Status.LOADING -> {
            showLoading()
          }
        }
      })
  }

  override fun onClickFifty() {
    viewModel.amount.setValue("50")
  }

  override fun onClickHundred() {
    viewModel.amount.setValue("100")
  }

  override fun onClickOneFifty() {
    viewModel.amount.setValue("150")
  }

  override fun onClickLayoutAmount() {
    binding.lyAmount.edAmount.requestFocus()
  }

  private fun topUpIsDon() {
    walletViewModel.requestCards()
    profileViewModel.updateCurrentUser()
    findNavController().navigateUp()
  }

  private fun openActivityForPayment(paymentUrl: String?, successUrl: String?, failUrl: String?) {
    val intent = Intent(requireActivity(), WebViewPaymentActivity::class.java)
    intent.putExtra("paymentURL", paymentUrl)
    intent.putExtra("successURL", successUrl)
    intent.putExtra("failURL", failUrl)
    startActivityForResult(intent, ConstantsUtil.PAYMENT_ACTIVITY_REQUEST_CODE)
//    WebViewPaymentActivity.open(requireActivity(), urls)
  }

  private fun handleTransactionResult(result: GeneralResponseModel<TransactionModel>) {
    if (viewModel.viaNewCard) {
      result.data?.let { it1 ->
        openActivityForPayment(it1.paymentUrl, it1.successUrl, it1.failUrl)
      }
    } else {
      if (result.data?.status == 0) {
        AlertManager.showErrorMessage(
          requireActivity(),
          getString(R.string.transaction_failed)
        )
      } else {
        topUpIsDon()
      }
    }
  }

  @SuppressLint("Recycle")
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK) {
      when (requestCode) {
        ConstantsUtil.PAYMENT_ACTIVITY_REQUEST_CODE -> {
          val status = data!!.getIntExtra("status", 0)
          if (status == 1) {
            topUpIsDon()
          } else {
            AlertManager.showErrorMessage(
              requireActivity(),
              getString(R.string.payment_failed)
            )
          }
        }
      }
    }
  }
}



