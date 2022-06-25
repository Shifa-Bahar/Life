package com.lifepharmacy.application.ui.orders.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemReturnReasonBinding

import kotlin.collections.ArrayList

class ReturnReasonAdapter(
  context: Activity?,
  private val onItemTaped: ClickReturnReason
) : RecyclerView.Adapter<ReturnReasonAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<String>? = ArrayList()
  var activity: Activity? = context
  var rowSelected = -1
  var oldSelection = -1
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemReturnReasonBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_return_reason,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemReturnReasonBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(list: ArrayList<String>?) {
    arrayList?.clear()
    if (list != null) {
      arrayList?.addAll(list)
    }
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    var item = arrayList!![position]
    holder.binding?.type = item
    holder.binding?.position = position
    holder.binding?.click = onItemTaped
    holder.binding?.isChecked = rowSelected == position

  }

  fun setItemSelected(position: Int) {
    rowSelected = position
    notifyItemChanged(rowSelected)
    if (oldSelection != -1) {
      notifyItemChanged(oldSelection)
    }
    oldSelection = rowSelected
  }
}