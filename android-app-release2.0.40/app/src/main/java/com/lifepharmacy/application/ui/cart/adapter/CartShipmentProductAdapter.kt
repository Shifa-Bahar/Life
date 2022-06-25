package com.lifepharmacy.application.ui.cart.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemShipmentProductBinding
import com.lifepharmacy.application.model.cart.CartModel

class CartShipmentProductAdapter(context: Activity?) :
  RecyclerView.Adapter<CartShipmentProductAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<CartModel>? = ArrayList()
  var firstArray: ArrayList<CartModel>? = ArrayList()
  var bothProductsArray: ArrayList<CartModel>? = ArrayList()
  var isAddBothArrays = false
  var activity: Activity? = context


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemShipmentProductBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_shipment_product,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemShipmentProductBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<CartModel>?) {
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