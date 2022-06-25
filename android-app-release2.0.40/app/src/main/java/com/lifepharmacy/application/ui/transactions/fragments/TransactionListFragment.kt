package com.lifepharmacy.application.ui.transactions.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentTransactionsBinding
import com.lifepharmacy.application.enums.ScrollingState
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.transactionListScreenOpen
import com.lifepharmacy.application.model.payment.TransactionMainModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.transactions.adapters.ClickTransaction
import com.lifepharmacy.application.ui.transactions.adapters.TransactionsAdapter
import com.lifepharmacy.application.ui.utils.ScrollStateListener
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.wallet.viewmodels.WalletViewModel
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.universal.Logger
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class TransactionListFragment : BaseFragment<FragmentTransactionsBinding>(),ClickTool, ClickTransaction {


  private val viewModel: WalletViewModel by activityViewModels()
  private var layoutManager: GridLayoutManager? = null

  private var pastVisibleItems: Int = 0
  private var visibleItemCount: Int = 0
  private var totalItemCount: Int = 0
  private var previous_total: Int = 0
  private var isLoading = true

  private lateinit var transactionsAdapter: TransactionsAdapter

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.transactionListScreenOpen()
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)

      initUI()
      observers()
    }

    viewModel.skip=0
    return mView
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

  }

  private fun initUI() {
    binding.lyTopBar.click =this
    binding.lyTopBar.tvToolbarTitle.text = getString(R.string.transaction_list)
    initTransactionListRV()
  }


  private fun initTransactionListRV() {
    transactionsAdapter = TransactionsAdapter(requireActivity(), this)
    layoutManager = GridLayoutManager(requireContext(), 1)
    binding.rvTransactions.layoutManager = layoutManager
    binding.rvTransactions.adapter = transactionsAdapter
    binding.rvTransactions.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        visibleItemCount = layoutManager!!.childCount
        totalItemCount = layoutManager!!.itemCount
        pastVisibleItems = layoutManager!!.findFirstCompletelyVisibleItemPosition()
        val visibleItem = layoutManager?.findLastCompletelyVisibleItemPosition()
        if (visibleItem != null) {
          if (visibleItem > viewModel.skip + 5 - viewModel.take)
            onBottomHit()
        }
//        if (!binding.recyclerViewProducts.canScrollVertically(1)) {
//          onBottomHit()
//        }
      }
    })
    binding.rvTransactions.post { // Call smooth scroll
      binding.rvTransactions.scrollToPosition(0)
    }
  }


  private fun observers() {
    getTransactionObserver()
  }


  private fun getTransactionObserver(){
    viewModel.getTransactions().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            it.data?.data?.let { data ->
              viewModel.skip = viewModel.skip + data.size
              previous_total = data.size
            }

            isLoading = false
            transactionsAdapter.setNewData(it.data?.data)
            hideLoading()
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

  private fun onBottomHit() {
      if (isLoading) {
        if (totalItemCount > previous_total) {
          isLoading = false
          previous_total = totalItemCount
        }
      }
      if (!isLoading && totalItemCount - visibleItemCount <= pastVisibleItems + viewModel.take) {
        pastVisibleItems++
        getTransactionObserver()
        isLoading = true
      }
  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_transactions
  }

  override fun permissionGranted(requestCode: Int) {
  }

  override fun onClickTransaction(transactionModel: TransactionMainModel) {
    findNavController().navigate(R.id.transactionDetailFragment,TransactionDetailFragment.getBundle(transactionModel))

  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }


}
