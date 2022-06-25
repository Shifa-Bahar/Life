package com.lifepharmacy.application.ui.orders.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.*
import com.lifepharmacy.application.model.*

import kotlin.collections.ArrayList

class RatingTagsAdapter(
  context: Activity?,
  private val onItemTaped: ClickRatingTag
) : RecyclerView.Adapter<RatingTagsAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<String>? = ArrayList()
  var activity: Activity? = context
  var selectedItems = ArrayList<String>()
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemRatingTagBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_rating_tag,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemRatingTagBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<String>?) {
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
    holder.binding?.click = onItemTaped
    holder.binding?.isChecked = selectedItems.contains(item)

  }
  fun clearItems(){
    selectedItems.clear()
    notifyDataSetChanged()
  }
  fun setItems(position: Int, items: ArrayList<String>) {
    selectedItems = items
    notifyItemChanged(position)
  }
}