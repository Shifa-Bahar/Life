package com.lifepharmacy.application.ui.search.adapters

import android.app.Activity
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemProductsVerticalBinding
import com.lifepharmacy.application.databinding.LayoutTitleRecyclerBottomTextBinding
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.model.search.SearchCategory
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeProduct
import com.lifepharmacy.application.utils.PricesUtil
import kotlin.collections.ArrayList

class SearchResultAdapter(
  context: Activity,
  val fragment: Fragment,
  private val onItemTapped: ClickHomeProduct,
  private val appManager: AppManager,
  val onClick: ClickSearchCategoryItem,
  val perOrderTitle: String
) :
  RecyclerView.Adapter<SearchResultAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<ProductDetails>? = ArrayList()
  var searchCatList: ArrayList<SearchCategory>? = ArrayList()
  var activity: Activity = context
  var term: String = ""
  var viewType = 0
  override fun getItemViewType(position: Int): Int {
    return if (position == 0) {
      0
    } else {
      1
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    this.viewType = viewType
    return if (viewType == 0) {
      val binding: LayoutTitleRecyclerBottomTextBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_title_recycler_bottom_text,
        parent, false
      )
      ItemViewHolder(binding.root, viewType)
    } else {
      val binding: ItemProductsVerticalBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.item_products_vertical,
        parent, false
      )
      ItemViewHolder(binding.root, viewType)
    }
  }

//  override fun onViewRecycled(holder: ItemViewHolder) {
//    super.onViewRecycled(holder)
//    holder.binding?.imageViewIcon?.let { Glide.with(activity).clear(it) }
//  }

  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size + 1 else 1
  }

  class ItemViewHolder internal constructor(itemView: View, int: Int) :
    RecyclerView.ViewHolder(itemView) {
    var bindingBanner: LayoutTitleRecyclerBottomTextBinding? = null
    var bindingProducts: ItemProductsVerticalBinding? = null

    init {
      if (int == 0) {
        bindingBanner = DataBindingUtil.bind(itemView)
      } else {
        bindingProducts = DataBindingUtil.bind(itemView)
      }
    }
  }

  fun setCategoryResult(list: ArrayList<SearchCategory>, term: String?) {
    this.term = term ?: ""
    searchCatList?.clear()
    searchCatList?.addAll(list)
    viewType = 0
    notifyItemChanged(0)
  }

  fun setDataChanged(order: ArrayList<ProductDetails>?) {
//        arrayList?.clear()
    if (arrayList.isNullOrEmpty()) {
      arrayList = ArrayList()
    }
    val oldSize = arrayList?.size ?: 0
//    var rangeRemove = 0
    if (order != null) {
      arrayList?.addAll(order)
      notifyItemRangeInserted(oldSize, order.size)
//      notifyDataSetChanged()
    }
  }

  fun refreshData(list: ArrayList<ProductDetails>?) {
    arrayList?.clear()
    if (list != null) {
      arrayList?.addAll(list)
    }
    notifyDataSetChanged()
  }

  fun removeItems() {
    arrayList?.clear()
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

    if (position == 0) {
      holder.bindingBanner?.let { bindCategories(it) }
    } else {
      if (!arrayList.isNullOrEmpty()) {
        holder.bindingProducts?.let { bindProducts(it, position - 1) }
      }
    }

//        val displayMetrics = DisplayMetrics()
//        activity.windowManager?.getDefaultDisplay()?.getMetrics(displayMetrics)
//        val screenWidth = displayMetrics.widthPixels.toDouble()
//        val withPers = screenWidth
//        holder.binding?.clMain?.layoutParams?.height = (withPers * 0.4).roundToInt()

  }

  //  fun applySearch(string: String) {
