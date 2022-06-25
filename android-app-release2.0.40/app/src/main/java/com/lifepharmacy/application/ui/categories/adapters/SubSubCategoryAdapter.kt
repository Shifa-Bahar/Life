package com.lifepharmacy.application.ui.categories.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemSubSubCategoryBinding
import com.lifepharmacy.application.model.category.Section
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeSubItem

class SubSubCategoryAdapter(
  context: Activity?,
  private val onItemTapped: ClickHomeSubItem,
  private val title: String?,
  private val isBrand: Boolean = false
) :
  RecyclerView.Adapter<SubSubCategoryAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<Section>? = ArrayList()
  var activity: Activity? = context
  var rowSelected = -1
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemSubSubCategoryBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_sub_sub_category,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemSubSubCategoryBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<Section>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    var item = (arrayList ?: return)[position]
    holder.binding?.item = item.name
    holder.binding?.image = item.images?.logo
    holder.binding?.isSelected = rowSelected == position
    holder.binding?.isBrand = isBrand
    holder.binding?.clMainItem?.setOnClickListener {
      onItemTapped.onClickHomeSubItem(item.name, item.id, "category", null)
    }

  }

  fun setItemSelected(position: Int) {
    rowSelected = position
    notifyDataSetChanged()
  }
}