package com.lifepharmacy.application.ui.home.adapters

import android.app.Activity
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.*
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.blog.BlogModel
import com.lifepharmacy.application.model.blog.Title
import com.lifepharmacy.application.model.home.HomeProductFeedResponse
import com.lifepharmacy.application.model.home.HomeResponseItem
import com.lifepharmacy.application.model.home.SectionData
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.ui.dashboard.adapter.*
import com.lifepharmacy.application.ui.dashboard.fragments.ClickHomeFragment
import com.lifepharmacy.application.ui.home.fragments.ClickHomeRecyclerFragment
import com.lifepharmacy.application.utils.universal.Extensions.doubleDigitDouble
import com.lifepharmacy.application.utils.universal.Extensions.intToNullSafeDoubleByDefault1
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeDouble
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeInt
import com.lifepharmacy.application.utils.universal.Logger
import com.lifepharmacy.application.utils.universal.SnapToBlock
import com.lifepharmacy.application.utils.universal.Utils
import org.imaginativeworld.whynotimagecarousel.CarouselItem
import org.imaginativeworld.whynotimagecarousel.OnItemClickListener
import java.lang.Exception
import kotlin.math.roundToInt


class LandingAdapter(
  context: Activity,
  private val appManager: AppManager,
  private val onHomeSubItemTapped: ClickHomeSubItem,
  private val onHomeProductTapped: ClickHomeProduct,
  private val onClickLayoutProduct: ClickLayoutHorizontalProducts,
  private val onClickHomeOffer: ClickHomeOffer,
  private val onClickOffers: ClickHomeOffers
) : RecyclerView.Adapter<LandingAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<HomeResponseItem>? = ArrayList()
  var activity: Activity = context as Activity
  var blogsArraylist: ArrayList<BlogModel>? = ArrayList()
  var homeTilesList: ArrayList<com.lifepharmacy.application.model.config.Title>? = ArrayList()
  var viewType = 2

  //  var homeProductAdapter = HomeProductAdapter(activity, onHomeProductTapped, appManager)
  override fun getItemViewType(position: Int): Int {
    var returnInt = 1
    var arrayListPosition = position
    if (!arrayList.isNullOrEmpty()) {
      if (arrayListPosition < arrayList?.size ?: 0) {
        arrayList?.let {
          if (it[arrayListPosition].sectionType == "category") {
            returnInt = 1
          }
          if (it[arrayListPosition].sectionType == "main_slider") {
            returnInt = 2
          }
          if (it[arrayListPosition].sectionType == "deals_slider") {
            returnInt = 3
          }
          if (it[arrayListPosition].sectionType == "dual_row_products") {
            returnInt = 4
          }
          if (it[arrayListPosition].sectionType == "trending_offers") {
            returnInt = 5
          }
          if (it[arrayListPosition].sectionType == "banner_ad") {
            returnInt = 6
          }
          if (it[arrayListPosition].sectionType == "icon_grid_4x4") {
            returnInt = 7
          }
          if (it[arrayListPosition].sectionType == "offer_grid_4x2") {
            returnInt = 7
          }
          if (it[arrayListPosition].sectionType == "deal_small_slider") {
            returnInt = 8
          }
          if (it[arrayListPosition].sectionType == "collection_slider") {
            returnInt = 5
          }
          if (it[arrayListPosition].sectionType == "collection_slider_brands") {
            returnInt = 5
          }
          if (it[arrayListPosition].sectionType == "parent_child_grid") {
            returnInt = 10
          }
          if (it[arrayListPosition].sectionType == "featured_offers_3x3") {
            returnInt = 11
          }
          if (it[arrayListPosition].sectionType == "collection_slider_brands") {
            returnInt = 13
          }
          if (it[arrayListPosition].sectionType == "static_slider") {
            returnInt = 14
          }
          if (it[arrayListPosition].sectionType == "9x9_slider_grid") {
            returnInt = 15
          }

          if (it[arrayListPosition].sectionType == "dynamic_grid") {
            returnInt = 16
          }
          if (it[arrayListPosition].sectionType == "single_row_products") {
            returnInt = 17
          }
        }
      }

    }

    return returnInt
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

    this.viewType = viewType
    if (viewType == 1) {
      val binding: LayoutHorizontalCategoriesBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_horizontal_categories,
        parent, false
      )
      return ItemViewHolder(binding.root, viewType)
    } else if (viewType == 2) {
      val binding: LayoutHorizontalSliderBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_horizontal_slider,
        parent, false
      )
      return ItemViewHolder(binding.root, viewType)
    } else if (viewType == 3) {
      val binding: LayoutHorizontalDealsBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_horizontal_deals,
        parent, false
      )
      return ItemViewHolder(binding.root, viewType)
    } else if (viewType == 4) {
      val binding: LayoutHorizontalProductsBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_horizontal_products,
        parent, false
      )
      return ItemViewHolder(binding.root, viewType)
    } else if (viewType == 5 || viewType == 13) {
      val binding: LayoutHorizontalTrendsBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_horizontal_trends,
        parent, false
      )
      return ItemViewHolder(binding.root, viewType)
    } else if (viewType == 6) {
      val binding: LayoutBannerAddBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_banner_add,
        parent, false
      )
      return ItemViewHolder(binding.root, viewType)
    } else if (viewType == 7) {
      val binding: LayoutViewMoreBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_view_more,
        parent, false
      )
      return ItemViewHolder(binding.root, viewType)
    } else if (viewType == 8) {
      val binding: LayoutHorizontalDeallSmallSliderBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_horizontal_deall_small_slider,
        parent, false
      )
      return ItemViewHolder(binding.root, viewType)
    } else if (viewType == 10) {
      val binding: LayoutHorizontalCatGridBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_horizontal_cat_grid,
        parent, false
      )
      return ItemViewHolder(binding.root, viewType)
    } else if (viewType == 11) {
      val binding: LayoutVerticalPromotionsBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_vertical_promotions,
        parent, false
      )
      return ItemViewHolder(binding.root, viewType)


    } else if (viewType == 14) {
      val binding: LayoutHorizontalStaticSliderBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_horizontal_static_slider,
        parent, false
      )
      return ItemViewHolder(binding.root, viewType)


    } else if (viewType == 15) {
      val binding: LayoutHorizontalCategoriesTilesSliderBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_horizontal_categories_tiles_slider,
        parent, false
      )
      return ItemViewHolder(binding.root, viewType)


    } else if (viewType == 16) {
      val binding: LayoutDynamiceGridRecyclerViewBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_dynamice_grid_recycler_view,
        parent, false
      )
      return ItemViewHolder(binding.root, viewType)


    } else if (viewType == 17) {
      val binding: LayoutHorizontalSingleProductsBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_horizontal_single_products,
        parent, false
      )
      return ItemViewHolder(binding.root, viewType)
    }
