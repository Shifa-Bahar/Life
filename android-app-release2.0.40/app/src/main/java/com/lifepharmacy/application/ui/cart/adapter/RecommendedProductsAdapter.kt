package com.lifepharmacy.application.ui.cart.adapter

import android.app.Activity
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.*
import com.lifepharmacy.application.model.*
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeProduct

class RecommendedProductsAdapter(context: Activity?, private val onItemTapped: ClickHomeProduct) :
  RecyclerView.Adapter<RecommendedProductsAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<ProductModel>? = ArrayList()
  var activity: Activity? = context


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemProductsBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_products,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemProductsBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<ProductModel>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    var item = arrayList!![position]
    holder.binding?.productModel = item
    holder.binding?.click = onItemTapped
    holder.binding?.tvOrignalPrice?.paintFlags
      ?.or(Paint.STRIKE_THRU_TEXT_FLAG)?.let {
        holder.binding?.tvOrignalPrice?.setPaintFlags(
          it
        )
      }
  }

}