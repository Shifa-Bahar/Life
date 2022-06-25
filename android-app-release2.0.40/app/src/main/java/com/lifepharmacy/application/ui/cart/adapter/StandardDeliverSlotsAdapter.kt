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
import com.lifepharmacy.application.databinding.ItemStandardDeliverySlotBinding
import com.lifepharmacy.application.model.category.CategoryMainModel
import com.lifepharmacy.application.model.category.RootCategory
import com.lifepharmacy.application.model.config.DeliverySlot

class StandardDeliverSlotsAdapter(context: Activity?, private val onItemTapped: ClickStandardDeliverySlot) :
  RecyclerView.Adapter<StandardDeliverSlotsAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<DeliverySlot> = ArrayList()
  var activity: Activity? = context
  var rowSelected = -1
  var oldSelection = -1
  var isAnySelected: Boolean = false
  var firstActivePosition: Int = -1
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemStandardDeliverySlotBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_standard_delivery_slot,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return arrayList.size
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemStandardDeliverySlotBinding? = DataBindingUtil.bind(itemView)
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
    val item = arrayList[position]
    holder.binding?.position = position
    holder.binding?.item = item
    holder.binding?.click = onItemTapped
    if (rowSelected == -1) {
      if (isAnySelected) {
        holder.binding?.isSelected = item.selected == true && item.isActive == true
        if (item.selected == true && item.isActive == true) {
          oldSelection = position
        }

      } else {
        holder.binding?.isSelected = position == firstActivePosition
        if (position == firstActivePosition) {
          oldSelection = position
        }
      }
    } else {
      holder.binding?.isSelected = position == rowSelected && item.isActive == true
    }
    holder.binding?.isLast = arrayList.lastIndex == position

  }

  fun setItemSelected(position: Int) {
    rowSelected = position
    notifyItemChanged(oldSelection)
    notifyItemChanged(rowSelected)
    oldSelection = rowSelected
  }
}