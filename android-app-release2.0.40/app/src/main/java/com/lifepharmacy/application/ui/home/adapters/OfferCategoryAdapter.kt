package com.lifepharmacy.application.ui.home.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemMainQuickOptionsBinding
import com.lifepharmacy.application.databinding.ItemOfferCategoriesBinding
import com.lifepharmacy.application.databinding.ItemQuickOptionsBinding
import com.lifepharmacy.application.model.category.Section

import kotlin.collections.ArrayList

class OfferCategoryAdapter(
  context: Activity?,
  private val onItemTapped: ClickOffersCategory
) : RecyclerView.Adapter<OfferCategoryAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<Section>? = ArrayList()
  var activity: Activity? = context
  var rowSelected = 0
  var oldSelection = 0
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemOfferCategoriesBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_offer_categories,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return 10
//    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemOfferCategoriesBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<Section>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
//    val item = (arrayList ?: return)[position]
//    holder.binding?.item = item
    holder.binding?.position = position
    holder.binding?.click = onItemTapped
    holder.binding?.isSelected = rowSelected == position
    holder.binding?.clMain?.setOnClickListener {
      onItemTapped.onClickOfferCategory(position)
      setItemSelected(position)
    }

  }


  fun setItemSelected(position: Int) {
    rowSelected = position
    notifyItemChanged(oldSelection)
    notifyItemChanged(rowSelected)
    oldSelection = rowSelected
  }

}