//      else if (viewType == 14) {
//        val binding: LayoutHorizontalOffersBinding = DataBindingUtil.inflate(
//          LayoutInflater.from(activity),
//          R.layout.layout_horizontal_offers,
//          parent, false
//        )
//        return ItemViewHolder(binding.root, viewType)
//
//
//      }else {
    val binding: LayoutHorizontalCategoriesBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.layout_horizontal_categories,
      parent, false
    )
    return ItemViewHolder(binding.root, viewType)

  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size + 3 else 3
  }

  class ItemViewHolder internal constructor(itemView: View, int: Int) :
    RecyclerView.ViewHolder(itemView) {
    var bindHomeTagsBinding: LayoutHomeTagsBinding? = null
    var bindingBackBlueBinding: LayoutSearchBarHomeBackBlueBinding? = null
    var bindingCategories: LayoutHorizontalCategoriesBinding? = null
    var bindingMainSlider: LayoutHorizontalSliderBinding? = null
    var bindingDeals: LayoutHorizontalDealsBinding? = null
    var bindingProducts: LayoutHorizontalProductsBinding? = null
    var bindingProductsSingle: LayoutHorizontalSingleProductsBinding? = null
    var bindingTrendings: LayoutHorizontalTrendsBinding? = null
    var bindingBanner: LayoutBannerAddBinding? = null
    var bindingViewMode: LayoutViewMoreBinding? = null
    var bindingDealsSmallSlider: LayoutHorizontalDeallSmallSliderBinding? = null
    var bindingHorizontalGrid: LayoutHorizontalCatGridBinding? = null
    var bindingVerticalPromotions: LayoutVerticalPromotionsBinding? = null
    var bindingBlogs: LayoutHorizontalBlogBinding? = null
    var bindStaticSlider: LayoutHorizontalStaticSliderBinding? = null
    var bindTilesCategory: LayoutHorizontalCategoriesTilesSliderBinding? = null
    var bindDynamiceGrid: LayoutDynamiceGridRecyclerViewBinding? = null
//    var bindingOffers: LayoutHorizontalOffersBinding? = null

    init {
      if (int == 1) {
        bindingCategories = DataBindingUtil.bind(itemView)
      } else if (int == 2) {
        bindingMainSlider = DataBindingUtil.bind(itemView)
      } else if (int == 3) {
        bindingDeals = DataBindingUtil.bind(itemView)
      } else if (int == 4) {
        bindingProducts = DataBindingUtil.bind(itemView)
      } else if (int == 5 || int == 13) {
        bindingTrendings = DataBindingUtil.bind(itemView)
      } else if (int == 6) {
        bindingBanner = DataBindingUtil.bind(itemView)
      } else if (int == 7) {
        bindingViewMode = DataBindingUtil.bind(itemView)
      } else if (int == 8) {
        bindingDealsSmallSlider = DataBindingUtil.bind(itemView)
      } else if (int == 10) {
        bindingHorizontalGrid = DataBindingUtil.bind(itemView)
      } else if (int == 11) {
        bindingVerticalPromotions = DataBindingUtil.bind(itemView)
      } else if (int == 14) {
        bindStaticSlider = DataBindingUtil.bind(itemView)
      } else if (int == 15) {
        bindTilesCategory = DataBindingUtil.bind(itemView)
      } else if (int == 16) {
        bindDynamiceGrid = DataBindingUtil.bind(itemView)
      } else if (int == 17) {
        bindingProductsSingle = DataBindingUtil.bind(itemView)
      }
//      else if (int == 14) {
//        bindingOffers = DataBindingUtil.bind(itemView)
//      }
      else {
        bindingCategories = DataBindingUtil.bind(itemView)
      }


    }


  }

  fun setDataChanged(order: ArrayList<HomeResponseItem>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }

  fun updateArrayListHomeProductssingle(homeProductFeedResponse: HomeProductFeedResponse) {
    val filterItem = arrayList?.firstOrNull { internalFilter ->
      internalFilter.sectionType == "single_row_products" && internalFilter.orderId == homeProductFeedResponse.orderId
    }
    if (filterItem != null) {
      filterItem.sectionDataObject?.feedData = homeProductFeedResponse.feedData
      val indexOfProduct: Int? = arrayList?.indexOf(filterItem)
      if (indexOfProduct != null) {
        updatePositionInAdapter(indexOfProduct)
      }
    }
  }

  fun updateArrayListHomeProducts(homeProductFeedResponse: HomeProductFeedResponse) {
    val filterItem = arrayList?.firstOrNull { internalFilter ->
      internalFilter.sectionType == "dual_row_products" && internalFilter.orderId == homeProductFeedResponse.orderId
    }
    if (filterItem != null) {
      filterItem.sectionDataObject?.feedData = homeProductFeedResponse.feedData
      val indexOfProduct: Int? = arrayList?.indexOf(filterItem)
      if (indexOfProduct != null) {
        updatePositionInAdapter(indexOfProduct)
      }
    }
  }

  private fun updatePositionInAdapter(arrayListPosition: Int) {
    notifyItemChanged(arrayListPosition)
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    var arrayListPosition = position
    if (!arrayList.isNullOrEmpty()) {
      if (arrayListPosition < arrayList?.size ?: 0) {
        val item = arrayList?.get(arrayListPosition)
        if (item != null && !item.backgroundColor.isNullOrEmpty()) {
          holder.itemView.setBackgroundColor(Color.parseColor(item.backgroundColor!!))
        }
        arrayList?.let {
          if (it[arrayListPosition].sectionType == "category") {
            holder.bindingCategories?.let { bindOptionsCategories(it, arrayListPosition) }
          }
          if (it[arrayListPosition].sectionType == "main_slider") {
            holder.bindingMainSlider?.let { bindSlider(it, arrayListPosition) }
          }
          if (it[arrayListPosition].sectionType == "deals_slider") {
            holder.bindingDeals?.let { bindOffers(it, arrayListPosition) }
          }
          if (it[arrayListPosition].sectionType == "dual_row_products") {
            holder.bindingProducts?.let { bindProducts(it, arrayListPosition) }
          }
          if (it[arrayListPosition].sectionType == "trending_offers") {
            holder.bindingTrendings?.let { bindTrending(it, arrayListPosition) }
          }
          if (it[arrayListPosition].sectionType == "banner_ad") {
            holder.bindingBanner?.let { bindingBanner(it, arrayListPosition) }
          }
          if (it[arrayListPosition].sectionType == "icon_grid_4x4") {
            holder.bindingViewMode?.let { bindViwMore(it, arrayListPosition) }
          }
          if (it[arrayListPosition].sectionType == "offer_grid_4x2") {
            holder.bindingViewMode?.let { bindViwMore(it, arrayListPosition) }
          }
          if (it[arrayListPosition].sectionType == "deal_small_slider") {
            holder.bindingDealsSmallSlider?.let { bindSmallDeals(it, arrayListPosition) }
          }
          if (it[arrayListPosition].sectionType == "collection_slider") {
            holder.bindingTrendings?.let { bindTrending(it, arrayListPosition) }
          }
          if (it[arrayListPosition].sectionType == "collection_slider_brands") {
            holder.bindingTrendings?.let { bindTrending(it, arrayListPosition) }
          }
          if (it[arrayListPosition].sectionType == "parent_child_grid") {
            holder.bindingHorizontalGrid?.let { bindPromotion(it, arrayListPosition) }
          }
          if (it[arrayListPosition].sectionType == "featured_offers_3x3") {
            holder.bindingVerticalPromotions?.let {
              bindVerticalPromotion(
                it,
                arrayListPosition
              )
            }
          }
          if (it[arrayListPosition].sectionType == "static_slider") {
            holder.bindStaticSlider?.let { bindStaticSlider(it, arrayListPosition) }
          }
          if (it[arrayListPosition].sectionType == "9x9_slider_grid") {
            holder.bindTilesCategory?.let { bindTilesSlider(it, arrayListPosition) }
          }
          if (it[arrayListPosition].sectionType == "dynamic_grid") {
            holder.bindDynamiceGrid?.let {
              bindDynamicGrid(it, arrayListPosition)
            }
          }
          if (it[arrayListPosition].sectionType == "single_row_products") {
            holder.bindingProductsSingle?.let { bindProductsSingle(it, arrayListPosition) }
          }
//            if (it[arrayListPosition].sectionType == "offer_slider") {
//              holder.bindingOffers?.let {
//                bindOffer(
//                  it,
//                  arrayListPosition
//                )
//              }
//            }
//      if (it[position].sectionType == "blog_posts") {
//        returnInt = 12
//      }
          if (it[arrayListPosition].sectionType == "collection_slider_brands") {
            holder.bindingTrendings?.let { bindTrendsWithBrands(it, arrayListPosition) }
          }

        }
      }


    }

  }


  private fun bindOptionsCategories(binding: LayoutHorizontalCategoriesBinding, position: Int) {
    val homeCategoriesAdapter = HomeOptionAdapter(activity, onHomeSubItemTapped)
    binding.recyclerViewOptions.adapter = homeCategoriesAdapter
    homeCategoriesAdapter.setDataChanged(arrayList?.get(position)?.sectionDataList)
  }

  private fun bindSlider(binding: LayoutHorizontalSliderBinding, mainIndex: Int) {
    val list = arrayListOf<CarouselItem>()
    arrayList?.get(mainIndex)?.sectionDataList?.let {
      for (item in it) {
        list.add(
          CarouselItem(
            imageUrl = item.imageUrl

          )
        )
      }
    }
    binding.carousel.addData(list)
    binding.customIndicator.let {
      binding.carousel.setIndicator(
        it
      )
    }

    binding.carousel.onItemClickListener = object : OnItemClickListener {
      override fun onClick(position: Int, carouselItem: CarouselItem) {
        onHomeSubItemTapped.onClickHomeSubItem(
          arrayList?.get(mainIndex)?.sectionDataList?.get(position)?.title,
          arrayList?.get(mainIndex)?.sectionDataList?.get(position)?.id,
          arrayList?.get(mainIndex)?.sectionDataList?.get(position)?.type,
          arrayList?.get(mainIndex)?.sectionDataList?.get(position)
        )
      }

      override fun onLongClick(position: Int, dataObject: CarouselItem) {
      }

    }
  }

  private fun bindOffers(binding: LayoutHorizontalDealsBinding, position: Int) {
    val offerAdapter = OfferAdapterTags(activity, onHomeSubItemTapped)
    binding.recyclerViewOffersDeals.adapter = offerAdapter
    offerAdapter.setDataChanged(arrayList?.get(position)?.sectionDataList)
  }

  private fun bindProducts(binding: LayoutHorizontalProductsBinding, position: Int) {
    binding.title = arrayList?.get(position)?.sectionTitle
    binding.item = arrayList?.get(position)
    binding.id = arrayList?.get(position)?.sectionDataObject?.viewAll?.id
    binding.type = arrayList?.get(position)?.sectionDataObject?.viewAll?.type
    binding.click = onClickLayoutProduct
    val homeProductAdapter = HomeProductAdapter(
      activity,
      onHomeProductTapped,
      appManager,
      appManager.storageManagers.config.backOrder ?: "Pre-Order"
    )
    binding.recyclerViewProducts.adapter = homeProductAdapter
//    binding.recyclerViewProducts.onFlingListener = null
//    val snapHelper = LinearSnapHelper() // Or PagerSnapHelper
//    binding.recyclerViewProducts.onFlingListener = null;
//    snapHelper.attachToRecyclerView(binding.recyclerViewProducts)
    homeProductAdapter.setDataChanged(arrayList?.get(position)?.sectionDataObject?.feedData)
    if (arrayList?.get(position)?.sectionDataObject?.feedData.isNullOrEmpty()) {
      binding.progressBar.visibility = View.VISIBLE
      arrayList?.get(position)?.let { onClickLayoutProduct.onLoadSectionItems(it) }
    } else {
      binding.progressBar.visibility = View.GONE
    }
  }

  private fun bindProductsSingle(binding: LayoutHorizontalSingleProductsBinding, position: Int) {
    binding.title = arrayList?.get(position)?.sectionTitle
    binding.item = arrayList?.get(position)
    binding.id = arrayList?.get(position)?.sectionDataObject?.viewAll?.id
    binding.type = arrayList?.get(position)?.sectionDataObject?.viewAll?.type
    binding.click = onClickLayoutProduct
    val homeProductSingleAdapter = HomeProductSingleAdapter(
      activity,
      onHomeProductTapped,
      appManager,
      appManager.storageManagers.config.backOrder ?: "Pre-Order"
    )
    binding.recyclerViewProducts.adapter = homeProductSingleAdapter
//    binding.recyclerViewProducts.onFlingListener = null
//    val snapHelper = LinearSnapHelper() // Or PagerSnapHelper
//    binding.recyclerViewProducts.onFlingListener = null;
//    snapHelper.attachToRecyclerView(binding.recyclerViewProducts)
    homeProductSingleAdapter.setDataChanged(arrayList?.get(position)?.sectionDataObject?.feedData)
    if (arrayList?.get(position)?.sectionDataObject?.feedData.isNullOrEmpty()) {
      binding.progressBar.visibility = View.VISIBLE
      arrayList?.get(position)?.let { onClickLayoutProduct.onLoadSectionItems(it) }
    } else {
      binding.progressBar.visibility = View.GONE
    }
  }

  private fun bindTrending(binding: LayoutHorizontalTrendsBinding, position: Int) {
    binding.item = arrayList?.get(position)
    binding.title = arrayList?.get(position)?.sectionTitle
    var trendings = PromotionsAdapter(activity, onHomeSubItemTapped)
    binding.recyclerViewTrends.adapter = trendings
    trendings.setDataChanged(arrayList?.get(position)?.sectionDataList)
  }

  private fun bindingBanner(binding: LayoutBannerAddBinding, position: Int) {
    val item = arrayList?.get(position)?.sectionDataObject
    val displayMetrics = DisplayMetrics()
    activity.windowManager?.getDefaultDisplay()?.getMetrics(displayMetrics)
    val screenWidth = displayMetrics.widthPixels
    try {
      val imageHeight = item?.imageHeight?.stringToNullSafeDouble() ?: 1.0
      val imageWidth = item?.imageWidth?.stringToNullSafeDouble() ?: 1.0
      val radio = (imageHeight / imageWidth).doubleDigitDouble()
      val heightWithAspectRatio = radio * (screenWidth)
//        val imageHeight = item?.imageHeight?.toDouble()?:1.0
//        val imageWidth = item?.imageWidth?.toDouble()?:1.0
//        val radio = (imageHeight/imageWidth).doubleDigitDouble()
//        val heightWithAspectRatio = radio*(holder.bindingBanner?.imageBanner?.measuredWidth!!)

      val layoutParams = binding.imageBanner?.layoutParams as ConstraintLayout.LayoutParams
      layoutParams.dimensionRatio = "$imageWidth:$imageHeight"
      binding.imageBanner?.layoutParams = layoutParams
      Logger.d("aspectHeight", heightWithAspectRatio.roundToInt().toString())
      Logger.d("aspectHeight2", radio.toString())
      Logger.d("aspectHeight3", screenWidth.toString())
      Logger.d("aspectHeight4", binding.imageBanner?.measuredWidth.toString())
//        holder.bindingBanner?.clMain?.layoutParams?.width = screenWidth
//        holder.bindingBanner?.clMain?.layoutParams?.height = heightWithAspectRatio.roundToInt()
//        holder.bindingBanner?.imageBanner?.layoutParams?.width = holder.bindingBanner?.imageBanner?.layoutParams?.width!!
//        holder.bindingBanner?.imageBanner?.layoutParams?.height = heightWithAspectRatio.roundToInt()
      binding.imageBanner?.scaleType = ImageView.ScaleType.FIT_CENTER
    } catch (e: Exception) {
      e.printStackTrace()
    }

    binding.item = item
    binding.click = onHomeSubItemTapped
  }

  private fun bindViwMore(binding: LayoutViewMoreBinding, position: Int) {
    binding.isShow = false
    binding.item = arrayList?.get(position)
    binding.title = arrayList?.get(position)?.sectionTitle
    val trendingOfferAdapter = ViewMoreAdapter(activity, onHomeSubItemTapped)
    val trendingOfferAdapterHidden =
      ViewMoreAdapter(activity, onHomeSubItemTapped)
    binding.rvOffers?.adapter = trendingOfferAdapter
    binding.rvOffersHidden?.adapter = trendingOfferAdapterHidden
    arrayList?.get(position)?.sectionDataList?.let { list ->
      val size: Int = list.size
      val first: ArrayList<SectionData> = ArrayList(list.subList(0, size / 2))
      val second: ArrayList<SectionData> = ArrayList(list.subList(size / 2, size))
      trendingOfferAdapter.setDataChanged(first)
      trendingOfferAdapterHidden.setDataChanged(second)
    }
    binding.tvShow?.setOnClickListener {
      binding.isShow = !binding.isShow!!
    }
  }

  private fun bindSmallDeals(binding: LayoutHorizontalDeallSmallSliderBinding, position: Int) {
    var discountAdapter = DiscountAdapter(activity, onHomeSubItemTapped)
    binding.recyclerViewDiscounts?.adapter = discountAdapter
    discountAdapter.setDataChanged(arrayList?.get(position)?.sectionDataList)
  }

  private fun bindPromotion(binding: LayoutHorizontalCatGridBinding, position: Int) {
    var promoMultiple =
      PromotionsMultipleAdapter(activity, onHomeSubItemTapped)
    binding.rvPromoGrid?.adapter = promoMultiple
//    val snapHelper = LinearSnapHelper() // Or PagerSnapHelper
//    binding.rvPromoGrid.onFlingListener = null;
//    snapHelper.attachToRecyclerView(binding.rvPromoGrid)
    promoMultiple.setDataChanged(arrayList?.get(position)?.sectionDataList)
  }

  private fun bindVerticalPromotion(binding: LayoutVerticalPromotionsBinding, position: Int) {
    binding.item = arrayList?.get(position)
    binding.title = arrayList?.get(position)?.sectionTitle
    val adapter = FeaturesOffersAdapter(activity, onHomeSubItemTapped)
    binding.recyclerViewTrends?.adapter = adapter
    adapter.setDataChanged(arrayList?.get(position)?.sectionDataList)
  }

  private fun bindTrendsWithBrands(binding: LayoutHorizontalTrendsBinding, position: Int) {
    binding.item = arrayList?.get(position)
    binding.title = arrayList?.get(position)?.sectionTitle
    val trendings =
      com.lifepharmacy.application.ui.dashboard.BrandsAdapter(activity, onHomeSubItemTapped)
    binding.recyclerViewTrends.adapter = trendings
    trendings.setDataChanged(arrayList?.get(position)?.sectionDataList)
  }


  private fun bindOffer(binding: LayoutHorizontalOffersBinding, position: Int) {
    binding.click = onClickOffers
    binding.item = arrayList?.get(position)
    binding.title = arrayList?.get(position)?.sectionTitle
    val offersAdapter = HomeOffersAdapter(activity, onClickHomeOffer)
    binding.recyclerViewOffers.adapter = offersAdapter
    val offerCategoryAdapter = OfferCategoryAdapter(activity, object : ClickOffersCategory {
      override fun onClickOfferCategory(position: Int) {
      }

    })
    binding.rvTags.adapter = offerCategoryAdapter
    offerCategoryAdapter.setItemSelected(0)

  }

  private fun bindStaticSlider(binding: LayoutHorizontalStaticSliderBinding, mainIndex: Int) {
    val list: ArrayList<CarouselItem>
    val mainItem = arrayList?.get(mainIndex)
    try {
      val item = arrayList?.get(mainIndex)?.sectionDataList?.get(0)
      val imageHeight = item?.imageHeight?.stringToNullSafeDouble() ?: 1.0
      val imageWidth = item?.imageWidth?.stringToNullSafeDouble() ?: 1.0

      val layoutParams = binding.carousel.layoutParams as ConstraintLayout.LayoutParams
      layoutParams.dimensionRatio = "$imageWidth:$imageHeight"
      binding.carousel.layoutParams = layoutParams
    } catch (e: Exception) {
      e.printStackTrace()
    }

    list = mainItem?.sectionDataList?.map {
      CarouselItem(imageUrl = it.imageUrl)
    } as ArrayList<CarouselItem>
    binding.carousel.addData(list)
    if (list.size > 1) {
      binding.customIndicator.let {
        binding.carousel.setIndicator(
          it
        )
      }
    } else {
      binding.customIndicator.visibility = View.GONE
    }
    binding.carousel.onItemClickListener = object : OnItemClickListener {
      override fun onClick(position: Int, carouselItem: CarouselItem) {
        onHomeSubItemTapped.onClickHomeSubItem(
          arrayList?.get(mainIndex)?.sectionDataList?.get(position)?.title,
          arrayList?.get(mainIndex)?.sectionDataList?.get(position)?.id,
          arrayList?.get(mainIndex)?.sectionDataList?.get(position)?.type,
          arrayList?.get(mainIndex)?.sectionDataList?.get(position)
        )
      }

      override fun onLongClick(position: Int, dataObject: CarouselItem) {
      }

    }
  }

  private fun bindTilesSlider(
    binding: LayoutHorizontalCategoriesTilesSliderBinding,
    position: Int
  ) {
    binding.item = arrayList?.get(position)
    binding.title = arrayList?.get(position)?.sectionTitle
    val layoutParams = binding.recyclerViewOptions.layoutParams as ConstraintLayout.LayoutParams
    layoutParams.dimensionRatio = "375:211"
    binding.recyclerViewOptions.layoutParams = layoutParams
    val nineByNinTilesAdapter = HomeNineByNineAdapter(activity, onHomeSubItemTapped)
    binding.recyclerViewOptions.adapter = nineByNinTilesAdapter
    binding.recyclerViewOptions.onFlingListener = null
//    val snapHelper = PagerSnapHelper() // Or PagerSnapHelper
    val snapToBlock = SnapToBlock(3)
    snapToBlock.attachToRecyclerView(binding.recyclerViewOptions)
    binding.recyclerViewOptions.onFlingListener = null;
    snapToBlock.attachToRecyclerView(binding.recyclerViewOptions)
    nineByNinTilesAdapter.setDataChanged(arrayList?.get(position)?.sectionDataList)
    binding.pageIndicatorView.count = Utils.getBlockNumber(
      9.0,
      arrayList?.get(position)?.sectionDataList?.size?.intToNullSafeDoubleByDefault1()

    );
    snapToBlock.setSnapBlockCallback(object : SnapToBlock.SnapBlockCallback {
      override fun onBlockSnap(snapPosition: Int) {

      }

      override fun onBlockSnapped(snapPosition: Int) {
        binding.pageIndicatorView.selection = snapPosition;
      }

    })

  }

  private fun bindDynamicGrid(
    binding: LayoutDynamiceGridRecyclerViewBinding,
    position: Int
  ) {
    val item = arrayList?.get(position)
    binding.item = item
    binding.title = item?.sectionTitle
    val layoutManager =
      GridLayoutManager(activity, item?.numberOfColumns?.stringToNullSafeInt() ?: 1)
    binding.rvSections.layoutManager = layoutManager
    val dynamicAdapter =
      DynamicGridAdapter(
        activity,
        itemHeight = item?.sectionDataList?.first()?.imageHeight ?: "1",
        itemWidth = item?.sectionDataList?.first()?.imageWidth ?: "1",
        onHomeSubItemTapped
      )
    binding.rvSections.adapter = dynamicAdapter
    dynamicAdapter.setDataChanged(item?.sectionDataList)
    binding.rvSections.isNestedScrollingEnabled = false
  }
}