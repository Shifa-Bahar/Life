package com.lifepharmacy.application.ui.products.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemProductAddOnsBinding
import com.lifepharmacy.application.model.product.ProductDetails

class AddOnProductAdapter(context: Activity, private val onItemTapped: AddOnProduct) :
  RecyclerView.Adapter<AddOnProductAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<ProductDetails>? = ArrayList()
  var activity: Activity = context

  var selectedItems = ArrayList<ProductDetails>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemProductAddOnsBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_product_add_ons,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemProductAddOnsBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<ProductDetails>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    var item = arrayList!![position]

    holder.binding?.position = position

    if (position == 0) {
      holder.binding?.imageViewAdd?.visibility = View.GONE
      holder.binding?.clMain?.background =
        ContextCompat.getDrawable(activity, R.drawable.white_with_left_round_corner)

    } else if (position == (arrayList?.lastIndex)) {
      holder.binding?.clMain?.background =
        ContextCompat.getDrawable(activity, R.drawable.white_with_right_round_corner)
      holder.binding?.imageViewAdd?.visibility = View.VISIBLE
    } else {
      holder.binding?.clMain?.setBackgroundColor(ContextCompat.getColor(activity, R.color.white))
      holder.binding?.imageViewAdd?.visibility = View.VISIBLE
    }
    holder.binding?.isChecked = selectedItems.contains(item)
//        if (position == (arrayList?.lastIndex)) {
//            holder.binding?.imageViewAddTwo?.visibility = View.GONE
//            holder.binding?.imageViewAdd?.visibility = View.GONE
//        }

    holder.binding?.item = item
    holder.binding?.click = onItemTapped
  }

  fun setItems(position: Int, items: ArrayList<ProductDetails>) {
    selectedItems = items
//        notifyDataSetChanged()
    notifyItemChanged(position)
  }
}