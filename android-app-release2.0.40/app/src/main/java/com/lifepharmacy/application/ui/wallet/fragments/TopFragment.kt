package com.lifepharmacy.application.ui.wallet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentTopUpBinding
import com.lifepharmacy.application.enums.PaymentType
import com.lifepharmacy.application.managers.analytics.topUpOpen
import com.lifepharmacy.application.model.payment.CardMainModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.card.ClickCard
import com.lifepharmacy.application.ui.card.ClickCardLayout
import com.lifepharmacy.application.ui.card.WalletCardsAdapter
import com.lifepharmacy.application.ui.categories.adapters.*
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.wallet.viewmodels.TopViewModel
import com.lifepharmacy.application.ui.wallet.viewmodels.WalletViewModel
import com.lifepharmacy.application.utils.AnalyticsUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class TopFragment : BaseFragment<FragmentTopUpBinding>(),
  ClickTopFragment, ClickCard, ClickTool,ClickCardLayout {
  private val viewModel: TopViewModel by activityViewModels()
  private val walletViewModel: WalletViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()

  private lateinit var cardAdapter: WalletCardsAdapter
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    walletViewModel.appManager.analyticsManagers.topUpOpen()
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
    }
    observers()
    return mView
  }

  private fun initUI() {
    binding.lyTopBar.click = this
    binding.lyCards.click = this
    binding.lyTopBar.tvToolbarTitle.text = getString(R.string.top_up)
    binding.lyWalletAmount.mViewModel = profileViewModel
    binding.lyWalletAmount.lifecycleOwner = this
    binding.click = this
    binding.lyCards.title = "Choose The Card"
    binding.lyCards.type = "Top"
    binding.lyWalletAmount.type = "Top"
    initCardRV()
  }

  private fun initCardRV() {
    cardAdapter = WalletCardsAdapter(requireActivity(), this, "TOP")
    binding.lyCards.rvCards.adapter = cardAdapter
  }

  private fun observers() {
    walletViewModel.cardsDataMut
      .observe(viewLifecycleOwner, Observer {
        when (it.status) {
          Result.Status.SUCCESS -> {
            cardAdapter.setDataChanged(it.data)
          }
          Result.Status.ERROR -> {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
              .show()
          }
          Result.Status.LOADING -> {

          }
        }
      })
  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_top_up
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onCardSelect(position: Int?,cardMainModel: CardMainModel,pg:Int) {
    viewModel.viaNewCard = false
    viewModel.cardMainModel = cardMainModel
    viewModel.card_pg = pg
    viewModel.selectedPaymentType.value = PaymentType.CARD
    findNavController().navigate(R.id.topAmountFragment)
  }

  override fun onDeleteCard(position: Int?, cardMainModel: CardMainModel) {

  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }

  override fun onClickManage() {

  }

  override fun onClickViaNewCard() {
    viewModel.viaNewCard = true
    viewModel.selectedPaymentType.value = PaymentType.NEW
    findNavController().navigate(R.id.topAmountFragment)
  }

}



