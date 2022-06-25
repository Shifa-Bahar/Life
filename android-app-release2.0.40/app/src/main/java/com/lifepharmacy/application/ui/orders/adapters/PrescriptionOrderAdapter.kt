package com.lifepharmacy.application.ui.orders.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemOrdersPrescriptionBinding
import com.lifepharmacy.application.databinding.ItemReturnOrderBinding
import com.lifepharmacy.application.databinding.ItemReturnShipmentBinding
import com.lifepharmacy.application.model.ShipementModel
import com.lifepharmacy.application.model.orders.PrescriptionOrder
import com.lifepharmacy.application.model.orders.ReturnOrderModel
import com.lifepharmacy.application.model.product.ProductDetails


class PrescriptionOrderAdapter(
  context: Activity?,
  val onItemTapped: ClickPrescriptionOrderItem
) :
  RecyclerView.Adapter<PrescriptionOrderAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<PrescriptionOrder>? = ArrayList()
  var activity: Activity? = context

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemOrdersPrescriptionBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_orders_prescription,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemOrdersPrescriptionBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<PrescriptionOrder>?) {
//    arrayList?.clear()
    val oldSize = arrayList?.size ?: 0
//    var rangeRemove = 0
    if (order != null) {
      arrayList?.addAll(order)
      notifyItemRangeInserted(oldSize, order.size)
//      notifyDataSetChanged()
    }
//    if (order != null) {
//      arrayList?.addAll(order)
//    }
//    notifyDataSetChanged()
  }
  fun refreshData(list: ArrayList<PrescriptionOrder>) {
    arrayList?.clear()
    arrayList?.addAll(list)
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    holder.binding?.let { bind(it, position) }
  }

  private fun bind(binding: ItemOrdersPrescriptionBinding, position: Int) {
    val item = arrayList!![position]
    binding.item = item
    binding.click = onItemTapped
    binding.position = position
  }

}