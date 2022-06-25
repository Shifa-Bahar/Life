package com.lifepharmacy.application.ui.orders.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemOrdersBinding
import com.lifepharmacy.application.databinding.ItemOrdersWithShipmentsBinding
import com.lifepharmacy.application.model.orders.OrderResponseModel
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.ui.cart.adapter.CartShipmentProductAdapter

class OrdersAdapter(
  context: Activity?,
  private val onItemTapped: ClickOrder,
  private val onClickSubShipment: ClickOrderShipmentItem
) :
  RecyclerView.Adapter<OrdersAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<OrderResponseModel>? = ArrayList()
  var activity: Activity? = context

  lateinit var orderProductsAdapter: OrdersShipmentsAdapter
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemOrdersWithShipmentsBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_orders_with_shipments,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemOrdersWithShipmentsBinding? = DataBindingUtil.bind(itemView)
  }

  fun setRefreshData(order: ArrayList<OrderResponseModel>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }

  fun setDataChanged(order: ArrayList<OrderResponseModel>?) {
    val oldSize = arrayList?.size ?: 0
    if (order != null) {
      arrayList?.addAll(order)
      notifyItemRangeInserted(oldSize, order.size)
    }
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = (arrayList ?: return)[position]
    holder.binding?.item = item
    holder.binding?.click = onItemTapped
    holder.binding?.transaction = item.payment
//    if (!item.transactions.isNullOrEmpty()) {
//      holder.binding?.transaction = item.transactions?.firstOrNull()
//    }
    orderProductsAdapter = OrdersShipmentsAdapter(activity, onClickSubShipment, item.id.toString())
    holder.binding?.rvShipments?.adapter = orderProductsAdapter
    orderProductsAdapter.setDataChanged(item.subOrders)
  }

}