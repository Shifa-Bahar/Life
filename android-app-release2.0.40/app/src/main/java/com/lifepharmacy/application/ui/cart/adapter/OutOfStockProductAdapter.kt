package com.lifepharmacy.application.ui.cart.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemCartDetailProductBinding
import com.lifepharmacy.application.databinding.ItemCartOutOfStockBinding
import com.lifepharmacy.application.model.cart.CartModel

import kotlin.collections.ArrayList

class OutOfStockProductAdapter(
  var context: Activity?,
  var onClick: ClickOutOfStock
) : RecyclerView.Adapter<OutOfStockProductAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<CartModel>? = ArrayList()
  var activity: Activity? = context
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemCartOutOfStockBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_cart_out_of_stock,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemCartOutOfStockBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<CartModel>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = arrayList!![position]
    holder.binding?.item = item
    holder.binding?.position = position
    holder.binding?.click  = onClick

  }
}