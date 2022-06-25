package com.lifepharmacy.application.ui.productList.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemMainQuickOptionsBinding
import com.lifepharmacy.application.databinding.ItemQuickOptionsBinding
import com.lifepharmacy.application.databinding.ItemSubQuickOptionsBinding
import com.lifepharmacy.application.model.category.Section

import kotlin.collections.ArrayList

class SubQuickOptionsAdapter(
  context: Activity?,
  private val onItemTapped: SuperSellerQuickOption
) : RecyclerView.Adapter<SubQuickOptionsAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<Section>? = ArrayList()
  var activity: Activity? = context
  var rowSelected: String? = ""
  var laodingPosition = -1
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemSubQuickOptionsBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_sub_quick_options,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemSubQuickOptionsBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<Section>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
      for (item in 0 until (arrayList?.size ?: 0)) {
        if (arrayList?.get(item)?.isSelected == true) {
          val index: Int = arrayList!!.indexOf(arrayList?.get(item))
          var temp = arrayList?.get(item)
          arrayList!!.removeAt(index);
          if (temp != null) {
            arrayList!!.add(0, temp)
          }
        }
      }
    }
    laodingPosition = -1
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = arrayList!![position]
    holder.binding?.item = item
    holder.binding?.position = position
    holder.binding?.click = onItemTapped

    holder.binding?.isSelected = item.isSelected
    holder.binding?.loading = position == laodingPosition
  }

  fun setLoadingItem(position: Int) {
    laodingPosition = position
    notifyItemChanged(position)
  }
//  fun setItemSelected(position: Int) {
//    rowSelected = position
//    notifyDataSetChanged()
//  }

}