package com.lifepharmacy.application.ui.productList


import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.RangeSlider
import com.google.gson.Gson
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentProductListBinding
import com.lifepharmacy.application.interfaces.ClickSearchBarProduct
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.filterApplied
import com.lifepharmacy.application.managers.analytics.productClicked
import com.lifepharmacy.application.managers.analytics.productFetched
import com.lifepharmacy.application.managers.analytics.productListingScreenOpen
import com.lifepharmacy.application.model.category.Section
import com.lifepharmacy.application.model.filters.FilterMainRequest
import com.lifepharmacy.application.model.filters.FilterModel
import com.lifepharmacy.application.model.filters.FilterRequestModel
import com.lifepharmacy.application.model.filters.FilterTypeModel
import com.lifepharmacy.application.model.home.SectionData
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.model.product.ProductListingMainModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeProduct
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeSubItem
import com.lifepharmacy.application.ui.filters.adapters.ClickFilterCheckBox
import com.lifepharmacy.application.ui.filters.adapters.ClickFilterType
import com.lifepharmacy.application.ui.filters.fragments.FiltersBottomSheet
import com.lifepharmacy.application.ui.productList.adapter.*
import com.lifepharmacy.application.ui.productList.viewmodel.ProductListViewModel
import com.lifepharmacy.application.ui.products.fragment.ProductFragment
import com.lifepharmacy.application.ui.products.viewmodel.ProductViewModel
import com.lifepharmacy.application.ui.utils.dailogs.AnimationDialog.Companion.TAG
import com.lifepharmacy.application.utils.universal.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class ProductListFragment() : BaseFragment<FragmentProductListBinding>(), FilterOption,
  ClickProductListFragment, ClickHomeSubItem, ClickHomeProduct, SuperSellerQuickOption,
  ClickSearchBarProduct, RecyclerPagingListener, ClickFilterType, ClickFilterCheckBox {
  companion object {
    fun getProductListingBundle(
      title: String?,
      id: String?,
      type: String?,
      isSearch: Boolean = false,
      imageUrl: String? = ""
    ): Bundle {
      val bundle = Bundle()
      id?.let {
        bundle.putString("id", id)
      }
      type?.let {
        bundle.putString("type", type)
      }
      title?.let {
        bundle.putString("title", title)
      }
      isSearch?.let {
        bundle.putBoolean("isSearch", isSearch)
      }
      imageUrl?.let {
        bundle.putString("imageUrl", imageUrl)
      }
      return bundle
    }
  }

  private val viewModel: ProductListViewModel by activityViewModels()
  private val productViewModel: ProductViewModel by activityViewModels()
  private lateinit var filterTopTypesAdapter: FilterTopTypesAdapter

  //    private lateinit var discountAdapter: DiscountAdapter
  private lateinit var gridProductAdapter: GridProductAdapter
  private lateinit var productListAdapter: ProductAdapter
  private lateinit var filterOptionsAdapter: FilterOptionAdapter
  private lateinit var quickMainSelectionAdapter: MainQuickOptionsAdapter
  private lateinit var quickSubSelectionAdapter: SubQuickOptionsAdapter
  private lateinit var filterSelected: FilterSelectedAdapter


  private var isQuickSelected: Boolean = false


  private var layoutManager: GridLayoutManager? = null
  private var filterArrayList = ArrayList<FilterRequestModel>()

  private lateinit var recyclerViewPagingUtil: RecyclerViewPagingUtil
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    arguments?.let {
      viewModel.titleString = it.getString("title")
      viewModel.id = it.getString("id")
      viewModel.type = it.getString("type")
      viewModel.listing_type = it.getString("listing_type")
      viewModel.slug = it.getString("slug")
      viewModel.imageUrl = it.getString("imageUrl")
      viewModel.isSearch = it.getBoolean("isSearch")
    }
    val intent = requireActivity().intent
    val uri: Uri? = intent.data
    uri?.let {
      Log.e(TAG, "onCreateView:uri $uri")
      Log.e(TAG, "onCreateView:listing_type ${viewModel.listing_type}")
      Log.e(TAG, "onCreateView:slug ${viewModel.slug}")
      viewModel.setValuesFromURL(it.toString())
    }
    viewModel.appManager.analyticsManagers.productListingScreenOpen()
    if (!viewModel.isSearch) {
      appManager.filtersManager.searchQuery = ""
    }
    if (mView == null) {
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      observers()
      makeSkipNothing()
    }

    reselectionObserver()
    reselectionCategoryObserver()
    viewModel.appManager.analyticsManagers.filterApplied(viewModel.appManager.filtersManager.getFilterModel())
    return mView
  }

  // Override this method to customize back press
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Override this method to customize back press
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      handleBackPress()
    }

  }


  private fun initUI() {
    viewModel.filtersManager.instantOnly = false
    viewModel.addParentFilter()
    viewModel.isSubOption.value = false
    viewModel.isMainOptions.value = false
    binding.layoutSearch.llRange.viewModel = viewModel
    binding.layoutSearch.llRange.lifecycleOwner = this
    binding.imageUrl = viewModel.imageUrl
    binding.layoutFilters.click = this
    viewModel.title.value = viewModel.titleString
    binding.layoutSearch.click = this
    binding.layoutSearch.viewModel = viewModel
    binding.layoutSearch.lifecycleOwner = this
    binding.layoutFilters.isGridSelected = viewModel.isGirdSelected
    viewModel.isGirdSelected.set(false)
    initFilterRV()
    initProductsGridRV()
    initProductsListRV()
    initQuickSelection()
    instantOnly()
    initRVTopFilters()
    priceRangeInit()
    viewModel.rangeTo.value = getString(R.string.ten_thousand_pluse)
    viewModel.rangeFrom.value = "0"
  }

  //  fun reselectionObserver() {
