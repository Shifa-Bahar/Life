package com.lifepharmacy.application.ui.offers.fragments


import android.Manifest
import android.content.Intent
import android.location.Address
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.multidex.BuildConfig
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.maps.model.LatLng
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentHomeRecyclerviewBinding
import com.lifepharmacy.application.databinding.FragmentOfferListingBinding
import com.lifepharmacy.application.enums.AddressChanged
import com.lifepharmacy.application.enums.AddressSelection
import com.lifepharmacy.application.interfaces.ClickSearchBar
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.LocationCustomManager
import com.lifepharmacy.application.managers.analytics.offersListScreenOpen
import com.lifepharmacy.application.model.blog.BlogModel
import com.lifepharmacy.application.model.home.HomeResponseItem
import com.lifepharmacy.application.model.home.SectionData
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.categories.viewmodel.CategoryViewModel
import com.lifepharmacy.application.ui.dashboard.adapter.*
import com.lifepharmacy.application.ui.home.adapters.*
import com.lifepharmacy.application.ui.home.viewModel.HomeViewModel
import com.lifepharmacy.application.ui.orders.fragments.MainOrdersFragment
import com.lifepharmacy.application.ui.productList.ProductListFragment
import com.lifepharmacy.application.ui.products.fragment.ProductFragment
import com.lifepharmacy.application.ui.products.viewmodel.ProductViewModel
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.splash.viewmodel.SplashViewModel
import com.lifepharmacy.application.ui.utils.topbar.ClickTool
import com.lifepharmacy.application.ui.wallet.viewmodels.WalletViewModel
import com.lifepharmacy.application.ui.webActivity.WebViewActivity
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.URLs
import com.lifepharmacy.application.utils.universal.*
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.livechatinc.inappchat.ChatWindowErrorType
import com.livechatinc.inappchat.ChatWindowView
import com.livechatinc.inappchat.models.NewMessageModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class OfferListingFragment : BaseFragment<FragmentOfferListingBinding>(), ClickHomeOffer,
  ClickTool {
  private val viewModel: HomeViewModel by activityViewModels()


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.offersListScreenOpen()
    if (mView == null) {

      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
    }

    return mView
  }

  fun initUI() {
    binding.llTop.tvToolbarTitle.text = "Offers For You "
    binding.llTop.click = this
    val offersAdapter = OffersListingAdapter(requireActivity(), this)
    binding.recyclerViewOffers.adapter = offersAdapter
    val offerCategoryAdapter = OfferCategoryAdapter(activity, object : ClickOffersCategory {
      override fun onClickOfferCategory(position: Int) {
      }

    })
    binding.rvTags.adapter = offerCategoryAdapter
    offerCategoryAdapter.setItemSelected(0)
//        binding.animationView.visibility = View.VISIBLE
//        binding.animationView.playAnimation()
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_offer_listing
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickOffer() {

  }

  override fun onClickBack() {
    findNavController().navigateUp()
  }


}
