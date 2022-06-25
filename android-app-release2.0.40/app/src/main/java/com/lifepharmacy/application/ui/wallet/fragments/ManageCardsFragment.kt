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
import com.lifepharmacy.application.databinding.*
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.manageCardsScreenOpen
import com.lifepharmacy.application.model.payment.CardMainModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.card.ClickCard
import com.lifepharmacy.application.ui.card.WalletCardsAdapter
import com.lifepharmacy.application.ui.categories.adapters.*
import com.lifepharmacy.application.ui.dashboard.DashboardActivity
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.wallet.viewmodels.TopViewModel
import com.lifepharmacy.application.ui.wallet.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class ManageCardsFragment : BaseFragment<FragmentManageCardsBinding>(),
  ClickManageCardFragment, ClickCard, ClickTool {
  private val viewModel: TopViewModel by viewModels()
  private val walletViewModel: WalletViewModel by activityViewModels()
  private lateinit var cardAdapter: WalletCardsAdapter
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    walletViewModel.appManager.analyticsManagers.manageCardsScreenOpen()
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      observers()
    }

    return mView
  }

  private fun initUI() {
    binding.lyTopBar.click = this
    binding.lyTopBar.tvToolbarTitle.text = getString(R.string.manage_cards)
    binding.click = this
    binding.lyCards.title = getString(R.string.save_cards)
    binding.lyCards.type = getString(R.string.wallet)
    initCardRV()
  }

  private fun initCardRV() {
    cardAdapter = WalletCardsAdapter(requireActivity(), this, "Manage")
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
    return R.layout.fragment_manage_cards
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onCardSelect(position: Int?, cardMainModel: CardMainModel,pg:Int) {

  }

  override fun onDeleteCard(position: Int?, cardMainModel: CardMainModel) {
    deleteCard(cardMainModel)
  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }

  private fun deleteCard(cardMainModel: CardMainModel) {
    cardMainModel.id?.let { walletViewModel.makeCardDeleteBody(it) }?.let { it ->
      walletViewModel.deleteCard(it).observe(viewLifecycleOwner, Observer {result->
        result?.let {
          when (result.status) {
            Result.Status.SUCCESS -> {
              hideLoading()
              walletViewModel.requestCards()
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
        }
      })
    }
  }

}



