package com.lifepharmacy.application.ui.home.fragments


import android.content.BroadcastReceiver
import android.content.Intent
import android.location.Address
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentHomeRecyclerviewBinding
import com.lifepharmacy.application.enums.AddressChanged
import com.lifepharmacy.application.enums.AddressSelection
import com.lifepharmacy.application.interfaces.ClickSearchBar
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.LocationCustomManager
import com.lifepharmacy.application.managers.analytics.homeScreenOpen
import com.lifepharmacy.application.managers.analytics.productClicked
import com.lifepharmacy.application.model.blog.BlogModel
import com.lifepharmacy.application.model.home.HomeResponseItem
import com.lifepharmacy.application.model.home.SectionData
import com.lifepharmacy.application.model.notifications.InAppNotificationDataModel
import com.lifepharmacy.application.model.notifications.InAppNotificationMainModel
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.address.AddressSelectionActivity
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.dashboard.adapter.*
import com.lifepharmacy.application.ui.dashboard.viewmodel.DashboardViewModel
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.home.adapters.ClickHomeOffer
import com.lifepharmacy.application.ui.home.adapters.ClickHomeOffers
import com.lifepharmacy.application.ui.home.adapters.HomeAdapter
import com.lifepharmacy.application.ui.home.viewModel.HomeViewModel
import com.lifepharmacy.application.ui.livechat.LiveChatActivity
import com.lifepharmacy.application.ui.orders.fragments.MainOrdersFragment
import com.lifepharmacy.application.ui.pcr.fragments.PcrListFragment
import com.lifepharmacy.application.ui.productList.ProductListFragment
import com.lifepharmacy.application.ui.products.fragment.ProductFragment
import com.lifepharmacy.application.ui.products.viewmodel.ProductViewModel
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.splash.viewmodel.SplashViewModel
import com.lifepharmacy.application.ui.wallet.viewmodels.WalletViewModel
import com.lifepharmacy.application.ui.webActivity.WebViewActivity
import com.lifepharmacy.application.utils.DateTimeUtil
import com.lifepharmacy.application.utils.URLs
import com.lifepharmacy.application.utils.universal.*
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
class HomeRecyclerFragment : BaseFragment<FragmentHomeRecyclerviewBinding>(),
  SwipeRefreshLayout.OnRefreshListener,
  ClickHomeRecyclerFragment,
  LocationCustomManager.CustomeLocationCallback, ChatWindowView.ChatWindowEventsListener,
  ClickSearchBar, ClickHomeSubItem, ClickHomeProduct, ClickBlog, ClickLayoutHorizontalProducts,
  ClickHomeOffer, ClickHomeOffers {
  private val viewModel: HomeViewModel by activityViewModels()
  private val viewModelDashBoard: DashboardViewModel by activityViewModels()
  private val viewModelAddress: AddressViewModel by activityViewModels()
  private val walletVieModel: WalletViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()
  var inapp: Boolean = false

  //  private val categoryViewModel: CategoryViewModel by activityViewModels()
  private val productViewModel: ProductViewModel by activityViewModels()
  private val splashViewModel: SplashViewModel by viewModels()

  var broadcastReceiver: BroadcastReceiver? = null
  private var locationCustomManager: LocationCustomManager? = null

  private lateinit var homeAdapter: HomeAdapter

  private lateinit var blogAdapter: BlogAdapter

  private var isLocationChangeFoundNearest = true
  private var fullScreenChatWindow: ChatWindowView? = null
  lateinit var onBackPressedCallback: OnBackPressedCallback
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
        locationPermissionGranted()
      } else {
        checkForPermissions()
      }

    }

  private val addressContract =
    registerForActivityResult(AddressSelectionActivity.Contract()) { result ->
      result?.address?.let {
        viewModelAddress.deliveredAddressMut.value = it
        viewModelAddress.addressSelection = AddressSelection.NON
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Override this method to customize back press
    requireActivity().onBackPressedDispatcher.addCallback(this) {

    }
    onBackPressedCallback = object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        //some logic that needs to be run before fragment is destroyed
        if (fullScreenChatWindow != null) {
          (fullScreenChatWindow ?: return).hideChatWindow()
        } else {
          Utils.exitApp(requireActivity())
        }
      }
    }

    requireActivity().onBackPressedDispatcher.addCallback(
      onBackPressedCallback
    )

    val gson = Gson()
    val inAppNotificationModel = InAppNotificationMainModel()
    inAppNotificationModel.data?.add(InAppNotificationDataModel())
    val jsonStr = gson.toJson(inAppNotificationModel)

    Logger.i("InAppNotificationJson", jsonStr)

  }

  private fun checkForPermissions() {
    if (!checkPermissionStatus(ConstantsUtil.getLocationListNormal())) {
      checkLastAskPermission()
    }
  }

  private fun checkLastAskPermission() {
    if (DateTimeUtil.getTimeDifferenceBtwTwoDatesInHorse(
        viewModel.appManager.persistenceManager.getAskPermissionDateTime()?.let {
          DateTimeUtil.getUTCStringToDate(
            it
          )
        }
          ?: return,
        DateTimeUtil.getCurrentDateObject()
      ) > 24
    ) {
      findNavController().navigate(R.id.askPermissionDialog)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    //unregister listener here
    try {
      onBackPressedCallback.isEnabled = false
      onBackPressedCallback.remove()
    } catch (e: java.lang.Exception) {
      e.printStackTrace()
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.homeScreenOpen()
    if (mView == null) {
      Logger.d("FirstTime", "FirstTime")
      inapp = true
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      initRV()
      observers()

      requestLocationPermissions.launch(ConstantsUtil.getLocationListNormal())
    } else {
//      inappObserver()
      inapp = false
      homeAdapter.notifyDataSetChanged()
    }
    viewModel.appManager.filtersManager.clearAllIncludingParentFilter()
    homeAdapter.notifyProductsAdapter()
    addressChangeObserver()
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      profileViewModel.isLoggedIn.set(true)
    } else {
      profileViewModel.isLoggedIn.set(false)
    }
    viewModel.appManager.storageManagers.getSettings()
    viewModel.appManager.storageManagers.getNewAvailableSlots()
    reselectionObserver()
//    Logger.d("BuildType", com.lifepharmacy.application.BuildConfig.BUILD_TYPE)

    appManager.storageManagers.getRootCategories()
//    observerHomeNavBroadCast()
    return mView
  }

  fun initUI() {
    binding.layoutSearch.click = this
    binding.llBlog.click = this
    binding.layoutSearch.mViewModel = viewModelAddress
    binding.layoutSearch.lifecycleOwner = this
    binding.slMain.setOnRefreshListener(this)
    walletVieModel.requestCards()
    profileViewModel.updateCurrentUser()
    viewModelAddress.addressSelection = AddressSelection.NEAREST_ADDRESS
    viewModelAddress.requestAddress()
    viewModel.getRecommended()
    splashViewModel.getFilters()
    viewModel.appManager.wishListManager.requestWishListList()
    initBlogRV()
//        binding.animationView.visibility = View.VISIBLE
//        binding.animationView.playAnimation()
  }

  fun reselectionObserver() {
    viewModel.appManager.loadingState.homeReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
//          binding.rvMain.post { // Call smooth scroll
//            binding.rvMain.scrollToPosition(0)
//          }
          binding.rvMain.smoothScrollToPosition(0)
          viewModel.appManager.loadingState.homeReselected.value = false
        }
      }
    }
  }


  private fun initBlogRV() {
    binding.llBlog.title = getString(R.string.blog)
    blogAdapter = BlogAdapter(requireActivity(), this)
    binding.llBlog.recyclerViewTrends.adapter = blogAdapter

  }


  private fun initRV() {
    homeAdapter = HomeAdapter(
      requireActivity(), viewModel.appManager, this, this,
      this, this, this, this, this
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
    viewModel.getGpsListener().observe(viewLifecycleOwner, { gpsStatus ->
      when (gpsStatus) {
        is GpsStatus.Enabled -> {
          locationCustomManager?.startLocationServices()

        }
        is GpsStatus.Disabled -> {
          locationCustomManager?.stopLocationServices()
        }
      }
    })


  }

  private fun inappapiObserver() {
    Log.e(TAG, "inappmsgratingObserver: shifa api")
    viewModel.getInapmsgRating().observe(viewLifecycleOwner) {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            Log.e(TAG, "inappmsgratingObserver: shifa1")
            viewModel.appManager.persistenceManager.Inappmsgrating = it.data?.data
          }
          Result.Status.ERROR -> {

          }
          Result.Status.LOADING -> {

          }
        }
      }
    }
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_home_recyclerview
  }

  override fun permissionGranted(requestCode: Int) {
  }

  private fun locationPermissionGranted() {
    locationCustomManager = LocationCustomManager(requireActivity())
    locationCustomManager?.setLocationCallbackListener(this)
    locationCustomManager?.startLocationServices()
  }

  override fun onChange(location: Location?) {
    location?.let {
      viewModel.appManager.storageManagers.saveLatLng(
        LatLng(
          it.latitude,
          it.longitude
        )
      )
      if (viewModelAddress.addressSelection == AddressSelection.NEAREST_ADDRESS) {
        viewModel.addressManager.getShortestAddress(LatLng(location.latitude, location.longitude))
          ?.let {
            viewModelAddress.setDeliveredAddress(it)
          }
      }
    }
  }

  override fun address(address: Address?) {
    if (isLocationChangeFoundNearest) {
      viewModelAddress.deliveredAddressMut.value = address?.let {
        viewModel.addressManager.getAddressModelFromAddress(
          it
        )
      }
      viewModelAddress.addressSelection = AddressSelection.NEAREST_ADDRESS
      viewModelAddress.requestAddress()
      isLocationChangeFoundNearest = false
    }

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
    Log.e(TAG, "onResume: ")
    inapp = false
  }

