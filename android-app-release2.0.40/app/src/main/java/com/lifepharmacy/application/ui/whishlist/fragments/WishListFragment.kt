package com.lifepharmacy.application.ui.whishlist.fragments

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
import com.lifepharmacy.application.databinding.FragmentWishlistBinding
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.wishListScreenOpen
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeProduct
import com.lifepharmacy.application.ui.productList.adapter.ProductAdapter
import com.lifepharmacy.application.ui.products.fragment.ProductFragment
import com.lifepharmacy.application.ui.products.viewmodel.ProductViewModel
import com.lifepharmacy.application.ui.whishlist.viewmodel.WishListViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.utils.AnalyticsUtil
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class WishListFragment : BaseFragment<FragmentWishlistBinding>(), ClickHomeProduct, ClickTool {
  private val viewModel: WishListViewModel by viewModels()
  private val productViewModel: ProductViewModel by activityViewModels()
  lateinit var productListAdapter: ProductAdapter

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.wishListScreenOpen()
    if (view == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      observers()
    }
    reselectionProfileObserver()
    return mView
  }

  private fun initUI() {
    binding.toolbarTitle.click = this
    binding.toolbarTitle.tvToolbarTitle.text = getString(R.string.wishList)

    productListAdapter = ProductAdapter(requireActivity(), this, this, viewModel.appManager,viewModel.appManager.storageManagers.config.backOrder ?: "Pre-Order")
    binding.rvItems.adapter = productListAdapter
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

  private fun observers() {
    viewModel.appManager.wishListManager.wishListItemsMut.observe(viewLifecycleOwner, Observer {
      it?.let {
        binding.showEmpty = it.isNullOrEmpty()
        productListAdapter.refreshData(it)
      }
    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_wishlist
  }

  override fun permissionGranted(requestCode: Int) {

  }

  companion object {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    @JvmStatic
    fun newInstance(isEditable: Boolean = false) =
      WishListFragment().apply {
        arguments = Bundle().apply {
          putBoolean("is_editable", isEditable)
        }
      }
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
    viewModel.appManager.offersManagers.removeProduct(requireActivity(), productDetails, position)
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
          Result.Status.SUCCESS -> {
            hideLoading()
            it.data?.message?.let { it1 -> AlertManager.showSuccessMessage(requireActivity(), it1) }
          }
          Result.Status.ERROR -> {
            it.message?.let { it1 -> AlertManager.showErrorMessage(requireActivity(), it1) }
            hideLoading()
          }
          Result.Status.LOADING -> {
            showLoading()
          }
        }
      }
    })
  }
}
