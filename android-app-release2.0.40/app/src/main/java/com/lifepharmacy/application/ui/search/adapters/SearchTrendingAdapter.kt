package com.lifepharmacy.application.ui.search.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemSearchTrendingBinding
import com.lifepharmacy.application.model.search.agolia.Hits

import kotlin.collections.ArrayList

class SearchTrendingAdapter(
  context: Activity?,
  val onClick:ClickTrendsSearchItem
) : RecyclerView.Adapter<SearchTrendingAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<Hits>? = ArrayList()
  var activity: Activity? = context
  var rowSelected = -1
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemSearchTrendingBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_search_trending,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemSearchTrendingBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(list: ArrayList<Hits>?) {
    arrayList?.clear()
    if (list != null) {
      arrayList?.addAll(list)
    }
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    var item = arrayList!![position]
    holder.binding?.position = position
    holder.binding?.term = item
    holder.binding?.click = onClick

  }

  fun setSelectedRow(int: Int) {
    rowSelected = int
    notifyDataSetChanged()
  }
}