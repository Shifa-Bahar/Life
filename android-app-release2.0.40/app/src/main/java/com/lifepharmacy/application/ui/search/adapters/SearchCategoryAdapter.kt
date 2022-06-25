package com.lifepharmacy.application.ui.search.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemSearchCatBinding
import com.lifepharmacy.application.model.search.SearchCategory

import kotlin.collections.ArrayList

class SearchCategoryAdapter(
  context: Activity?,
  val term:String,
  val onItemTapped:ClickSearchCategoryItem
) : RecyclerView.Adapter<SearchCategoryAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<SearchCategory>? = ArrayList()
  var activity: Activity? = context
  var rowSelected = -1
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemSearchCatBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_search_cat,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemSearchCatBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(list: ArrayList<SearchCategory>?) {
    arrayList?.clear()
    if (list != null) {
      arrayList?.addAll(list)
    }
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    var item = arrayList!![position]
//    holder.binding?.position = position
    holder.binding?.item = item
    holder.binding?.term = term
    holder.binding?.click = onItemTapped

  }

  fun setSelectedRow(int: Int) {
    rowSelected = int
    notifyDataSetChanged()
  }
}