package com.lifepharmacy.application.ui.filters.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemFilterCheckboxBinding
import com.lifepharmacy.application.model.filters.FilterModel

class FilterCheckboxAdapter(
  context: Activity?,
  private val parentPosition: Int,
  private val type: String,
  private val onItemTapped: ClickFilterCheckBox
) :
  RecyclerView.Adapter<FilterCheckboxAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<FilterModel>? = ArrayList()
  var activity: Activity? = context
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemFilterCheckboxBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_filter_checkbox,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemFilterCheckboxBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<FilterModel>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }

  fun replaceData(list: ArrayList<FilterModel>?) {
    arrayList?.clear()
    if (list != null) {
      arrayList?.addAll(list)
    }
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = arrayList!![position]
    holder.binding?.item = item
    if (item.title != null && !item.title.isNullOrBlank()) {
      holder.binding?.title = item.title
    } else {
      holder.binding?.title = item.name
    }
    holder.binding?.type = type
    holder.binding?.position = position
    holder.binding?.click = onItemTapped
    holder.binding?.isChecked = item.isChecked
    holder.binding?.parentPosition = parentPosition

  }

  fun setItems(position: Int) {
    notifyItemChanged(position)
  }
}