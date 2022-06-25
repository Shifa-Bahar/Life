package com.lifepharmacy.application.ui.transactions.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.*
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.transactionScreenOpen
import com.lifepharmacy.application.model.payment.CardMainModel
import com.lifepharmacy.application.model.payment.TransactionMainModel
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.card.ClickCard
import com.lifepharmacy.application.ui.categories.adapters.*
import com.lifepharmacy.application.ui.orders.fragments.OrderDetailFragment
import com.lifepharmacy.application.ui.transactions.viewmodels.TransactionViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class TransactionDetailFragment : BaseFragment<FragmentTransactionDetailBinding>(),
  ClickTransactionDetailFragment, ClickCard, ClickTool {
  private val viewModel: TransactionViewModel by viewModels()

  companion object {
    fun getBundle(transactionModel: TransactionMainModel): Bundle {
      val bundle = Bundle()
      transactionModel.let {
        bundle.putParcelable("transaction", transactionModel)
      }
      return bundle
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      viewModel.selectedTransaction.value = it.getParcelable("transaction")!!
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.transactionScreenOpen()
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      observers()
    }

    return mView
  }

  private fun initUI() {
    binding.lyTopBar.click = this
    binding.lyTopBar.tvToolbarTitle.text = getString(R.string.detail_title)
    binding.click = this
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    binding.lyTransctionDetails.viewModel = viewModel
    binding.lyTransctionDetails.lifecycleOwner = this
  }


  private fun observers() {
  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_transaction_detail
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

  override fun onClickViewOrder() {
    requireActivity().findNavController(R.id.fragment).navigate(
      R.id.nav_order_details,
      OrderDetailFragment.getOrderDetailBundle(viewModel.selectedTransaction.value?.orderId.toString())
    )
  }

  override fun onClickReturnToCard() {
    returnTOCard()
  }

  private fun returnTOCard() {
    viewModel.returnToCard()
      .observe(viewLifecycleOwner, {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()

            val successMessage =
              "${getString(R.string.dear)} ${viewModel.appManager.persistenceManager.getLoggedInUser()?.name}, ${
                getString(R.string.order_return_text)
              }"
            AlertManager.showSuccessMessage(
              requireActivity(),
              successMessage
            )
            binding.btnReturnToCard.isEnabled = false
          }
          Result.Status.ERROR -> {
            hideLoading()
            it.message?.let { it1 ->
              AlertManager.showErrorMessage(
                requireActivity(),
                it1
              )
            }
            binding.btnReturnToCard.isEnabled = false
          }
          Result.Status.LOADING -> {
            showLoading()
          }
        }
      })
  }

}



