package com.lifepharmacy.application.ui.orders.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemOrdersShipmentsProductBinding
import com.lifepharmacy.application.databinding.ItemShipmentProductBinding
import com.lifepharmacy.application.model.cart.CartModel
import com.lifepharmacy.application.model.orders.OrderItem
import com.lifepharmacy.application.model.orders.SubOrderProductItem

class OrdersShipmentsProductAdapter(context: Activity?) :
  RecyclerView.Adapter<OrdersShipmentsProductAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<SubOrderProductItem>? = ArrayList()
  var activity: Activity? = context


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemOrdersShipmentsProductBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_orders_shipments_product,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemOrdersShipmentsProductBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<SubOrderProductItem>?) {
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
    var item = arrayList!![position]

    holder.binding?.item = item

  }

}