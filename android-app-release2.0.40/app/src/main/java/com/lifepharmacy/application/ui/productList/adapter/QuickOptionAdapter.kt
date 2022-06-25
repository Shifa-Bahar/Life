package com.lifepharmacy.application.ui.productList.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemQuickOptionsBinding
import com.lifepharmacy.application.model.category.Section

import kotlin.collections.ArrayList

class QuickOptionAdapter(
  context: Activity?,
  private val onItemTapped: SuperSellerQuickOption
) : RecyclerView.Adapter<QuickOptionAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<Section>? = ArrayList()
  var activity: Activity? = context
  var rowSelected = -1
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemQuickOptionsBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_quick_options,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemQuickOptionsBinding? = DataBindingUtil.bind(itemView)
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
    holder.binding?.item = item
    holder.binding?.position = position
    holder.binding?.click = onItemTapped
    holder.binding?.isSelected = rowSelected == position
  }

  fun setItemSelected(position: Int) {
    rowSelected = position
    notifyDataSetChanged()
  }

}