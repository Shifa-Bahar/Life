package com.lifepharmacy.application.ui.cart.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemCategoryBinding
import com.lifepharmacy.application.databinding.ItemDeliverySlotBinding
import com.lifepharmacy.application.model.category.CategoryMainModel
import com.lifepharmacy.application.model.category.RootCategory
import com.lifepharmacy.application.model.config.DeliverySlot

class DeliverSlotsAdapter(context: Activity?, private val onItemTapped: ClickDeliverySlot) :
  RecyclerView.Adapter<DeliverSlotsAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<DeliverySlot> = ArrayList()
  var activity: Activity? = context
  var rowSelected = -1
  var oldSelection = -1
  var isAnySelected: Boolean = false
  var firstActivePosition: Int = -1
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemDeliverySlotBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_delivery_slot,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return arrayList.size
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemDeliverySlotBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<DeliverySlot>?) {
    arrayList.clear()
    if (order != null) {
      arrayList.addAll(order)
    }
    val anySelected = arrayList.firstOrNull {
      it.selected == true
    }
    val firstActive = arrayList.firstOrNull { it.isActive == true }
    if (firstActive != null) {
      firstActivePosition = arrayList.indexOf(firstActive)
    }
    isAnySelected = anySelected != null
    notifyDataSetChanged()
  }

  fun clearSlots() {
    arrayList.clear()
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

    holder.binding?.let { bindSlots(it, position) }
//    val item = arrayList[position]
//    holder.binding?.position = position
//    holder.binding?.item = item
//    holder.binding?.click = onItemTapped
//    if (rowSelected == -1) {
//      if (isAnySelected) {
//        if (item.selected == true && item.isActive == true) {
//          holder.binding?.isSelected = true
//          onItemTapped.onClickSlot(item, position)
//        } else {
//          holder.binding?.isSelected = false
//        }
////        holder.binding?.isSelected = item.selected == true && item.isActive == true
//        if (item.selected == true && item.isActive == true) {
//          oldSelection = position
//        }
//
//      } else {
//        if (position == firstActivePosition) {
//          holder.binding?.isSelected = true
//          onItemTapped.onClickSlot(item, position)
//          oldSelection = position
//        } else {
//          holder.binding?.isSelected = false
//        }
//      }
//    } else {
//      if (position == rowSelected && item.isActive == true) {
//        holder.binding?.isSelected = true
//        onItemTapped.onClickSlot(item, position)
//        oldSelection = position
//      } else {
//        holder.binding?.isSelected = false
//      }
//    }
//    holder.binding?.isLast = arrayList.lastIndex == position

  }

  fun setItemSelected(position: Int) {
    rowSelected = position
    notifyItemChanged(oldSelection)
    notifyItemChanged(rowSelected)
    oldSelection = rowSelected
  }

  private fun bindSlots(binding: ItemDeliverySlotBinding, position: Int) {
    val item = arrayList[position]
    binding.position = position
    binding.item = item
    binding.click = onItemTapped
    if (rowSelected == -1) {
      if (isAnySelected) {
        binding.isSelected = item.selected == true && item.isActive == true
//        holder.binding?.isSelected = item.selected == true && item.isActive == true
        if (item.selected == true && item.isActive == true) {
          oldSelection = position
        }

      } else {
        if (position == firstActivePosition) {
          binding.isSelected = true
          oldSelection = position
        } else {
          binding.isSelected = false
        }
      }
    } else {
      if (position == rowSelected && item.isActive == true) {
        binding.isSelected = true
        oldSelection = position
      } else {
        binding.isSelected = false
      }
    }
    binding.isLast = arrayList.lastIndex == position
  }
}