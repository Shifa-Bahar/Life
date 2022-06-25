package com.lifepharmacy.application.ui.orders.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemOrdersShipmentsBinding
import com.lifepharmacy.application.databinding.ItemOrdersShipmentsProductBinding
import com.lifepharmacy.application.databinding.ItemShipmentProductBinding
import com.lifepharmacy.application.model.cart.CartModel
import com.lifepharmacy.application.model.orders.OrderItem
import com.lifepharmacy.application.model.orders.SubOrderItem
import com.lifepharmacy.application.model.orders.SubOrderProductItem

class OrdersShipmentsAdapter(
  context: Activity?,
  val onItemClick: ClickOrderShipmentItem,
  val orderID: String
) :
  RecyclerView.Adapter<OrdersShipmentsAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<SubOrderItem>? = ArrayList()
  var activity: Activity? = context


  lateinit var orderProductsAdapter: OrdersShipmentsProductAdapter
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemOrdersShipmentsBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_orders_shipments,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemOrdersShipmentsBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<SubOrderItem>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }
//  fun setBothProducts(list:ArrayList<CartModel>?){
//    bothProductsArray?.clear()
//    if (list != null) {
//      bothProductsArray?.addAll(list)
//    }
//  }
//  fun setMergerBoolean(mergerBoth:Boolean){
//    isAddBothArrays = mergerBoth
//    notifyDataChanged()
//  }
//  fun notifyDataChanged(){
//    if (isAddBothArrays){
//      firstArray?.let { arrayList?.addAll(it) }
//      bothProductsArray?.let { arrayList?.addAll(it) }
//    }else{
//      firstArray?.let { arrayList?.addAll(it) }
//    }
//
//    notifyDataSetChanged()
//  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    holder.binding?.let { bindView(it, position) }
  }

  fun bindView(binding: ItemOrdersShipmentsBinding, position: Int) {
    val item = (arrayList ?: return)[position]
    binding.item = item
    binding.click = onItemClick
    binding.orderID = orderID
    orderProductsAdapter = OrdersShipmentsProductAdapter(activity)
    binding.rvProducts.adapter = orderProductsAdapter
    orderProductsAdapter.setDataChanged(item.items)
    binding.simpleRatingBar.rating = item.rating ?: 0.0F
    if (item?.rating != null && item?.rating != 0.0F) {
      binding.simpleRatingBar.rating =
        item?.rating ?: 0.0F
      //CommentedForTesting
//      binding.simpleRatingBar.isClickable = false;
//      binding.simpleRatingBar.isScrollable = false;
//      binding.simpleRatingBar.isEnabled = false
      ////END////
    } else {
      binding.simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
        onItemClick.onClickRating(item, orderId = orderID, rating)
      }
    }
    //TESTING
    binding.simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
      onItemClick.onClickRating(item, orderId = orderID, rating)
    }
    ////END////
    binding.simpleRatingBar.setOnClickListener {
      onItemClick.onClickRating(item, orderId = orderID, binding.simpleRatingBar.rating)
    }
  }
}