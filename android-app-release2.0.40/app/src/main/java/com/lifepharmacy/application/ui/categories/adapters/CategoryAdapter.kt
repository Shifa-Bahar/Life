package com.lifepharmacy.application.ui.categories.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemCategoryBinding
import com.lifepharmacy.application.model.category.CategoryMainModel
import com.lifepharmacy.application.model.category.RootCategory

class CategoryAdapter(context: Activity?, private val onItemTapped: ClickCategory) :
  RecyclerView.Adapter<CategoryAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<RootCategory>? = ArrayList()
  var activity: Activity? = context
  var rowSelected = 0
  var oldSelection = 0
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemCategoryBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_category,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemCategoryBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<RootCategory>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    var item = arrayList!![position]
    holder.binding?.position = position
    holder.binding?.item = item
    holder.binding?.click = onItemTapped
    holder.binding?.isSelected = rowSelected == position

//        if (rowSelected == position){
//            holder.binding?.cvMain?.setCardBackgroundColor(ContextCompat.getColorStateList(activity!!,R.color.color_eff))
//            holder.binding?.clMain?.background = ContextCompat.getDrawable(activity!!,R.drawable.bg_8b_border)
//            holder.binding?.tvTitle?.setTextColor(ContextCompat.getColor(activity!!,R.color.accent_blue_darker))
//        }else{
//            holder.binding?.cvMain?.setCardBackgroundColor(ContextCompat.getColorStateList(activity!!,R.color.white))
//            holder.binding?.clMain?.background = ContextCompat.getDrawable(activity!!,R.drawable.bg_transparent)
//            holder.binding?.tvTitle?.setTextColor(ContextCompat.getColor(activity!!,R.color.color_8585))
//
//
//        }
  }

  fun setItemSelected(position: Int) {
    rowSelected = position
    notifyItemChanged(oldSelection)
    notifyItemChanged(rowSelected)
    oldSelection = rowSelected
  }
}