//    if (viewModel.appManager.loadingState.homeReselected.equals(true)) {
//      findNavController().navigate(R.id.nav_home)
//      viewModel.appManager.loadingState.homeReselected = false
//    }
//  }
//  fun reselectionCategoryObserver() {
//    if (viewModel.appManager.loadingState.categoryReselected.equals(true)) {
//      findNavController().navigate(R.id.nav_home)
//      viewModel.appManager.loadingState.categoryReselected = false
//    }
//  }
  fun reselectionObserver() {
    viewModel.appManager.loadingState.homeReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
          findNavController().navigate(R.id.nav_home)
//          binding.rvMain.smoothScrollToPosition(0)
          viewModel.appManager.loadingState.homeReselected.value = false
        }
      }
    }
  }

  fun reselectionCategoryObserver() {
    viewModel.appManager.loadingState.categoryReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
          findNavController().navigate(R.id.nav_categories)
          viewModel.appManager.loadingState.categoryReselected.value = false
        }
      }
    }
  }

  @SuppressLint("LongLogTag")
  private fun priceRangeInit() {
    binding.layoutSearch.llRange.rangeSlider.addOnSliderTouchListener(object :
      RangeSlider.OnSliderTouchListener {
      override fun onStartTrackingTouch(slider: RangeSlider) {
        val values = slider.values
      }

      override fun onStopTrackingTouch(slider: RangeSlider) {
        changePriceRange(slider)
        resetProductListingSendNewFilters()
      }
    })
    binding.layoutSearch.llRange.rangeSlider.addOnChangeListener { rangeSlider, value, fromUser ->
      // Responds to when slider's value is changed
      changePriceRange(rangeSlider)
    }
    binding.layoutSearch.llRange.rangeSlider.setLabelFormatter { value -> //It is just an example
      when (value) {
        1000.0f -> {
          getString(R.string.ten_thousand_pluse)
        }
        1.0f -> {
          getString(R.string.zero)
        }
        else -> {
          value.toString()
        }
      }
    }
  }

  private fun initRVTopFilters() {
    filterTopTypesAdapter = FilterTopTypesAdapter(requireActivity(), this, viewModel.filtersManager)
    binding.layoutSearch.rvMainFilter.adapter = filterTopTypesAdapter

  }

  private fun instantOnly() {
    binding.layoutFilters.checkboxInstantOnly.isChecked =
      viewModel.filtersManager.instantOnly ?: false
    binding.layoutFilters.checkboxInstantOnly.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
      viewModel.filtersManager.instantOnly = b
      makeSkipNothing()
      getProductsObserver(viewModel.filtersManager.getFilterModel())
    }
  }

  private fun initFilterRV() {
    filterOptionsAdapter = FilterOptionAdapter(requireActivity(), this)
    binding.layoutFilters.recyclerViewQuickOption.adapter = filterOptionsAdapter

  }

  private fun initProductsGridRV() {
    gridProductAdapter = GridProductAdapter(
      requireActivity(),
      this,
      this,
      viewModel.appManager,
      viewModel.appManager.storageManagers.config.backOrder ?: "Pre-Order"
    )
    binding.recyclerViewProducts.adapter = gridProductAdapter
  }


  private fun initProductsListRV() {
    productListAdapter = ProductAdapter(
      requireActivity(),
      this,
      this,
      viewModel.appManager,
      viewModel.appManager.storageManagers.config.backOrder ?: "Pre-Order"
    )
    layoutManager = GridLayoutManager(requireContext(), 1)
    binding.recyclerViewProducts.layoutManager = layoutManager
    binding.recyclerViewProducts.adapter = productListAdapter
    val animator: RecyclerView.ItemAnimator? = binding.recyclerViewProducts.itemAnimator
    if (animator is SimpleItemAnimator) {
      animator.supportsChangeAnimations = false
    }
    binding.recyclerViewProducts.itemAnimator?.changeDuration = 0
    recyclerViewPagingUtil = RecyclerViewPagingUtil(
      binding.recyclerViewProducts,
      layoutManager!!, this
    )
    binding.recyclerViewProducts.addOnScrollListener(recyclerViewPagingUtil)
    binding.recyclerViewProducts.post { // Call smooth scroll
      binding.recyclerViewProducts.scrollToPosition(0)
    }

//    val callback = object :MiddleItemFinder.MiddleItemCallback {
//      override fun scrollFinished(middleElement: Int) {
//      }
//    }
//    binding.recyclerViewProducts.addOnScrollListener(
//      MiddleItemFinder(requireContext(), binding.recyclerViewProducts.layoutManager,
//         RecyclerView.SCROLL_STATE_IDLE,callback)
//    )
  }

  private fun initQuickSelection() {
    quickMainSelectionAdapter = MainQuickOptionsAdapter(requireActivity(), this)
    binding.layoutSearch.rvCategories.adapter = quickMainSelectionAdapter
    quickSubSelectionAdapter = SubQuickOptionsAdapter(requireActivity(), this)
    binding.layoutSearch.rvSubCategory.adapter = quickSubSelectionAdapter
  }

  private fun observers() {
    getProductsObserver(viewModel.filtersManager.getFilterModel())
    viewModel.title.observe(viewLifecycleOwner, Observer {
      it?.let {
        binding.layoutSearch.title = it
      }
    })
    viewModel.image.observe(viewLifecycleOwner, Observer {
      binding.imageUrl = it
      productListAdapter.setImage(it)
      gridProductAdapter.setImage(it)
    })
    viewModel.filtersManager.totalCountOfFilters.observe(viewLifecycleOwner, Observer {
      if (it > 0) {
        binding.layoutFilters.tvFilterCount.text = it.toString()
        binding.layoutFilters.tvFilterCount.visibility = View.VISIBLE
        viewModel.isFilterApplied.value = true
        filterTopTypesAdapter.setDataChanged(viewModel.filtersManager.allFilters)
        viewModel.title.value = getString(R.string.filter_applied)
      } else {
        binding.layoutFilters.tvFilterCount.text = it.toString()
        binding.layoutFilters.tvFilterCount.visibility = View.GONE
        viewModel.isFilterApplied.value = false
        if (!isQuickSelected) {
          viewModel.title.value = viewModel.titleString
        }

      }
    })

  }


  private fun getProductsObserver(appliedFilter: FilterMainRequest) {
    val gson = Gson()
    val jsonStr = gson.toJson(appliedFilter)
    Logger.d("Filters", jsonStr)
    viewModel.getProducts(appliedFilter).observe(viewLifecycleOwner, Observer {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            //event tracking call for product listing page
            it.data?.data?.let { it1 ->
              viewModel.appManager.eventlistmainManager.eventTrackingItemListViewed(
                it1
              )
            }

            hideLoading()

            it.data?.data?.products?.let { data ->
              recyclerViewPagingUtil.nextPageLoaded(data.size)
              viewModel.filtersManager.parentFilter?.let { it1 ->
                viewModel.appManager.analyticsManagers.productFetched(
                  filterModel = it1, products = data, skip = recyclerViewPagingUtil.skip
                )
              }
//              if (data.isNotEmpty()) {
//                if (productListAdapter.arrayList?.contains(data.first()) == true) {
//                  Toast.makeText(requireContext(), "productExists", Toast.LENGTH_LONG).show()
//                }
//              }
            }

            handleQuickSelectionData(it.data?.data)
            if (isQuickSelected || (viewModel.skip == 0)) {
              if (appManager.filtersManager.totalCountOfFilters.value == null || appManager.filtersManager.totalCountOfFilters.value == 0) {
                it.data?.data?.filters?.let { it1 -> appManager.filtersManager.updateAllFilters(it1) }
              }
              productListAdapter.refreshData(it.data?.data?.products)
              gridProductAdapter.refreshData(it.data?.data?.products)
              isQuickSelected = false
            } else {
              productListAdapter.setDataChanged(it.data?.data?.products)
              gridProductAdapter.setDataChanged(it.data?.data?.products)
            }
            binding.noProducts = productListAdapter.arrayList.isNullOrEmpty()
            handleImage(it.data?.data)
//            CoroutineScope(Dispatchers.IO).launch {
//              delay(200)
//              CoroutineScope(Dispatchers.Main.immediate).launch {
//                hideLoading()
//              }
//            }


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
            recyclerViewPagingUtil.isLoading = true
            if (viewModel.skip == 0) {
              showLoading()
            }
          }
        }
      }
    })
  }

  private fun openFilterSheet() {
    val addFilters = FiltersBottomSheet.newInstance()
    addFilters.show(childFragmentManager, FiltersBottomSheet.TAQ)
    addFilters.setClickCaListener(object : FiltersBottomSheet.FilterCallback {
      override fun onClickApply() {
        resetProductsListing()
        viewModel.filtersManager.makeSelectedFilter()?.let { getProductsObserver(it) }
      }

      override fun onClickClear() {

      }

    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.fragment_product_list
  }

  override fun permissionGranted(requestCode: Int) {
  }

  override fun onItemClick(
    filterModel: FilterModel?,
    position: Int
  ) {
    filterModel?.let { viewModel.setFilterSelectedItems(it) }
    filterOptionsAdapter.setItems(viewModel.selectedFilteredItems)
  }

  override fun gridSelectionValue(isSelected: Boolean) {
    if (isSelected) {
      viewModel.isGirdSelected.set(false)
      layoutManager = GridLayoutManager(requireContext(), 1)
      binding.recyclerViewProducts.layoutManager = layoutManager
      binding.recyclerViewProducts.adapter = productListAdapter
    } else {
      viewModel.isGirdSelected.set(true)
      layoutManager = GridLayoutManager(requireContext(), 2)
      layoutManager!!.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
          when (position) {
            0 -> return 2
            else -> return 1
          }
        }
      }
      binding.recyclerViewProducts.layoutManager = layoutManager
      binding.recyclerViewProducts.adapter = gridProductAdapter

    }
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

  override fun onClickBacK() {
    handleBackPress()
  }

  override fun onClickSearch() {
    try {
      findNavController().navigate(R.id.nav_search)
    } catch (e: Exception) {

    }

  }

  override fun onClickClearFilter() {
    viewModel.filtersManager.clearFilters()
    makeSkipNothing()
    getProductsObserver(viewModel.filtersManager.getFilterModel())
  }

  override fun onClickFilters() {
    openFilterSheet()
  }

  override fun onClickQuickSelection(section: Section?, position: Int?) {
    position?.let { quickMainSelectionAdapter.setLoadingItem(it) }
    viewModel.title.value = section?.name
    viewModel.isSubOption.value = true
    viewModel.isSubClicked = false
    if (section != null) {
      viewModel.parentSelectedQuickSection = section
    }
    quickOptionSelected(section = section, isParent = true)
    binding.layoutSearch.rvCategories.post { // Call smooth scroll
      binding.layoutSearch.rvCategories.scrollToPosition(0)
    }
//    if (position != null) {
//      quickMainSelectionAdapter.setItemSelected(position)
//    }

  }

  override fun onClickSubQuickSelection(section: Section?, position: Int?) {
    position?.let { quickSubSelectionAdapter.setLoadingItem(it) }
    viewModel.isSubClicked = true
    viewModel.title.value = section?.name
    quickOptionSelected(section = section)
    binding.layoutSearch.rvSubCategory.post { // Call smooth scroll
      binding.layoutSearch.rvSubCategory.scrollToPosition(0)
    }
//    if (position != null) {
//      quickSubSelectionAdapter.setItemSelected(position)
//    }

  }

  override fun onClickHomeSubItem(
    title: String?,
    id: String?,
    type: String?,
    sectionData: SectionData?
  ) {

  }


  private fun handleBackPress() {
    findNavController().navigateUp()
  }

  private fun resetProductsListing() {
    makeSkipNothing()
    gridProductAdapter.removeItems()
    productListAdapter.removeItems()
  }

  private fun makeSkipNothing() {
    viewModel.skip = 0
    recyclerViewPagingUtil.skip = 0
  }

  private fun resetProductListingSendNewFilters() {
    resetProductsListing()
    viewModel.filtersManager.makeSelectedFilter()?.let { getProductsObserver(it) }
  }

  private fun handleQuickSelectionData(main: ProductListingMainModel?) {
    val selectedParent = ""
    if (viewModel.skip == 0) {
      if (main?.parentCategories.isNullOrEmpty()) {
        quickMainSelectionAdapter.setDataChanged(main?.siblingsCategories, selectedParent)
        quickSubSelectionAdapter.setDataChanged(main?.childCategories)
        viewModel.isMainOptions.value = !main?.siblingsCategories.isNullOrEmpty()
        viewModel.isSubOption.value = !main?.childCategories.isNullOrEmpty()
      } else if (main?.childCategories.isNullOrEmpty()) {
        quickMainSelectionAdapter.setDataChanged(main?.parentCategories, selectedParent)
        quickSubSelectionAdapter.setDataChanged(main?.siblingsCategories)
        viewModel.isMainOptions.value = !main?.parentCategories.isNullOrEmpty()
        viewModel.isSubOption.value = !main?.siblingsCategories.isNullOrEmpty()
      }
      val parentSelectedSection =
        quickMainSelectionAdapter.arrayList?.firstOrNull { section -> section.isSelected == true }
      if (parentSelectedSection != null) {
        viewModel.parentSelectedQuickSection = parentSelectedSection
      }
    }
  }

  private fun handleImage(main: ProductListingMainModel?) {
    viewModel.image.value = null
    main?.selectedParentCategory?.images?.banner?.let {
      viewModel.image.value = it
    }
    main?.selectedCategory?.images?.banner?.let {
      viewModel.image.value = it
    }
  }

  private fun changePriceRange(slider: RangeSlider) {
    val values = slider.values
    Log.d("onStopTrackingTouchFrom", values[0].toString())
    Log.d("onStopTrackingTouch T0", values[1].toString())
    if (values[0] == 0f) {
      viewModel.filtersManager.fromPrice = ""
      viewModel.rangeFrom.value = getString(R.string.zero)
    } else {
      viewModel.rangeFrom.value = values[0].roundToInt().toString()
      viewModel.filtersManager.fromPrice = values[0].roundToInt().toString()
    }
    if (values[1] == 1000.0f) {
      viewModel.filtersManager.toPrice = ""
      viewModel.rangeTo.value = getString(R.string.ten_thousand_pluse)
    } else {
      viewModel.filtersManager.toPrice = values[1].roundToInt().toString()
      viewModel.rangeTo.value = values[1].roundToInt().toString()
    }
  }

  private fun quickOptionSelected(section: Section?, isParent: Boolean = false) {
    isQuickSelected = true
    Utils.vibrate(requireActivity())
    viewModel.filtersManager.parentFilter?.let { filterArrayList.add(it) }
    if (!isParent) {
      section?.id?.let {
        if (viewModel.filtersManager.checkIfFilterContains("category", it)) {
          viewModel.filtersManager.clearFilters()
          viewModel.id = viewModel.parentSelectedQuickSection.id
          viewModel.type = "category"

        } else {
          viewModel.filtersManager.clearFilters()
          viewModel.id = it
          viewModel.type = "category"
        }
      }
    } else {
      viewModel.filtersManager.clearFilters()
      section?.id?.let {
        viewModel.id = it
        viewModel.type = "category"
//      viewModel.filtersManager.addFirstFilter(it, "category")
      }
    }

    viewModel.addParentFilter()
    makeSkipNothing()
    getProductsObserver(viewModel.filtersManager.getFilterModel())
  }

  override fun onNextPage(skip: Int, take: Int) {
    viewModel.skip = skip
    viewModel.take = take
    getProductsObserver(viewModel.filtersManager.appliedFilterMain)
  }

  override fun onClickFilterType(filterType: FilterTypeModel, position: Int) {
    if (filterTopTypesAdapter.rowSelected == position) {
      binding.layoutSearch.rvSubFilter.visibility = View.GONE
    } else {
      binding.layoutSearch.rvSubFilter.visibility = View.VISIBLE

    }
    viewModel.isRangeSelected.value = filterType.inputType == "range"
    filterTopTypesAdapter.selectedWithCompleteRefresh(position)
    filterSelected = FilterSelectedAdapter(requireActivity(), position, filterType.type, this)
    binding.layoutSearch.rvSubFilter.adapter = filterSelected
    filterSelected.setDataChanged(filterType.data, position)
  }

  override fun onClickCheckBox(
    filter: FilterModel,
    type: String,
    position: Int,
    parentPosition: Int
  ) {
    viewModel.filtersManager.clearSingleFilter(parentPosition, position)
    filterSelected.setDataChanged(
      viewModel.filtersManager.allFilters?.get(parentPosition)?.data,
      parentPosition
    )
    filterTopTypesAdapter.setDataChanged(viewModel.filtersManager.allFilters)
    resetProductListingSendNewFilters()

  }


}
