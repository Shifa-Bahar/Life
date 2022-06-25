package com.lifepharmacy.application.ui.categories.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemSubCategoryBinding
import com.lifepharmacy.application.model.category.Children
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeSubItem

class CategoryChildsAdapter(
  context: Activity?,
  private val onItemTapped: ClickSubCategory,
  private val onSubItemTapped: ClickHomeSubItem
) :
  RecyclerView.Adapter<CategoryChildsAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<Children> = ArrayList()
  var activity: Activity? = context
  var rowSelected = 0
  var oldSelection = 0
  lateinit var subSubCategoryAdapter: SubSubCategoryAdapter
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemSubCategoryBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_sub_category,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return arrayList.size
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemSubCategoryBinding? = DataBindingUtil.bind(itemView)
  }

  //    fun setDataChanged(categoryChild: ArrayList<Children>) {
//        val diffCallback = ChildCategoryDiffCallback(arrayList, categoryChild)
//        val diffResult = DiffUtil.calculateDiff(diffCallback)
//        arrayList.clear()
//        arrayList.addAll(categoryChild)
//        diffResult.dispatchUpdatesTo(this)
//    }
  fun setDataChanged(order: ArrayList<Children>?) {
    arrayList.clear()
    if (order != null) {
      arrayList.addAll(order)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = arrayList[position]
    holder.binding?.mlSubCat?.animation =
      AnimationUtils.loadAnimation(activity, R.anim.mainfadein)
    holder.binding?.position = position
    holder.binding?.item = item.name
    holder.binding?.lySubColapsable?.item = item
    holder.binding?.click = onItemTapped
    holder.binding?.isExpanded = rowSelected == position
    if (rowSelected == position) {
      subSubCategoryAdapter = SubSubCategoryAdapter(activity, onSubItemTapped, item.name)
      holder.binding?.lySubColapsable?.rvSubSubCategory?.adapter = subSubCategoryAdapter
      subSubCategoryAdapter.setDataChanged(item.sections)
    }
//        if (position == rowIndex){
//            holder.binding?.mlSubCat?.setTransition(R.id.start, R.id.end)
//            holder.binding?.mlSubCat?.setTransitionDuration(300)
//            holder.binding?.mlSubCat?.transitionToStart()
//            holder.binding?.mlSubCat?.transitionToEnd()
//        }else{
//            holder.binding?.mlSubCat?.setTransition(R.id.end, R.id.start)
//            holder.binding?.mlSubCat?.setTransitionDuration(50)
//            holder.binding?.mlSubCat?.transitionToStart()
//            holder.binding?.mlSubCat?.transitionToEnd()
//        }
  }
//    fun setItems(items: ArrayList<Int>) {
//        selectedItems = items
//        notifyDataSetChanged()
//    }

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