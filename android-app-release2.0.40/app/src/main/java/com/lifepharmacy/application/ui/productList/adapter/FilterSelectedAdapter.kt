package com.lifepharmacy.application.ui.productList.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemFilterCheckboxBinding
import com.lifepharmacy.application.databinding.ItemFiltersTopBottomBinding
import com.lifepharmacy.application.model.filters.FilterModel
import com.lifepharmacy.application.model.filters.FilterTypeModel
import com.lifepharmacy.application.ui.filters.adapters.ClickFilterCheckBox

class FilterSelectedAdapter(
  context: Activity,
  private var parentPosition: Int,
  private val type: String,
  private val onItemTapped: ClickFilterCheckBox
) :
  RecyclerView.Adapter<FilterSelectedAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<FilterModel>? = ArrayList()
  var activity: Activity? = context
  var orignalArray: ArrayList<FilterModel>? = ArrayList()
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemFiltersTopBottomBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_filters_top_bottom,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemFiltersTopBottomBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(list: ArrayList<FilterModel>?, parentPosition: Int) {
    val filteredArray = list?.filter { item ->
      item.isChecked
    }
    orignalArray = list
    arrayList?.clear()
    if (filteredArray != null) {
      arrayList?.addAll(filteredArray)
    }
    this.parentPosition = parentPosition
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

    if (type == "orderBy"){
      val filter = orignalArray?.firstOrNull { internalFilter -> internalFilter.title == item.title }
      val indexOfProduct: Int? = orignalArray?.indexOf(filter)
      if (indexOfProduct != null) {
        holder.binding?.position = indexOfProduct
      }
    }else{
      val filter = orignalArray?.firstOrNull { internalFilter -> internalFilter.id == item.id }
      val indexOfProduct: Int? = orignalArray?.indexOf(filter)
      if (indexOfProduct != null) {
        holder.binding?.position = indexOfProduct
      }
    }
    if (item.isChecked) {
      holder.binding?.clMain?.visibility = View.VISIBLE
      holder.itemView.visibility = View.VISIBLE
      if (!item.title.isNullOrBlank()) {
        holder.binding?.title = item.title
      } else {
        holder.binding?.title = item.name
      }
      holder.binding?.type = type
      holder.binding?.click = onItemTapped
      holder.binding?.parentPosition = parentPosition
      holder.binding?.item = item
    } else {
      holder.itemView.visibility = View.GONE
      holder.binding?.clMain?.visibility = View.GONE
    }



  }

  fun setItems(position: Int) {
    notifyItemChanged(position)
  }
}