//    val filtered = arrayList?.filter { product ->
//      product.title.toLowerCase(Locale.ROOT).contains(
//        string.toLowerCase(
//          Locale.ROOT
//        )
//      )
//    }
//    refreshData(filtered as ArrayList<ProductDetails>)
//  }
  private fun bindCategories(binding: LayoutTitleRecyclerBottomTextBinding) {
    binding.title = activity.getString(R.string.products_in_cat)
    term.let {
      val categoryAdapter = SearchCategoryAdapter(activity, term = term, onItemTapped = onClick)
      binding.recyclerView.adapter = categoryAdapter
      categoryAdapter.setDataChanged(searchCatList)
    }
    binding.isAnyCategory = !searchCatList.isNullOrEmpty()
  }

  private fun bindProducts(binding: ItemProductsVerticalBinding, position: Int) {
    val recyclerviewPosition = position + 1
    binding.preOder = perOrderTitle
    val item = arrayList!![position]
    binding.tvOrignalPrice.paintFlags =
      binding.tvOrignalPrice.paintFlags.or(Paint.STRIKE_THRU_TEXT_FLAG)
    binding.item = item
    binding.price = PricesUtil.getRelativePrice(item.prices, activity)
    binding.isInCart = appManager.offersManagers.checkIfProductAlreadyExistInCart(item)
    binding.isInWishList = appManager.wishListManager.checkIfItemExistInWishList(item)
//    binding.clMain.animation =
//      AnimationUtils.loadAnimation(activity, R.anim.mainlongfadein)
    binding.imageViewHeart.setOnClickListener {
      onItemTapped.onClickWishList(item, position)
      notifyItemChanged(recyclerviewPosition)
    }
    binding.clMain.setOnClickListener {
      onItemTapped.onProductClicked(item, position)

    }
//    if (item.images.featuredImage.isNotEmpty()) {
//      CoroutineScope(Dispatchers.IO).launch {
//        delay(50)
//        CoroutineScope(Dispatchers.Main.immediate).launch {
//          Glide.with(fragment)
//            .load(item.images.featuredImage)
//            .placeholder(R.drawable.image_place_holder)
//            .into(binding.imageViewIcon)
////          Picasso.get()
////            .load(item.images.featuredImage)
////            .error(R.drawable.image_place_holder)
////            .placeholder(R.drawable.image_place_holder)
////            .into(holder.binding!!.imageViewIcon)
//        }
//
//      }
//
//    }

//        val circularProgressDrawable = CircularProgressDrawable(activity)
//        circularProgressDrawable.strokeWidth = 5f
//        circularProgressDrawable.centerRadius = 30f
//        circularProgressDrawable.start()

//        holder.binding?.imageViewIcon?.let {
//            Glide.with(activity)
//                .load(item.images.featuredImage)
////                .apply(
////                    RequestOptions()
////                        .override(Target.SIZE_ORIGINAL)
////                        .format(DecodeFormat.PREFER_ARGB_8888)
////                )
////                .placeholder(circularProgressDrawable)
//                .into(it)
//        }


    appManager.offersManagers.checkAndGetExistingQTY(item)?.let {
      binding.qty = it.toString()
    }
    binding.btnMinus.setOnClickListener {
      binding.qty?.let {
        if (it.toInt() == 1) {
          MaterialAlertDialogBuilder(
            activity,
            R.style.ThemeOverlay_App_MaterialAlertDialog
          )
            .setTitle(activity.resources.getString(R.string.remove_product_title))
            .setMessage(activity.resources.getString(R.string.remove_product_descr))
            .setNegativeButton(activity.resources.getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(activity.resources.getString(R.string.remove)) { _, _ ->
              onItemTapped.onClickMinus(item, position)
              notifyItemChanged(recyclerviewPosition)
            }
            .show()
        } else {
          onItemTapped.onClickMinus(item, position)
          notifyItemChanged(recyclerviewPosition)
        }
      }
    }
    binding.btnPlus.setOnClickListener {
      onItemTapped.onClickPlus(item, position)
      notifyItemChanged(recyclerviewPosition)
    }
    binding.imgCart.setOnClickListener {
      onItemTapped.onClickAddProduct(item, position)
      notifyItemChanged(recyclerviewPosition)
    }
    binding.btnNotify.setOnClickListener {
      onItemTapped.onClickNotify(item, position)
    }
    binding.btnOrder.setOnClickListener {
      onItemTapped.onClickOrderOutOfStock(item, position)
    }
  }

}