package com.lifepharmacy.application.ui.home.fragments


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Address
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.maps.model.LatLng
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentHomeRecyclerviewBinding
import com.lifepharmacy.application.databinding.FragmentLandingPageBinding
import com.lifepharmacy.application.enums.AddressChanged
import com.lifepharmacy.application.enums.AddressSelection
import com.lifepharmacy.application.interfaces.ClickSearchBar
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.LocationCustomManager
import com.lifepharmacy.application.managers.analytics.landingScreenOpen
import com.lifepharmacy.application.model.blog.BlogModel
import com.lifepharmacy.application.model.home.HomeResponseItem
import com.lifepharmacy.application.model.home.SectionData
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.dashboard.adapter.*
import com.lifepharmacy.application.ui.home.adapters.ClickHomeOffer
import com.lifepharmacy.application.ui.home.adapters.ClickHomeOffers
import com.lifepharmacy.application.ui.home.adapters.HomeAdapter
import com.lifepharmacy.application.ui.home.adapters.LandingAdapter
import com.lifepharmacy.application.ui.home.viewModel.HomeViewModel
import com.lifepharmacy.application.ui.orders.fragments.MainOrdersFragment
import com.lifepharmacy.application.ui.productList.ProductListFragment
import com.lifepharmacy.application.ui.products.fragment.ProductFragment
import com.lifepharmacy.application.ui.products.viewmodel.ProductViewModel
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
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
class HomeLandingFragment : BaseFragment<FragmentLandingPageBinding>(),
  SwipeRefreshLayout.OnRefreshListener, ClickLandingFragment,
  ChatWindowView.ChatWindowEventsListener,
  ClickSearchBar, ClickHomeSubItem, ClickHomeProduct, ClickLayoutHorizontalProducts,
  ClickHomeOffer, ClickHomeOffers {
  private val viewModel: HomeViewModel by activityViewModels()
  private val viewModelAddress: AddressViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()
  var broadcastReceiver: BroadcastReceiver? = null

  //  private val categoryViewModel: CategoryViewModel by activityViewModels()
  private val productViewModel: ProductViewModel by activityViewModels()


  private lateinit var homeAdapter: LandingAdapter

  private var isLocationChangeFoundNearest = true
  private var fullScreenChatWindow: ChatWindowView? = null
  var titleString: String? = ""
  var id: String? = ""

  companion object {
    fun getLandingPageBundle(
      title: String?,
      id: String?,
    ): Bundle {
      val bundle = Bundle()
      id?.let {
        bundle.putString("id", id)
      }
      title?.let {
        bundle.putString("title", title)
      }

      return bundle
    }
  }

  private val requestLocationPermissions =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
      var result = true
      granted.entries.forEach {
        if (!it.value) {
          result = false
          return@forEach
        }
      }
      if (result) {

      } else {
        AlertManager.permissionRequestPopup(requireActivity())
      }

    }


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.landingScreenOpen()
    arguments?.let {
      viewModel.title.value = it.getString("title")
      viewModel.id.value = it.getString("id")
    }
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      initRV()
      observers()
      requestLocationPermissions.launch(ConstantsUtil.getLocationListNormal())
    } else {
      homeAdapter.notifyDataSetChanged()
    }
    viewModel.appManager.filtersManager.clearAllIncludingParentFilter()
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      profileViewModel.isLoggedIn.set(true)
    } else {
      profileViewModel.isLoggedIn.set(false)
    }
    reselectionObserver()
//    Logger.d("BuildType", com.lifepharmacy.application.BuildConfig.BUILD_TYPE)

    return mView
  }

  fun initUI() {
    binding.slMain.setOnRefreshListener(this)
    binding.click = this
    binding.viemodel = viewModel
    binding.lifecycleOwner = this
    viewModel.filtersManager.clearFilters()
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

//  fun reselectionObserver() {
//    if (viewModel.appManager.loadingState.homeReselected.equals(true)) {
//      findNavController().navigate(R.id.nav_home)
//      viewModel.appManager.loadingState.homeReselected = false
//    }
//  }
  private fun initRV() {
    homeAdapter = LandingAdapter(
      requireActivity(), viewModel.appManager, this, this, this, this, this
    )
    binding.rvMain.setHasFixedSize(true)
    binding.rvMain.adapter = homeAdapter
    binding.rvMain.setItemViewCacheSize(50)
    binding.rvMain.setDrawingCacheEnabled(true)
    binding.rvMain.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH)
//    val viewPool = RecycledViewPool()
//    binding.rvMain.setRecycledViewPool(viewPool)
//    viewPool.setMaxRecycledViews(0, 20)
//    val animator: RecyclerView.ItemAnimator? = binding.rvMain.itemAnimator
//    if (animator is SimpleItemAnimator) {
//      animator.supportsChangeAnimations = false
//    }
//    binding.rvMain.itemAnimator?.changeDuration = 0
  }


  private fun observers() {
    homeObserver()
  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_landing_page
  }

  override fun permissionGranted(requestCode: Int) {
  }


  override fun onPause() {
    try {
      requireActivity().unregisterReceiver(broadcastReceiver)
    } catch (e: java.lang.Exception) {
      e.printStackTrace()
    }
    super.onPause()

  }

  override fun onResume() {
    super.onResume()
    observerHomeNavBroadCast()
  }

  private fun observerHomeNavBroadCast() {
    broadcastReceiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
          ConstantsUtil.HOME_FG_NAV -> {

          }
          else -> {
          }
        }
      }
    }
    val filter = IntentFilter(ConstantsUtil.HOME_NAV)
    requireActivity().registerReceiver(broadcastReceiver, filter)
  }


  override fun onClickBacK() {
    findNavController().navigateUp()
  }

  override fun onClickSearch() {
    try {
      findNavController().navigate(R.id.nav_search)
    } catch (e: Exception) {

    }
  }

  override fun onClickAddress() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      viewModelAddress.isSelecting.set(true)
      findNavController().navigate(R.id.nav_address)
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }

