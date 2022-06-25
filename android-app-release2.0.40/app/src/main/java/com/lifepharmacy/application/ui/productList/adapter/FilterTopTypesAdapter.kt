package com.lifepharmacy.application.ui.productList.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemFiltersOptionsBinding
import com.lifepharmacy.application.databinding.ItemFiltersTopMainBinding
import com.lifepharmacy.application.managers.FiltersManager
import com.lifepharmacy.application.model.docs.DocumentModel
import com.lifepharmacy.application.model.filters.FilterTypeModel
import com.lifepharmacy.application.ui.filters.adapters.ClickFilterType
import com.lifepharmacy.application.utils.universal.Utils

class FilterTopTypesAdapter(
  context: Activity,
  private val onItemTapped: ClickFilterType,
  private val filtersManager: FiltersManager
) :
  RecyclerView.Adapter<FilterTopTypesAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<FilterTypeModel>? = ArrayList()
  var orignalArray: ArrayList<FilterTypeModel>? = ArrayList()
  var activity: Activity = context
  var rowSelected = -1
  var oldSelection = -1
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemFiltersTopMainBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_filters_top_main,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemFiltersTopMainBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(list: ArrayList<FilterTypeModel>?) {
    val filteredArray = list?.filter { item ->
      item.individualCount > 0 || item.inputType == "range"
    }
    orignalArray = list
    arrayList?.clear()
    if (filteredArray != null) {
      arrayList?.addAll(filteredArray)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = arrayList!![position]
    if(item.individualCount > 0 || item.inputType == "range"){
      holder.itemView.visibility = View.VISIBLE
    }else{
      holder.itemView.visibility = View.GONE
    }
    holder.binding?.let { bindView(it, position) }

  }

  private fun bindView(binding: ItemFiltersTopMainBinding, position: Int) {
    val item = arrayList!![position]
    binding.item = item

    val filter = orignalArray?.firstOrNull { internalFilter -> internalFilter.type == item.type }
    val indexOfProduct: Int? = orignalArray?.indexOf(filter)

    if (indexOfProduct != null) {
      binding.position = indexOfProduct
    }
    binding.count = item.individualCount.toString()

    if (item.inputType == "range"){
      binding.tvCount.visibility = View.GONE
    }else {
      binding.tvCount.visibility = View.VISIBLE
    }
//        holder.binding?.subtitle = filtersManager.getSelectedText(position)
    binding.click = onItemTapped
    binding.isSelected = rowSelected == position

  }
  fun selectedWithCompleteRefresh(position: Int){
    rowSelected = if (position != rowSelected) {
      position
    } else {
      -1
    }
    notifyDataSetChanged()
  }

  fun setItemSelected(position: Int) {
    rowSelected = if (position != rowSelected) {
      position
    } else {
      -1
    }
    notifyItemChanged(rowSelected)
    notifyItemChanged(oldSelection)
    oldSelection = rowSelected
  }
}