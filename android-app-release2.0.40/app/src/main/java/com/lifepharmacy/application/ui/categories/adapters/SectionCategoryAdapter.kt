package com.lifepharmacy.application.ui.categories.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.LayoutStaticSubCategoryBinding
import com.lifepharmacy.application.model.category.Section
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeSubItem
import com.lifepharmacy.application.ui.dashboard.adapter.ClickPromotion

class SectionCategoryAdapter(
  context: Activity?,
  private val onSectionProduct: ClickSectionProductsCategory,
  private val homeSubItem: ClickHomeSubItem
) :
  RecyclerView.Adapter<SectionCategoryAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<Section>? = ArrayList()
  var activity: Activity? = context
  var rowSelected = 0
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: LayoutStaticSubCategoryBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.layout_static_sub_category,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: LayoutStaticSubCategoryBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<Section>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    var item = arrayList!![position]
    holder.binding?.clMain?.animation =
      AnimationUtils.loadAnimation(activity, R.anim.mainfadein)
    holder.binding?.item = item
    holder.binding?.isThereBanner = !item.images?.banner.isNullOrEmpty()

    if (item.sectionType == "brand") {
      var brands = BrandsAdapter(activity, homeSubItem)
      holder.binding?.rvSubSubCategory?.adapter = brands
      brands.setDataChanged(arrayList?.get(position)?.sectionDataArray)
    }
    if (item.sectionType == "dual_row_products") {
      var products = SectionProductsAdapter(activity, onSectionProduct)
      holder.binding?.rvSubSubCategory?.adapter = products
      products.setDataChanged(arrayList?.get(position)?.sectionDataArray)
    }
    if (item.sectionType == "single_row_products") {
      var products = SectionProductsAdapter(activity, onSectionProduct)
      holder.binding?.rvSubSubCategory?.adapter = products
      products.setDataChanged(arrayList?.get(position)?.sectionDataArray)
    }
  }

  fun setItemSelected(position: Int) {
    rowSelected = position
    notifyDataSetChanged()
  }
}