//    openGooglePlacesBox()
  }

  override fun onCLickEmpty() {

  }

  override fun onProductClicked(productDetails: ProductDetails, position: Int) {
    findNavController().navigate(
      R.id.nav_product,
      ProductFragment.getBundle(productID = productDetails.id, position)
    )
  }

  override fun onClickAddProduct(productDetails: ProductDetails, position: Int) {
    //event tracking
    viewModel.appManager.eventlistmainManager.eventTrakingItemAddedToCart(productDetails)
    viewModel.offersManagers.addProduct(requireActivity(), productDetails, position)
  }

  override fun onClickMinus(productDetails: ProductDetails, position: Int) {
    //event tracking
    viewModel.appManager.eventlistmainManager.eventTrakingItemRemovedFromCart(productDetails)
    viewModel.offersManagers.removeProduct(requireActivity(), productDetails, position)
  }

  override fun onClickPlus(productDetails: ProductDetails, position: Int) {
    //event tracking
    viewModel.appManager.eventlistmainManager.eventTrakingItemAddedToCart(productDetails)
    viewModel.offersManagers.addProduct(requireActivity(), productDetails, position)
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

  override fun onClickViewAll(id: String, title: String, type: String) {
//    viewModel.appManager.filtersManager.addFirstFilter(id, type)
    findNavController().navigate(
      R.id.nav_product_listing,
      ProductListFragment.getProductListingBundle(title, id, type)
    )
  }

  override fun onLoadSectionItems(homeResponseItem: HomeResponseItem) {
    observeHomeFeedProducts(homeResponseItem)
  }

  override fun onClickHomeSubItem(
    title: String?,
    id: String?,
    type: String?,
    sectionData: SectionData?
  ) {
    if (id != null && type != null) {
//      viewModel.appManager.filtersManager.addFirstFilter(id, type)
    }
    if (sectionData != null) {
      if (sectionData.maxPriceLimit != "0") {
        viewModel.appManager.filtersManager.fromPrice = sectionData.lowerPriceLimit
        viewModel.appManager.filtersManager.toPrice = sectionData.maxPriceLimit
        viewModel.appManager.filtersManager.checkAndAddRangeValue()
      }
      viewModel.appManager.filtersManager.searchQuery = id
    }

    type?.let {
      if (type == "screen") {
        if (id != null) {
          navigationForScreens(id)
        }

      } else  if (type == "page") {
        if (id != null) {
          if (title == null) {
            moveToPage(id)
          } else {
            moveToPage(id, title)
          }
        }
      } else if (type == "search") {
        try {
          findNavController().navigate(R.id.nav_search)
        } catch (e: Exception) {

        }
      } else if (type == "link") {
        try {
          if (id != null) {
            IntentAction.openLink(id, requireActivity())
          }
        } catch (e: Exception) {
          e.printStackTrace()
        }

      } else if (type == "products") {
        findNavController().navigate(
          R.id.nav_product_listing, ProductListFragment.getProductListingBundle(title, id, type)
        )
      } else {
        if (type == "product") {
          if (id.isNullOrEmpty()) {
            findNavController().navigate(
              R.id.nav_product_listing
            )
          } else {
            findNavController().navigate(
              R.id.nav_product,
              ProductFragment.getBundle(id, 0)
            )
          }
        } else {
          if (!id.isNullOrEmpty()) {
            findNavController().navigate(
              R.id.nav_product_listing, ProductListFragment.getProductListingBundle(title, id, type)
            )
          }
        }
      }
    }
  }

  override fun onRefresh() {
    homeObserver()
  }


  private fun homeObserver() {
    viewModel.getLandingPageData().observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            it.data?.data?.let { landingData ->
              val filteredArray = landingData?.filter { (isEnabled) ->
                isEnabled == true
              }
              initRV()
              homeAdapter.setDataChanged(filteredArray as ArrayList<HomeResponseItem>)
//              homeAdapter.setDataChanged(landingData)
            }
            hideLoading()
            binding.slMain.isRefreshing = false
          }
          Result.Status.ERROR -> {
            it.message?.let { it1 -> AlertManager.showErrorMessage(requireActivity(), it1) }
            hideLoading()
            binding.slMain.isRefreshing = false
          }
          Result.Status.LOADING -> {
            showLoading()
          }
        }
      }
    })
  }


  private fun notifyAboutProduct(productDetails: ProductDetails) {
    productViewModel.notifyProduct(productDetails).observe(viewLifecycleOwner, {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            it.data?.message?.let { it1 -> AlertManager.showSuccessMessage(requireActivity(), it1) }
            hideLoading()
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


  private fun observeHomeFeedProducts(homeItem: HomeResponseItem) {
    viewModel.getHomeFeedProductData(viewModel.makeHomeProductFeedRequestModel(homeItem))
      .observe(viewLifecycleOwner) {
        it?.let {
          when (it.status) {
            Result.Status.SUCCESS -> {
              it.data?.data?.let { it1 -> homeAdapter.updateArrayListHomeProducts(it1) }
              it.data?.data?.let { it1 -> homeAdapter.updateArrayListHomeProductssingle(it1) }
            }
            Result.Status.ERROR -> {

            }
            Result.Status.LOADING -> {
            }
          }
        }
      }
  }

  private fun navigationForScreens(screen: String) {
    when (screen) {
      "vouchers" -> {
        if (viewModel.appManager.persistenceManager.isLoggedIn()) {
          findNavController().navigate(R.id.nav_voucher)
        } else {
          findNavController().navigate(R.id.nav_login_sheet)
        }

      }
      "prescription_request" -> {
        if (viewModel.appManager.persistenceManager.isLoggedIn()) {
          findNavController().navigate(R.id.nav_prescription)
        } else {
          findNavController().navigate(R.id.nav_login_sheet)
        }
      }
      "orders" -> {
        if (viewModel.appManager.persistenceManager.isLoggedIn()) {
          findNavController().navigate(R.id.nav_orders)
        } else {
          findNavController().navigate(R.id.nav_login_sheet)
        }

      }
      "prescription_requests" -> {
        if (viewModel.appManager.persistenceManager.isLoggedIn()) {
          findNavController().navigate(R.id.nav_orders, MainOrdersFragment.getBundle(1))
        } else {
          findNavController().navigate(R.id.nav_login_sheet)
        }
      }
      "wallet" -> {
        if (viewModel.appManager.persistenceManager.isLoggedIn()) {
          findNavController().navigate(R.id.nav_wallet)
        } else {
          findNavController().navigate(R.id.nav_login_sheet)
        }
      }
      "account" -> {
        requireActivity().findNavController(R.id.fragment).navigate(R.id.nav_profile)
      }
      "cart" -> {
        findNavController().navigate(R.id.nav_cart)
      }
      "chat" -> {
        openLiveChat()
      }

    }
  }

  fun moveToPage(id: String, title: String? = "") {
    findNavController().navigate(
      R.id.homeLandingFragment, getLandingPageBundle(title, id)
    )

  }

  private fun openLiveChat() {
    val customParamsMap = HashMap<String, String>()
    customParamsMap["phone"] = appManager.persistenceManager.getLoggedInUser()?.phone.toString()
    customParamsMap["source"] = "profile"
    val configuration = ChatWindowConfiguration(
      ConstantsUtil.LIVE_CHAT_LICENSE,
      "0",
      appManager.persistenceManager.getLoggedInUser()?.name,
      appManager.persistenceManager.getLoggedInUser()?.email,
      customParamsMap
    )
    if (fullScreenChatWindow == null) {
      fullScreenChatWindow = ChatWindowView.createAndAttachChatWindowInstance(requireActivity())
      (fullScreenChatWindow ?: return).setUpWindow(configuration)
      (fullScreenChatWindow ?: return).setUpListener(this)
      (fullScreenChatWindow ?: return).initialize()
    }
    fullScreenChatWindow?.showChatWindow()
  }

  override fun onChatWindowVisibilityChanged(visible: Boolean) {

  }

  override fun onNewMessage(message: NewMessageModel?, windowVisible: Boolean) {

  }

  override fun onStartFilePickerActivity(intent: Intent?, requestCode: Int) {

  }

  override fun onError(
    errorType: ChatWindowErrorType?,
    errorCode: Int,
    errorDescription: String?
  ): Boolean {
    return false
  }

  override fun handleUri(uri: Uri?): Boolean {
    return false
  }

  override fun onClickOffer() {

  }

  override fun onClickOfferViewAll() {
    findNavController().navigate(R.id.offerListingFragment)
  }

  override fun onClickOfferCategory() {

  }

}
