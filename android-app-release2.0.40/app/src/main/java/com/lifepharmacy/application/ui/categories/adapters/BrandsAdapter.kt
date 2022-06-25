package com.lifepharmacy.application.ui.categories.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.*
import com.lifepharmacy.application.model.*
import com.lifepharmacy.application.model.home.SectionData
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeSubItem
import com.lifepharmacy.application.ui.dashboard.adapter.ClickPromotion

import kotlin.collections.ArrayList

class BrandsAdapter(context: Activity?, private val onItemTapped: ClickHomeSubItem) :
  RecyclerView.Adapter<BrandsAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<SectionData>? = ArrayList()
  var activity: Activity? = context


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemCategoryBrandsBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_category_brands,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemCategoryBrandsBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<SectionData>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    var item = arrayList!![position]
    holder.binding?.item = item
    holder.binding?.title = activity?.getString(R.string.brands)
    holder.binding?.click = onItemTapped
  }

}