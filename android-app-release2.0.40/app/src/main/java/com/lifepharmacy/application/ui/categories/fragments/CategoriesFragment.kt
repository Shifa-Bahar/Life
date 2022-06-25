package com.lifepharmacy.application.ui.categories.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseFragment
import com.lifepharmacy.application.databinding.FragmentCategoriesBinding
import com.lifepharmacy.application.interfaces.ClickSearchBarProduct
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.managers.analytics.categoryScreenOpen
import com.lifepharmacy.application.managers.analytics.productClicked
import com.lifepharmacy.application.model.category.CategoryMainModel
import com.lifepharmacy.application.model.category.RootCategory
import com.lifepharmacy.application.model.category.Section
import com.lifepharmacy.application.model.home.SectionData
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.categories.adapters.*
import com.lifepharmacy.application.ui.categories.viewmodel.CategoryViewModel
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeSubItem
import com.lifepharmacy.application.ui.productList.ProductListFragment
import com.lifepharmacy.application.ui.products.fragment.ProductFragment
import com.lifepharmacy.application.utils.universal.Logger
import com.lifepharmacy.application.utils.universal.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class CategoriesFragment : BaseFragment<FragmentCategoriesBinding>(),
  ClickCategoriesFragment, ClickSearchBarProduct,
  ClickSubSubCategory, ClickSubCategory, ClickCategory, ClickHomeSubItem,
  ClickSectionProductsCategory {
  private val viewModel: CategoryViewModel by activityViewModels()
  private lateinit var childAdapter: CategoryChildsAdapter
  private lateinit var mainCategories: CategoryAdapter
  lateinit var sectionAdapter: SectionCategoryAdapter
  private lateinit var categoryMainModel: CategoryMainModel

  //  private lateinit var mainCategoryDetailFragment :CategoryMainDetailAdapter
  var rootSelected: RootCategory? = null
  var firstTimeLoading = false
  var slug: String? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewModel.appManager.analyticsManagers.categoryScreenOpen()
    arguments?.let {
      slug = it.getString("slug")
    }
    if (mView == null) {
      Logger.d("FirstTime", firstTimeLoading.toString())
      mView = super.onCreateView(inflater, container, savedInstanceState)
      initUI()
      observers()
      firstTimeLoading = true
    }
    reselectionCategoryObserver()


    return mView
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Override this method to customize back press
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      findNavController().navigateUp()

//      Utils.exitApp(requireActivity())
    }

  }

  private fun initUI() {

    binding.click = this
    binding.tollBar.click = this

    initCategoryRV()
    initSectionCategoriesRV()
    initChildCategoriesRV()
  }
  fun reselectionCategoryObserver() {
    viewModel.appManager.loadingState.categoryReselected.observe(viewLifecycleOwner) {
      it?.let {
        if (it) {
          findNavController().navigate(R.id.nav_categories)
//          binding.rvMain.smoothScrollToPosition(0)
          viewModel.appManager.loadingState.categoryReselected.value = false
        }
      }
    }
  }
  private fun initChildCategoriesRV() {
    childAdapter = CategoryChildsAdapter(requireActivity(), this, this)
    binding.rvChilds.adapter = childAdapter
    val animator: RecyclerView.ItemAnimator? = binding.rvChilds.itemAnimator
    if (animator is SimpleItemAnimator) {
      animator.supportsChangeAnimations = false
    }
    binding.rvChilds.itemAnimator?.changeDuration = 0
  }

