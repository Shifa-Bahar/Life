package com.lifepharmacy.application.ui.search.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemSearchRecentBinding
import com.lifepharmacy.application.databinding.ItemSearchTrendingBinding
import com.lifepharmacy.application.model.search.agolia.Hits

import kotlin.collections.ArrayList

class SearchRecentAdapter(
  context: Activity?,
  val onClick:ClickTrendsSearchItem
) : RecyclerView.Adapter<SearchRecentAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<String?> = ArrayList()
  var activity: Activity? = context
  var rowSelected = -1
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemSearchRecentBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_search_recent,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemSearchRecentBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(list: ArrayList<String?>) {
    arrayList?.clear()
    if (list != null) {
      arrayList?.addAll(list)
    }
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    var item = arrayList!![position]
    holder.binding?.position = position
//    holder.binding?.term = item
    holder.binding?.click = onClick
    holder.binding?.tvType2?.text = item.toString()

  }

  fun setSelectedRow(int: Int) {
    rowSelected = int
    notifyDataSetChanged()
  }
}


