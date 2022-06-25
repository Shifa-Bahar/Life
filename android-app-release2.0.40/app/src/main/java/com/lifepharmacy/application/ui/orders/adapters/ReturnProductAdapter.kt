package com.lifepharmacy.application.ui.orders.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemOrdersBinding
import com.lifepharmacy.application.databinding.ItemReturingProductsBinding
import com.lifepharmacy.application.databinding.ItemReturnReasonBinding
import com.lifepharmacy.application.model.orders.OrderItem
import com.lifepharmacy.application.model.orders.OrderResponseModel
import com.lifepharmacy.application.model.orders.SubOrderItem
import com.lifepharmacy.application.model.orders.SubOrderProductItem
import com.lifepharmacy.application.ui.orders.viewmodels.OrderDetailViewModel

class ReturnProductAdapter(context: Activity?, private val onItemTapped: ClickReturnProductItem,private val viewModel:OrderDetailViewModel) :
  RecyclerView.Adapter<ReturnProductAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<SubOrderProductItem>? = ArrayList()
  var activity: Activity? = context


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemReturingProductsBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_returing_products,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemReturingProductsBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(list: ArrayList<SubOrderProductItem>?) {
    arrayList?.clear()
    if (list != null) {
      arrayList?.addAll(list)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = arrayList!![position]
    holder.binding?.item = item
    holder.binding?.returnQty = viewModel.checkIfItAlreadyAddedQty(item)?:"0"
    holder.binding?.btnMinus?.setOnClickListener {
      var temp = holder.binding?.returnQty?.toInt() ?: 1
      temp -= 1
      holder.binding?.returnQty = temp.toString()
      onItemTapped.onClickMinus(orderModel = item, qty = temp)
      notifyItemChanged(position)
    }
    holder.binding?.btnPlus?.setOnClickListener {
      var temp = holder.binding?.returnQty?.toInt() ?: 0
      temp += 1
      holder.binding?.returnQty = temp.toString()
      onItemTapped.onClickPlus(orderModel = item, qty = temp)
      notifyItemChanged(position)
    }
  }

}