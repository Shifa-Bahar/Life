package com.lifepharmacy.application.ui.orders.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemOrderShipmentProductBinding
import com.lifepharmacy.application.model.ProductModel
import com.lifepharmacy.application.model.orders.OrderItem
import com.lifepharmacy.application.model.orders.SubOrderProductItem
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.ui.dashboard.adapter.ClickHomeProduct
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeInt
import kotlinx.coroutines.*

class ShipmentProductAdapter(
  context: Activity?,
  private val onItemTapped: ClickShipmentProduct,
  private val subOrderItem: Int,
  private val status: Int
) :
  RecyclerView.Adapter<ShipmentProductAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<SubOrderProductItem>? = ArrayList()
  var activity: Activity? = context

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemOrderShipmentProductBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_order_shipment_product,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }

  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemOrderShipmentProductBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<SubOrderProductItem>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = arrayList!![position]
    holder.binding?.let { bind(it, item) }
//        holder.binding?.checkBox?.setOnCheckedChangeListener { button, b ->
////            onItemTapped.onProductClicked()
//        }
  }

  private fun bind(binding: ItemOrderShipmentProductBinding, item: SubOrderProductItem) {
    binding.item = item
    binding.isDelivered = status == 5
    if (item.rating != null) {
      binding.simpleRatingBar.rating = item.rating ?: 0.0F
    } else {
      binding.simpleRatingBar.rating = 0.0F
    }
    binding.simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
      onItemTapped.onClickProductRating(
        item.productDetails.id,
        rating,
        subOrderItem.toString(),
        subOrderProductItem = item
      )

    }
  }

}