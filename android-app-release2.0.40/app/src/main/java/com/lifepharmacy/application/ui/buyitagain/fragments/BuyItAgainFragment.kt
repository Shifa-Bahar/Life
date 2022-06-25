package com.lifepharmacy.application.ui.buyitagain.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentBuyItAgainBinding
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.buyItAgainScreenOpen
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.ui.buyitagain.viewmodel.BuyAgainViewModel
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeProduct
import com.lifepharmacy.application.ui.productList.adapter.ProductAdapter
import com.lifepharmacy.application.ui.products.viewmodel.ProductViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.universal.DebouncingQueryTextListener
import com.lifepharmacy.application.utils.universal.SearchDebounceListener
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class BuyItAgainFragment : BaseFragment<FragmentBuyItAgainBinding>(), ClickHomeProduct, ClickTool {
  private val viewModel: BuyAgainViewModel by viewModels()
  private val productViewModel: ProductViewModel by activityViewModels()
  lateinit var buyItAgainAdapter: ProductAdapter

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    if (view == null) {
      viewModel.appManager.analyticsManagers.buyItAgainScreenOpen()
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      observers()
    }
    reselectionProfileObserver()
    return mView
  }

  private fun initUI() {
    binding.toolbarTitle.click = this
    binding.toolbarTitle.tvToolbarTitle.text = getString(R.string.buy_it_again)
    buyItAgainAdapter = ProductAdapter(
      requireActivity(),
      this,
      this,
      viewModel.appManager,
      viewModel.appManager.storageManagers.config.backOrder ?: "Pre-Order"
    )
    binding.rvItems.adapter = buyItAgainAdapter
    initSearchView()
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
  private fun initSearchView() {
    binding.searchViewQuery.isIconified = false
    binding.searchViewQuery.setOnQueryTextListener(
      DebouncingQueryTextListener(
        viewLifecycleOwner.lifecycle,
        object : SearchDebounceListener {
          override fun onDebouncingQueryTextChange(text: String?) {
            text?.let {
              if (it.isNotEmpty()) {
                buyItAgainAdapter.applySearch(it)
              }
            }
          }

          override fun onSimpleTextChange(text: String?) {
            binding.searchViewQuery.isIconified = false

          }
        })
    )
//    binding.searchViewQuery.setOnCloseListener {
//      binding.showRecommendations = true
//      false
//    }
  }

  private fun observers() {
    viewModel.getProducts().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          com.lifepharmacy.application.network.Result.Status.SUCCESS -> {
            hideLoading()
            buyItAgainAdapter.setDataChanged(it.data?.data)
          }
          com.lifepharmacy.application.network.Result.Status.ERROR -> {
            hideLoading()
          }
          com.lifepharmacy.application.network.Result.Status.LOADING -> {
            showLoading()
          }
        }
      }
    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_buy_it_again
  }

  override fun permissionGranted(requestCode: Int) {

  }


  override fun onClickBack() {
    findNavController().navigateUp()
  }

  override fun onProductClicked(productDetails: ProductDetails, position: Int) {

  }

  override fun onClickAddProduct(productDetails: ProductDetails, position: Int) {
    //event tracking
    viewModel.appManager.eventlistmainManager.eventTrakingItemAddedToCart(productDetails)
    viewModel.appManager.offersManagers.addProduct(requireActivity(), productDetails, position)
  }

  override fun onClickMinus(productDetails: ProductDetails, position: Int) {
    //event tracking
    viewModel.appManager.eventlistmainManager.eventTrakingItemRemovedFromCart(productDetails)
    viewModel.appManager.offersManagers.removeProduct(
      requireActivity(),
      productDetails,
      position
    )
  }

  override fun onClickPlus(productDetails: ProductDetails, position: Int) {
    //event tracking
    viewModel.appManager.eventlistmainManager.eventTrakingItemAddedToCart(productDetails)
    viewModel.appManager.offersManagers.addProduct(requireActivity(), productDetails, position)
  }

  override fun onClickWishList(productDetails: ProductDetails, position: Int) {
    viewModel.appManager.wishListManager.selectUnselected(productDetails)
    //event tracking
    viewModel.appManager.eventlistmainManager.eventTrakingItemAddedAndRemovedWishlist(
      productDetails,
      appManager.persistenceManager.wishlistselected
    )
  }

  override fun onClickNotify(productDetails: ProductDetails, position: Int) {
    notifyAboutProduct(productDetails)
  }

  override fun onClickOrderOutOfStock(productDetails: ProductDetails, position: Int) {
    viewModel.appManager.storageManagers.selectedOutOfstockProductItem = productDetails
    findNavController().navigate(R.id.orderOutOFStockFragment)
  }

  private fun notifyAboutProduct(productDetails: ProductDetails) {
    productViewModel.notifyProduct(productDetails).observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          com.lifepharmacy.application.network.Result.Status.SUCCESS -> {
            hideLoading()
            it.data?.message?.let { it1 -> AlertManager.showSuccessMessage(requireActivity(), it1) }
          }
          com.lifepharmacy.application.network.Result.Status.ERROR -> {
            it.message?.let { it1 -> AlertManager.showErrorMessage(requireActivity(), it1) }
            hideLoading()
          }
          com.lifepharmacy.application.network.Result.Status.LOADING -> {
            showLoading()
          }
        }
      }
    })
  }
}
