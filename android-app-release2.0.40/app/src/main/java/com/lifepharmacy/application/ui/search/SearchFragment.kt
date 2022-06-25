package com.lifepharmacy.application.ui.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import co.lujun.androidtagview.TagView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentSearchBinding
import com.lifepharmacy.application.enums.SearchState
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.clickAfterSearched
import com.lifepharmacy.application.managers.analytics.productsAfterSearched
import com.lifepharmacy.application.managers.analytics.searchScreenOpen
import com.lifepharmacy.application.managers.analytics.searchedTerm
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.model.search.SearchCategory
import com.lifepharmacy.application.model.search.agolia.Hits
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeProduct
import com.lifepharmacy.application.ui.dashboard.adapter.HomeProductAdapter
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.home.viewModel.HomeViewModel
import com.lifepharmacy.application.ui.productList.ProductListFragment
import com.lifepharmacy.application.ui.productList.viewmodel.ProductListViewModel
import com.lifepharmacy.application.ui.products.fragment.ProductFragment
import com.lifepharmacy.application.ui.products.viewmodel.ProductViewModel
import com.lifepharmacy.application.ui.profile.viewmodel.ProfileViewModel
import com.lifepharmacy.application.ui.search.adapters.*
import com.lifepharmacy.application.ui.search.viewmodels.SearchViewModel
import com.lifepharmacy.application.utils.universal.*
import com.lifepharmacy.application.utils.universal.ConstantsUtil.PERMISSION_LOCATIONS_REQUEST_CODE
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.livechatinc.inappchat.ChatWindowErrorType
import com.livechatinc.inappchat.ChatWindowView
import com.livechatinc.inappchat.models.NewMessageModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(),
  ClickHomeProduct, ClickSearchFragment, ClickTrendsSearchItem, ClickSearchCategoryItem,
  RecyclerPagingListener, ClickSearchSuggestionItem, ChatWindowView.ChatWindowEventsListener ,ClickSearchRecent{
  private val viewModel: SearchViewModel by viewModels()
  private val homeViewModel: HomeViewModel by activityViewModels()
  private val productListViewModel: ProductListViewModel by activityViewModels()
  private val productViewModel: ProductViewModel by activityViewModels()
  private val profileViewModel: ProfileViewModel by activityViewModels()

  private lateinit var categoryAdapter: SearchCategoryAdapter
  private lateinit var recommendedProducts: HomeProductAdapter
  private lateinit var searchSuggestionAdapter: SearchSuggestionsAdapter
  private lateinit var resultProducts: SearchResultAdapter
  private lateinit var searchTrending: SearchTrendingAdapter
  private lateinit var searchRecent: SearchRecentAdapter
  private lateinit var layoutManagerTrending: StaggeredGridLayoutManager
  private lateinit var layoutManagerRecent: StaggeredGridLayoutManager
  private lateinit var layoutManagerRecommeded: GridLayoutManager
  private var searchedText: String? = ""
//  var recentsearchget: ArrayList<String?> = ArrayList()

  private var layoutManager: GridLayoutManager? = null

  private lateinit var recyclerViewPagingUtil: RecyclerViewPagingUtil

  private var isLocationChangeFoundNearest = true
  private var fullScreenChatWindow: ChatWindowView? = null
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.searchScreenOpen()
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      viewModel.setTrending()
      initUI()
      observers()
      resetSkip()
    }
    initSearchView()
    return mView
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

  }

  private fun initUI() {
    binding.click = this
    binding.llEmptySearch.click = this
    binding.llRecent.click = this
    homeViewModel.getRecommended()
    binding.llRecent.title = getString(R.string.recent_search)
    binding.llCategory.title = getString(R.string.products_in_cat)
    binding.llRecommendations.title = getString(R.string.recommended_for_you)
    binding.llTrending.title = getString(R.string.trending_search)
    recommendationsRV()
    trendingRV()
    recentsearchRV()
    resultRecyclerView()
    initSuggestions()
//    resultProductRV()

    viewModel.searchState.value = SearchState.RECOMMENDED
    binding.profileViewModel = profileViewModel
    binding.lifecycleOwner = this
    binding.searchViewModel = viewModel
//    if (!viewModel.filerManager.searchQuery.isNullOrEmpty()){
//      binding.searchViewQuery.setQuery(viewModel.filerManager.searchQuery.toString(),true)
//    }
  }

  private fun initSearchCatRV(term: String?, arrayList: ArrayList<SearchCategory>?) {
    term?.let {
      categoryAdapter = SearchCategoryAdapter(requireActivity(), term = term, onItemTapped = this)
      binding.llCategory.recyclerView.adapter = categoryAdapter
      categoryAdapter.setDataChanged(arrayList)
    }
  }

  private fun recommendationsRV() {
    layoutManagerRecommeded =
      GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
    recommendedProducts = HomeProductAdapter(
      requireActivity(),
      this,
      viewModel.appManager,
      viewModel.appManager.storageManagers.config.backOrder ?: "Pre-Order"
    )
    binding.llRecommendations.recyclerView.layoutManager = layoutManagerRecommeded
    binding.llRecommendations.recyclerView.adapter = recommendedProducts

  }

  private fun initSuggestions() {
    searchSuggestionAdapter = SearchSuggestionsAdapter(requireActivity(), this)
    binding.rvSuggestion.adapter = searchSuggestionAdapter
  }

  private fun recentsearchRV() {
    layoutManagerRecent = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
    searchRecent = SearchRecentAdapter(requireActivity(), this)
    binding.llRecent.tagLayout.setOnTagClickListener(object : TagView.OnTagClickListener {
      override fun onTagClick(position: Int, text: String?) {
        if (text != null) {
          resetToFirstQuery(text)
        }
      }

      override fun onTagLongClick(position: Int, text: String?) {
      }

      override fun onSelectedTagDrag(position: Int, text: String?) {
      }

      override fun onTagCrossClick(position: Int) {
      }
    })
//    binding.llTrending.recyclerView.layoutManager = layoutManagerTrending
//    binding.llTrending.recyclerView.adapter = searchTrending

  }

  private fun trendingRV() {
    layoutManagerTrending = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
    searchTrending = SearchTrendingAdapter(requireActivity(), this)
    binding.llTrending.tagLayout.setOnTagClickListener(object : TagView.OnTagClickListener {
      override fun onTagClick(position: Int, text: String?) {
        if (text != null) {
          resetToFirstQuery(text)
        }
      }

      override fun onTagLongClick(position: Int, text: String?) {
      }

      override fun onSelectedTagDrag(position: Int, text: String?) {
      }

      override fun onTagCrossClick(position: Int) {
      }
    })
//    binding.llTrending.recyclerView.layoutManager = layoutManagerTrending
//    binding.llTrending.recyclerView.adapter = searchTrending

  }

  private fun tageLayoutStyling(list: ArrayList<Hits>?) {
    var trendingList = list?.map {
      it.query
    }
    binding.llTrending.tagLayout.setTags(trendingList)
  }

  private fun tagerecentLayoutStyling(list: ArrayList<String?>) {
    var recentList = list?.map {
      it
    }
    binding.llRecent.tagLayout.setTags(recentList)
  }

  private fun resultRecyclerView() {
    resultProducts = SearchResultAdapter(
      requireActivity(),
      this,
      this,
      viewModel.appManager,
      this,
      viewModel.appManager.storageManagers.config.backOrder ?: "Pre-Order"
    )
    layoutManager = GridLayoutManager(requireContext(), 1)
    binding.rvSearchResults.layoutManager = layoutManager
    recyclerViewPagingUtil = RecyclerViewPagingUtil(
      binding.rvSearchResults,
      layoutManager!!, this
    )
    binding.rvSearchResults.adapter = resultProducts
    binding.rvSearchResults.addOnScrollListener(recyclerViewPagingUtil)
    binding.rvSearchResults.post { // Call smooth scroll
      binding.rvSearchResults.scrollToPosition(0)
    }
  }

  private fun observers() {
    homeViewModel.recommendedMut.observe(viewLifecycleOwner, Observer {
      when (it.status) {
        Result.Status.SUCCESS -> {
          hideLoading()
          recommendedProducts.setDataChanged(it.data?.products)
//          it.data?.let { it1 -> viewModel.setTrending(it1) }

        }
        Result.Status.ERROR -> {
//          it.message?.let { it1 ->
//            AlertManager.showErrorMessage(
//              requireActivity(),
//              it1
//            )
//          }
          hideLoading()
        }
        Result.Status.LOADING -> {
          showLoading()
        }
      }
    })
    viewModel.suggestions.observe(viewLifecycleOwner) {
      it?.let {
        searchedText?.let { it1 -> searchSuggestionAdapter.setDataChanged(it, it1) }
      }
    }
    viewModel.trendings.observe(viewLifecycleOwner) {
      tageLayoutStyling(it)
      searchTrending.setDataChanged(it)
    }
    refershrecent()

  }

  private fun initSearchView() {
    binding.searchViewQuery.isIconified = false
    binding.searchViewQuery.setOnQueryTextListener(
      DebouncingQueryTextListener(
        viewLifecycleOwner.lifecycle,
        object : SearchDebounceListener {
          override fun onDebouncingQueryTextChange(text: String?) {
            try {
              if (text.isNullOrEmpty() || text.length < 2) {
                viewModel.searchState.value = SearchState.RECOMMENDED
              } else {
                if (viewModel.searchState.value == SearchState.SUGGESTIONS || viewModel.searchState.value == SearchState.NON
                  || viewModel.searchState.value == SearchState.RECOMMENDED
                ) {
                  searchedText = text
                  viewModel.appManager.analyticsManagers.searchedTerm(searchedText.toString())
                  viewModel.getSuggestions(text)
                } else {
                  viewModel.searchState.value = SearchState.NON
                }
//            binding.showRecommendations = true
              }
            } catch (e: Exception) {
              e.printStackTrace()
            }

          }

          override fun onSimpleTextChange(text: String?) {
            try {
              binding.searchViewQuery.isIconified = false
            } catch (e: Exception) {

            }


          }
        }, debouncePeriod = 5
      )
    )
    binding.searchViewQuery.onFocusChangeListener =
      View.OnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
          binding.searchViewQuery.isIconified = false
        }
      }
    binding.searchViewQuery.setOnCloseListener {
      viewModel.searchState.value = SearchState.RECOMMENDED
      viewModel.isSearchEmpty.value = false
      false
    }
  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_search
  }

  override fun permissionGranted(requestCode: Int) {
    if (requestCode == PERMISSION_LOCATIONS_REQUEST_CODE) {

    }
  }


  private fun observeSearch(query: String) {
    viewModel.searchQuery(query).observe(viewLifecycleOwner, Observer {
      when (it.status) {
        Result.Status.SUCCESS -> {
          hideLoading()
          if (viewModel.skip == 0) {

            viewModel.isSearchEmpty.value = it.data?.data?.products?.isEmpty() == true
          }


          it.data?.data?.let { it1 ->
            if (viewModel.skip == 0) {
              it1.categories?.let { it2 -> resultProducts.setCategoryResult(it2, it1.searchTerm) }
              binding.rvSearchResults.post { // Call smooth scroll
                binding.rvSearchResults.scrollToPosition(0)
              }
              resultProducts.refreshData(it.data?.data?.products)
            } else {
              resultProducts.setDataChanged(it.data?.data?.products)
            }
            it.data.data?.products?.let { data ->
              recyclerViewPagingUtil.nextPageLoaded(data.size)
            }
            it1.products?.let { it2 ->
              viewModel.appManager.analyticsManagers.productsAfterSearched(
                query = query,
                it2
              )
            }
//            KeyboardUtils.hideKeyboard(requireActivity(), binding.searchViewQuery)
          }

          viewModel.searchState.value = SearchState.SEARCHED
        }
        Result.Status.ERROR -> {
          it.message?.let { it1 ->
            AlertManager.showErrorMessage(
              requireActivity(),
              it1
            )
          }
          hideLoading()
        }
        Result.Status.LOADING -> {
          if (viewModel.skip < viewModel.take) {
            showLoading()
          }
          recyclerViewPagingUtil.isLoading = true
        }
      }
    })
  }

  override fun onProductClicked(productDetails: ProductDetails, position: Int) {
    try {
      productViewModel.position = position
      productViewModel.previewProductMut.value = productDetails
      findNavController().navigate(R.id.nav_product_preview)
    } catch (e: Exception) {
      e.printStackTrace()
    }

//    findNavController().navigate(
//      R.id.productFragment,
//      ProductFragment.getBundle(productID = productDetails.id)
//    )
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

  override fun onClickCancel() {
    findNavController().navigateUp()
  }

  override fun onClickViewAll() {
    productListViewModel.skip = 0
    viewModel.filerManager.searchQuery = searchedText
    findNavController().navigate(
      R.id.nav_product_listing,
      ProductListFragment.getProductListingBundle(searchedText, "", "", true)
    )
  }

  override fun onClickSearch() {
    binding.searchViewQuery.isIconified = false
  }

  override fun onClickOrderOutOfStock(productDetails: ProductDetails, position: Int) {
    viewModel.appManager.storageManagers.selectedOutOfstockProductItem = productDetails
    findNavController().navigate(R.id.orderOutOFStockFragment)
  }

  override fun onClickChat() {
    openLiveChat()
  }

  override fun onClickTrend(string: Hits) {
    resetToFirstQuery(string.query.toString())
  }

  override fun onClickSearchCategory(item: SearchCategory, term: String) {
    productListViewModel.skip = 0
    viewModel.filerManager.searchQuery = searchedText
//    item.categoryId?.let { viewModel.filerManager.addFirstFilter(it, "category") }
    findNavController().navigate(
      R.id.nav_product_listing,
      ProductListFragment.getProductListingBundle(
        "\'${term}\' ${getString(R.string.`in`)} ${item.categoryName}",
        "",
        "",
        true
      )
    )
  }

  override fun onClickNotify(productDetails: ProductDetails, position: Int) {
    notifyAboutProduct(productDetails)
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


  private fun resetSkip() {
    recyclerViewPagingUtil.skip = 0
    viewModel.skip = 0
  }

  override fun onNextPage(skip: Int, take: Int) {
    viewModel.skip = skip
    viewModel.take = take
    searchedText?.let { observeSearch(query = it) }
  }

  private fun refershrecent() {
    viewModel.appManager.persistenceManager.recentsearchget =
      appManager.persistenceManager.getArrayPrefs("recentsearch", requireContext())!!
    if (!viewModel.appManager.persistenceManager.recentsearchget.isNullOrEmpty()) {
      binding.showVisibility = true
      viewModel.appManager.persistenceManager.recentsearchget.let {
        tagerecentLayoutStyling(it)
        searchRecent.setDataChanged(it)
      }
    } else {
      binding.showVisibility = false
    }
  }

  override fun onClickSearchCategory(item: Hits) {
//    recent search logic
    viewModel.appManager.persistenceManager.recentsearch =
      appManager.persistenceManager.getArrayPrefs("recentsearch", requireContext())!!


    if (viewModel.appManager.persistenceManager.recentsearch.size >= 5) {
      viewModel.appManager.persistenceManager.recentsearch.removeAt(viewModel.appManager.persistenceManager.recentsearch.size - 1)
    }
    if (viewModel.appManager.persistenceManager.recentsearch.contains(item.getString())) {
      Log.e(TAG, "onClickSearchCategory: ${item.getString()}")
      Log.e(TAG, "onClickSearchCategory: same recent search item ignored")
    } else {
      viewModel.appManager.persistenceManager.recentsearch.add(0, item.getString())
    }



    viewModel.appManager.persistenceManager.setArrayPrefs(
      "recentsearch",
      appManager.persistenceManager.recentsearch,
      requireContext()
    );

    Logger.d(
      "getrecent",
      viewModel.appManager.persistenceManager.getArrayPrefs("recentsearch", requireContext())
        .toString()
    )

    refershrecent()

    viewModel.appManager.analyticsManagers.clickAfterSearched(item.queryID.toString(), hits = item)
    resetToFirstQuery(item.query.toString())
  }

  override fun onClickProduct(item: Hits) {
    viewModel.appManager.analyticsManagers.clickAfterSearched(item.queryID.toString(), hits = item)
    findNavController().navigate(
      R.id.nav_product,
      ProductFragment.getBundle(productID = item.id.toString(), item.position ?: 0)
    )
  }

  private fun resetToFirstQuery(query: String) {
    binding.searchViewQuery.setQuery(query, false)
    viewModel.searchState.value = SearchState.SEARCHED
    binding.searchViewQuery.isIconified = false
    viewModel.skip = 0
    searchedText = query
    observeSearch(query)
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

  override fun onResume() {
    super.onResume()
    searchRecent.notifyDataSetChanged()
  }

  override fun handleUri(uri: Uri?): Boolean {
    return false
  }

  override fun onClickClear() {
    Log.e(TAG, "onClickClear: ")
    viewModel.appManager.persistenceManager.recentsearch.clear()
    viewModel.appManager.persistenceManager.recentsearchget.clear()
    viewModel.appManager.persistenceManager.setArrayPrefs(
      "recentsearch",
      appManager.persistenceManager.recentsearch,
      requireContext()
    )
    viewModel.appManager.persistenceManager.getArrayPrefs("recentsearch", requireContext())?.clear()
    refershrecent()
//    binding.showVisibility = false
  }
}
