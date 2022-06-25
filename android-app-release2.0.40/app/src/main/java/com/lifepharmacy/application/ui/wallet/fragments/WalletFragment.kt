package com.lifepharmacy.application.ui.wallet.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentWalletBinding
import com.lifepharmacy.application.enums.PaymentType
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.walletOpen
import com.lifepharmacy.application.model.payment.CardMainModel
import com.lifepharmacy.application.model.payment.TransactionMainModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.card.ClickCard
import com.lifepharmacy.application.ui.card.ClickCardLayout
import com.lifepharmacy.application.ui.card.WalletCardsAdapter
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.transactions.adapters.ClickTransaction
import com.lifepharmacy.application.ui.transactions.adapters.WalletTransactionsAdapter
import com.lifepharmacy.application.ui.transactions.fragments.TransactionDetailFragment
import com.lifepharmacy.application.ui.utils.dailogs.AnimationDialog.Companion.TAG
import com.lifepharmacy.application.ui.wallet.viewmodels.TopViewModel
import com.lifepharmacy.application.ui.wallet.viewmodels.WalletViewModel
import com.lifepharmacy.application.utils.AnalyticsUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class WalletFragment : BaseFragment<FragmentWalletBinding>(),
  ClickWalletFragment, ClickCard, ClickTransaction, ClickTool, ClickCardLayout {
  private val viewModel: WalletViewModel by activityViewModels()
  private val topViewModel: TopViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()
  private lateinit var cardAdapter: WalletCardsAdapter
  private lateinit var transactionsAdapter: WalletTransactionsAdapter
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.walletOpen()
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
    }
    viewModel.skip = 0
    viewModel.isThereAnyCard.value = false
    viewModel.isThereAnyTransactions.value = false
    observers()
    reselectionObserver()
    reselectionProfileObserver()
    return mView
  }

  private fun initUI() {
    binding.lyTopBar.click = this
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    binding.lyTransactions.click = this
    binding.lyTopBar.tvToolbarTitle.text = getString(R.string.wallet)
    binding.lyWalletAmount.mViewModel = profileViewModel
    binding.lyWalletAmount.lifecycleOwner = this
    binding.click = this
    binding.lyCards.click = this
    binding.lyWalletAmount.click = this
    binding.lyCards.title = "Saved Cards"
    binding.lyCards.type = "Wallet"
    initCardRV()
    initTransactionRV()
    viewModel.requestCards()

  }
  fun reselectionObserver() {
    viewModel.appManager.loadingState.homeReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
          findNavController().navigate(R.id.nav_home)
          viewModel.appManager.loadingState.homeReselected.value = false
        }
      }
    }
  }
  fun reselectionProfileObserver() {
    viewModel.appManager.loadingState.profileReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
          findNavController().navigate(R.id.nav_profile)
          viewModel.appManager.loadingState.profileReselected.value = false
        }
      }
    }
  }
  private fun initCardRV() {
    cardAdapter = WalletCardsAdapter(requireActivity(), this)
    binding.lyCards.rvCards.adapter = cardAdapter
  }

  private fun initTransactionRV() {
    transactionsAdapter = WalletTransactionsAdapter(requireActivity(), this)
    binding.lyTransactions.rvTransactions.adapter = transactionsAdapter
  }

  private fun observers() {
    viewModel.cardsDataMut
      .observe(viewLifecycleOwner) {
        when (it.status) {
          Result.Status.SUCCESS -> {
            cardAdapter.setDataChanged(it.data)
            if (!it.data.isNullOrEmpty()) {
              viewModel.isThereAnyCard.value = true
            }
          }
          Result.Status.ERROR -> {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
              .show()
          }
          Result.Status.LOADING -> {

          }
        }
      }
    viewModel.getTransactions()
      .observe(viewLifecycleOwner) {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            transactionsAdapter.setDataChanged(it.data?.data)
            if (!it.data?.data.isNullOrEmpty()) {
              viewModel.isThereAnyTransactions.value = true
            }
          }
          Result.Status.ERROR -> {
            Log.e(TAG, "observers: 1")
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
      }
  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_wallet
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onCardSelect(position: Int?, cardMainModel: CardMainModel,pg:Int) {
    Log.e(TAG, "onCardSelect: $pg")
    viewModel.card_pg = pg
    topViewModel.card_pg = pg
    topViewModel.selectedPaymentType.value = PaymentType.CARD

  }

  override fun onDeleteCard(position: Int?, cardMainModel: CardMainModel) {

  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }

  override fun onClickManage() {
    findNavController().navigate(R.id.manageCardsFragment)
  }

  override fun onClickViaNewCard() {
    topViewModel.selectedPaymentType.value = PaymentType.NEW

  }

  override fun onClickTopUp() {
    if(viewModel.appManager.storageManagers.config.isPopupEnabled == true){
      findNavController().navigate(R.id.topFragment)
    }else{
      Log.e(TAG, "onClickTopUp: ")
      AlertManager.showInfoAlertDialog(requireActivity(),getString(R.string.top_up),getString(R.string.feature_not_available))
    }

  }

  override fun onClickTransactionViewAll() {
    findNavController().navigate(R.id.transactionListFragment)
  }

  override fun onClickTransaction(transactionModel: TransactionMainModel) {
//    viewModel.selectedTransaction = transactionModel
    findNavController().navigate(R.id.transactionDetailFragment, TransactionDetailFragment.getBundle(transactionModel))
//    findNavController().navigate(R.id.transactionDetailFragment)
  }

}