//  private fun iniMainCategoryDetailRV(){
//    mainCategoryDetailFragment = CategoryMainDetailAdapter(requireActivity(), this, this,this,this)
//    binding.rvChilds.adapter = mainCategoryDetailFragment
//    val animator: RecyclerView.ItemAnimator? = binding.rvChilds.itemAnimator
//    if (animator is SimpleItemAnimator) {
//      animator.supportsChangeAnimations = false
//    }
//    binding.rvChilds.itemAnimator?.changeDuration = 0
//  }

  private fun initSectionCategoriesRV() {
    sectionAdapter = SectionCategoryAdapter(requireActivity(), this, this)
    binding.rvSections.adapter = sectionAdapter
    val animator: RecyclerView.ItemAnimator? = binding.rvSections.itemAnimator
    if (animator is SimpleItemAnimator) {
      animator.supportsChangeAnimations = false
    }
    binding.rvSections.itemAnimator?.changeDuration = 0
  }

  private fun initCategoryRV() {
    mainCategories = CategoryAdapter(requireActivity(), this)
    binding.rvCategories.adapter = mainCategories
    val animator: RecyclerView.ItemAnimator? = binding.rvCategories.itemAnimator
    if (animator is SimpleItemAnimator) {
      animator.supportsChangeAnimations = false
    }
    binding.rvCategories.itemAnimator?.changeDuration = 0
  }

  private fun updateSectionAndChild(mainModel: CategoryMainModel) {
    categoryMainModel = mainModel
    binding.image = mainModel.images
    val childArray = mainModel.children
    if (childArray != null) {
      childAdapter.setDataChanged(childArray)
    }
    if (!mainModel.sections.isNullOrEmpty()) {
      sectionAdapter.setDataChanged(mainModel.sections)
      binding.rvSections.visibility = View.VISIBLE
    } else {
      binding.rvSections.visibility = View.GONE
    }


  }


  private fun observers() {
    viewModel.appManager.storageManagers.getRootCategories().observe(viewLifecycleOwner, Observer {
      it?.rootCategory?.let { categoryList ->
        if (firstTimeLoading) {
          mainCategories.setDataChanged(categoryList)
          if (categoryList.isNotEmpty()) {
            if (slug != null) {
              var getFiltersCategory =
                categoryList.firstOrNull { rootCategory -> rootCategory.slug == slug }
              getFiltersCategory?.let { rootSelected ->
                val position = categoryList.indexOf(rootSelected)
                mainCategories.setItemSelected(position)
                this.rootSelected = rootSelected
                mainCategories.setItemSelected(position)
                subCategoriesObserver(rootSelected)
              }
            } else {
              rootSelected = categoryList[0]
              subCategoriesObserver(categoryList[0])
            }

          }
        }
      }
//      when (it.status) {
//        Result.Status.SUCCESS -> {
//          it?.data?.let { list ->
//            if (firstTimeLoading){
//              mainCategories.setDataChanged(list)
//              if (list.isNotEmpty()) {
//                rootSelected = list[0]
//                subCategoriesObserver(list[0])
//              }
//            }
//          }
//          hideLoading()
//        }
//        Result.Status.ERROR -> {
//          hideLoading()
//          it.message?.let { it1 ->
//            AlertManager.showErrorMessage(
//              requireActivity(),
//              it1
//            )
//          }
//        }
//        Result.Status.LOADING -> {
//
//          showLoading()
//        }
//      }
    })

  }

  private fun subCategoriesObserver(id: RootCategory?) {
    binding.item = id
    id?.id?.let { id ->
      viewModel.getSubCategories(id).observe(viewLifecycleOwner, Observer {
        when (it.status) {
          Result.Status.SUCCESS -> {
            hideLoading()
            it.data?.data?.let { it1 ->
              updateSectionAndChild(it1)
            }
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
      })
    }

  }


  override fun getLayoutRes(): Int {
    return R.layout.fragment_categories
  }

  override fun permissionGranted(requestCode: Int) {

  }

  override fun onClickBacK() {
    findNavController().navigateUp()
  }

  override fun onClickSubCategory(position: Int) {
    childAdapter.setItemSelected(position)
  }


  override fun onClickCategory(position: Int, rootCategory: RootCategory) {
    rootSelected = rootCategory
    mainCategories.setItemSelected(position)
    subCategoriesObserver(rootCategory)
  }

  override fun onClickCategory(item: Section) {

  }

  override fun onClickSectionProductsCategory(item: SectionData) {

    findNavController().navigate(
      R.id.nav_product,
      ProductFragment.getBundle(productID = item.id, 0)
    )
  }

  override fun onClickHomeSubItem(
    title: String?,
    id: String?,
    type: String?,
    sectionData: SectionData?
  ) {
    if (sectionData != null) {
      if (sectionData.maxPriceLimit != "0") {
        viewModel.appManager.filtersManager.fromPrice = sectionData.lowerPriceLimit
        viewModel.appManager.filtersManager.toPrice = sectionData.maxPriceLimit
      }
      viewModel.appManager.filtersManager.searchQuery = sectionData.id
    }
    findNavController().navigate(
      R.id.nav_product_listing,
      ProductListFragment.getProductListingBundle(title, id, type)
    )
  }

  override fun onClickSearch() {
    try {
      findNavController().navigate(R.id.nav_search)
    } catch (e: Exception) {

    }
  }

  override fun onClickClearFilter() {

  }

  override fun onClickShopAll() {
    rootSelected?.let {
//      it.id?.let { it1 -> viewModel.appManager.filtersManager.addFirstFilter(it1, "category") }
      findNavController().navigate(
        R.id.nav_product_listing,
        ProductListFragment.getProductListingBundle(
          it.name,
          it.id,
          "category",
          imageUrl = binding.image?.banner
        )
      )
    }

  }
}



