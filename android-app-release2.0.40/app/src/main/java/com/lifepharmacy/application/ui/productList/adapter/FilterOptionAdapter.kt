package com.lifepharmacy.application.ui.productList.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemFiltersBinding
import com.lifepharmacy.application.model.filters.FilterModel
import com.lifepharmacy.application.ui.productList.FilterOption

class FilterOptionAdapter(context: Activity?, private val onItemTapped: FilterOption) :
  RecyclerView.Adapter<FilterOptionAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<FilterModel>? = ArrayList()
  var activity: Activity? = context

  var selectedItems = ArrayList<FilterModel>()
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemFiltersBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_filters,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemFiltersBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<FilterModel>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()

  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    var item = arrayList!![position]
    holder.binding?.filter = item
    holder.binding?.click = onItemTapped
    holder.binding?.isSelected = selectedItems.contains(item)


//        val color =  if (arrayList?.get(position)!!.linked) activity?.resources?.getColor(R.color.accent_blue) else activity?.resources?.getColor(R.color.color_8585)
//        val drawable =  if (arrayList?.get(position)!!.linked) activity?.resources?.getDrawable(R.drawable.bg_e9_e_dp_round_corner_with_border) else activity?.resources?.getDrawable(R.drawable.bg_e9_e_dp_round_corner)
//
//        holder.binding?.txtViewFilterName?.setTextColor(color!!)
//        holder.binding?.txtViewFilterName?.background = drawable
//
//
//        holder.binding?.root?.setOnClickListener {
//            val colorT =  if (arrayList?.get(position)!!.linked) activity?.resources?.getColor(R.color.accent_blue) else activity?.resources?.getColor(R.color.color_8585)
//            val drawableT =  if (arrayList?.get(position)!!.linked) activity?.resources?.getDrawable(R.drawable.bg_e9_e_dp_round_corner_with_border) else activity?.resources?.getDrawable(R.drawable.bg_e9_e_dp_round_corner)
//
//            holder.binding?.txtViewFilterName?.setTextColor(colorT!!)
//            holder.binding?.txtViewFilterName?.background = drawableT
//
//        }
  }

  fun setItems(items: ArrayList<FilterModel>) {
    selectedItems = items
    notifyDataSetChanged()
  }
}