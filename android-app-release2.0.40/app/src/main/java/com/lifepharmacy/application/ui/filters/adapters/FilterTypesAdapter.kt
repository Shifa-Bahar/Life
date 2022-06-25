package com.lifepharmacy.application.ui.filters.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemFiltersOptionsBinding
import com.lifepharmacy.application.managers.FiltersManager
import com.lifepharmacy.application.model.filters.FilterTypeModel

class FilterTypesAdapter(
  context: Activity,
  private val onItemTapped: ClickFilterType,
  private val filtersManager: FiltersManager
) :
  RecyclerView.Adapter<FilterTypesAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<FilterTypeModel>? = ArrayList()
  var activity: Activity = context
  var rowSelected = -1
  var oldSelection = -1
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemFiltersOptionsBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_filters_options,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemFiltersOptionsBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<FilterTypeModel>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
//    var item = arrayList!![position]
//    holder.binding?.item = item
//    holder.binding?.position = position
////        holder.binding?.subtitle = filtersManager.getSelectedText(position)
//    holder.binding?.click = onItemTapped
//    holder.binding?.isSelected = rowSelected == position
    holder.binding?.let { bindView(it,position) }

  }

  private fun bindView(binding: ItemFiltersOptionsBinding, position: Int){
    val item = arrayList!![position]
    binding.item = item
    binding.position = position
//        holder.binding?.subtitle = filtersManager.getSelectedText(position)
    binding.click = onItemTapped
    binding.isSelected = rowSelected == position
    if (item.individualCount>0){
      binding.tvFilterCount.visibility = View.VISIBLE
    }else{
      binding.tvFilterCount.visibility = View.GONE
    }
    if(item.inputType == "range"){
      if (filtersManager.fromPrice.isNullOrEmpty() && filtersManager.toPrice.isNullOrEmpty()){
        binding.tvFilterCount.visibility = View.GONE
      }else{
        binding.tvFilterCount.visibility = View.VISIBLE
      }
    }
  }
  fun setItemSelected(position: Int) {
    rowSelected = position
    notifyItemChanged(rowSelected)
    notifyItemChanged(oldSelection)
    oldSelection = rowSelected
  }
}