//  private fun observerHomeNavBroadCast() {
//    broadcastReceiver = object : BroadcastReceiver() {
//      override fun onReceive(context: Context, intent: Intent) {
//        when (intent.action) {
//          ConstantsUtil.HOME_FG_NAV -> {
//            val notificationModel: NotificationPayLoad? = intent.getParcelableExtra("payload")
//            notificationModel?.let {
//              navigateToDestination(it)
//            }
//          }
//          else -> {
//          }
//        }
//      }
//    }
//    val filter = IntentFilter(ConstantsUtil.HOME_FG_NAV)
//    requireActivity().registerReceiver(broadcastReceiver, filter)
//  }


  override fun onClickSearch() {
    try {
      findNavController().navigate(R.id.nav_search)
    } catch (e: Exception) {

    }
  }

  override fun onClickAddress() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      viewModelAddress.isSelecting.set(true)
      addressContract.launch(true)
    } else {
      findNavController().navigate(R.id.nav_login_sheet)
    }

//    openGooglePlacesBox()
  }

  override fun onCLickEmpty() {

  }

  override fun onProductClicked(productDetails: ProductDetails, position: Int) {
    viewModel.appManager.analyticsManagers.productClicked(productDetails, position)
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

      } else if (type == "test-booking-screen") {

        if (!id.isNullOrEmpty()) {
          appManager.persistenceManager.collection_id = id
          Log.e(TAG, "onClickHomeSubItem: collection_id $id")
//          findNavController().navigate(
//            R.id.nav_pcr, PcrStartFragment.getPcrListingBundle(id)
//          )
          if (viewModel.appManager.persistenceManager.isLoggedIn()) {
            appManager.persistenceManager.pcritemselected.clear()
            findNavController().navigate(
              R.id.nav_pcr, PcrListFragment.getPcrListingBundle(id)
            )
          } else {
            findNavController().navigate(R.id.nav_login_sheet)
          }
        }

      } else if (type == "page") {
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
//          val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(id))
//          startActivity(browserIntent)
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
    inapp = false
    homeObserver()
//    blogObserver()
  }

  override fun onClickBlog(model: BlogModel) {
    model.link?.let { WebViewActivity.open(requireActivity(), it) }
  }

  override fun onClickBlogViewAll() {
    WebViewActivity.open(requireActivity(), URLs.BLOG_VIEW_ALL)
  }

  private fun homeObserver() {
//    inappapiObserver()
    Log.e(TAG, "homeObserver:shifa2 ")
    viewModel.appManager.storageManagers.getHomeData().observe(viewLifecycleOwner, Observer {
      it?.let { (homeItems) ->
        val filteredArray = homeItems?.filter { (isEnabled) ->
          isEnabled == true
        }
        initRV()
        homeAdapter.setDataChanged(filteredArray as ArrayList<HomeResponseItem>)
        CoroutineScope(Dispatchers.Main.immediate).launch {
          delay(100)
          binding.isLoading = false
          binding.slMain.isRefreshing = false
        }

        blogObserver()
        if (inapp == true) {
//          inappObserver()
//          inappbackupload()
          inappObserver()

        }


//        when (it.status) {
//          Result.Status.SUCCESS -> {
//            val filteredArray = it.data?.data?.filter { homeItem ->
//              homeItem.isEnabled == true
//            }
//            initRV()
//            homeAdapter.setDataChanged(filteredArray as ArrayList<HomeResponseItem>)
//            CoroutineScope(Dispatchers.Main.immediate).launch {
//              delay(100)
//              binding.isLoading = false
//              binding.slMain.isRefreshing = false
//            }
//            blogObserver()
//          }
//          Result.Status.ERROR -> {
//            binding.isLoading = false
//            binding.slMain.isRefreshing = false
//            it.message?.let { it1 ->
//              AlertManager.showErrorMessage(
//                requireActivity(),
//                it1
//              )
//            }
//
//          }
//          Result.Status.LOADING -> {
//            binding.isLoading = true
//            binding.slMain.isRefreshing = false
//            hideLoading()
//          }
//        }
      }
    })
  }

  private fun addressChangeObserver() {
    viewModelAddress.addressChanged.observe(viewLifecycleOwner, {
      it?.let {
        when (it) {
          AddressChanged.ADDRESS_CHANGED -> {
            inapp = false
            homeObserver()
          }
          AddressChanged.ADDRESS_UNCHANGED -> {

          }
        }
      }
    })
  }

  private fun notifyAboutProduct(productDetails: ProductDetails) {
    productViewModel.notifyProduct(productDetails).observe(viewLifecycleOwner) {
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
    }
  }

  private fun inappObserver() {
    viewModel.getInapmsgRating().observe(viewLifecycleOwner) {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            viewModel.appManager.persistenceManager.Inappmsgrating = it.data?.data
//            hideLoading()

            if (it.data?.data?.type == "inapp-message") {

              Log.e(
                TAG,
                "inappObserver: getCompianID ${viewModel.appManager.persistenceManager.getCampianid()}"
              )

              if (viewModel.appManager.persistenceManager.getCampianid() != null) {
                if (it.data?.data?.inapp_data?.campaign_id != viewModel.appManager.persistenceManager.getCampianid()) {
                  it.data?.data?.inapp_data?.campaign_id?.let { campaign_id ->
                    viewModel.appManager.persistenceManager.saveCampaignid(
                      campaign_id
                    )
                  }
                  it.data?.data?.inapp_data?.campaign_id?.let { it1 ->
                    appManager.persistenceManager.saveCompianID(
                      it1.toString()
                    )
                  }
                  findNavController().navigate(
                    R.id.inappmessageDialog, InappmessageDialog.getBundle(it.data.data!!)
                  )
                }
              }
            } else if (it.data?.data?.type == "rate-order") {
              viewModel.appManager.persistenceManager.suborderidraing =
                it.data.data!!.sub_order_id.toString()

              viewModel.appManager.persistenceManager.subordernumber =
                it.data.data!!.sub_order_number
              Log.e(
                TAG,
                "rate-order: getRatingid ${viewModel.appManager.persistenceManager.getRatingid()}"
              )

              if (viewModel.appManager.persistenceManager.suborderidraing != viewModel.appManager.persistenceManager.getRatingid()) {
                viewModel.appManager.persistenceManager.suborderidraing.let { rating_suborder_id ->
                  Log.e(TAG, "rate-order: saveRatingid: $rating_suborder_id")
                  viewModel.appManager.persistenceManager.saveRatingid(
                    rating_suborder_id
                  )
                }
                findNavController().navigate(
                  R.id.orderRatingInappBottomSheet,
                  OrderRatingInappBottomSheet.getBundle(it.data.data!!.sub_order_id.toString())
                )
              }


//              if (it.data?.data?.sub_order_id != viewModel.appManager.persistenceManager.getCompianID()) {
//
//                findNavController().navigate(
//                  R.id.orderRatingInappBottomSheet,
//                  OrderRatingInappBottomSheet.getBundle(it.data.data!!.sub_order_id.toString())
//                )
//              }
            }


//            activity?.findNavController(R.id.fragment)?.navigate(
//              R.id.inappmessageDialog,
//              InappmessageDialog.getBundle(
//                "${it.data?.data}"))
//            blogAdapter.setDataChanged(it.data)
//            homeAdapter.notifyBlog(it.data)
//            binding.llBlog.clMain.visibility = View.VISIBLE
          }
          Result.Status.ERROR -> {
//            it.message?.let { it1 -> AlertManager.showErrorMessage(requireActivity(), it1) }
//            hideLoading()
          }
          Result.Status.LOADING -> {
//            showLoading()
          }

        }
      }
    }
  }

  private fun inappbackupload() {
    var data = viewModel.appManager.persistenceManager.Inappmsgrating
    if (data != null) {
      if (data?.type == "inapp-message") {
        findNavController().navigate(
          R.id.inappmessageDialog,
          InappmessageDialog.getBundle(viewModel.appManager.persistenceManager.Inappmsgrating!!)
        )
      }
    } else {
      inappObserver()
    }
  }

  private fun blogObserver() {
    viewModel.getBlog().observe(viewLifecycleOwner) {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
//            blogAdapter.setDataChanged(it.data)
            homeAdapter.notifyBlog(it.data)
//            binding.llBlog.clMain.visibility = View.VISIBLE
          }
          Result.Status.ERROR -> {

          }
          Result.Status.LOADING -> {
//            binding.llBlog.clMain.visibility = View.GONE
          }
        }
      }
    }
  }

  private fun observeHomeFeedProducts(homeItem: HomeResponseItem) {
    viewModel.getHomeFeedProductData(viewModel.makeHomeProductFeedRequestModel(homeItem))
      .observe(viewLifecycleOwner) {
        it?.let {
          when (it.status) {
            Result.Status.SUCCESS -> {
//            blogAdapter.setDataChanged(it.data)
              it.data?.data?.let { it1 -> homeAdapter.updateArrayListHomeProducts(it1) }
              it.data?.data?.let { it1 -> homeAdapter.updateArrayListHomeProductssingle(it1) }
//            binding.llBlog.clMain.visibility = View.VISIBLE
            }
            Result.Status.ERROR -> {

            }
            Result.Status.LOADING -> {
//            binding.llBlog.clMain.visibility = View.GONE
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
      "rewards" -> {
        if (viewModel.appManager.persistenceManager.isLoggedIn()) {
          findNavController().navigate(R.id.nav_rewards)
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
      "cart" -> {
        findNavController().navigate(R.id.nav_cart)
      }
      "account" -> {
        requireActivity().findNavController(R.id.fragment).navigate(R.id.nav_profile)
      }
      "chat" -> {
        openLiveChat()
      }

    }
  }

  fun moveToPage(id: String, title: String? = "") {
    findNavController().navigate(
      R.id.homeLandingFragment, HomeLandingFragment.getLandingPageBundle(title, id)
    )
  }

  private fun openLiveChat() {
    LiveChatActivity.open(
      activity = requireActivity(),
      name = viewModel.appManager.persistenceManager.getLoggedInUser()?.name ?: "",
      email = viewModel.appManager.persistenceManager.getLoggedInUser()?.email ?: "",
      phone = viewModel.appManager.persistenceManager.getLoggedInUser()?.phone ?: "",
    )
//    val customParamsMap = HashMap<String, String>()
//    customParamsMap["phone"] = appManager.persistenceManager.getLoggedInUser()?.phone.toString()
//    customParamsMap["source"] = "profile"
//    val configuration = ChatWindowConfiguration(
//      ConstantsUtil.LIVE_CHAT_LICENSE,
//      "0",
//      appManager.persistenceManager.getLoggedInUser()?.name,
//      appManager.persistenceManager.getLoggedInUser()?.email,
//      customParamsMap
//    )
//    if (fullScreenChatWindow == null) {
//      fullScreenChatWindow = ChatWindowView.createAndAttachChatWindowInstance(requireActivity())
//      (fullScreenChatWindow ?: return).setUpWindow(configuration)
//      (fullScreenChatWindow ?: return).setUpListener(this)
//      (fullScreenChatWindow ?: return).initialize()
//    }
//    fullScreenChatWindow?.showChatWindow()
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
