package com.lifepharmacy.application.ui.orders.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemOrdersProductBinding
import com.lifepharmacy.application.model.orders.OrderItem

import kotlin.collections.ArrayList

class OrderProductsAdapter(
  context: Activity?,
) : RecyclerView.Adapter<OrderProductsAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<OrderItem>? = ArrayList()
  var activity: Activity? = context
  var rowSelected = -1
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemOrdersProductBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_orders_product,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemOrdersProductBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<OrderItem>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    var item = arrayList!![position]
    holder.binding?.item = item

  }
}