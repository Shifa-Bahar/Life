package com.lifepharmacy.application.ui.pcr.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemOrdersBinding
import com.lifepharmacy.application.databinding.ItemOrdersWithShipmentsBinding
import com.lifepharmacy.application.databinding.ItemPcrAppoinmnetListBinding
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.model.orders.OrderResponseModel
import com.lifepharmacy.application.model.orders.PrescriptionOrder
import com.lifepharmacy.application.model.pcr.appoinmentlist.AppoinmentList
import com.lifepharmacy.application.model.pcr.pcrlist.Products
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.model.product.ProductReview
import com.lifepharmacy.application.model.vouchers.VoucherModel
import com.lifepharmacy.application.ui.cart.adapter.CartShipmentProductAdapter
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.ui.rewards.adapters.ClickItemRewards
import com.lifepharmacy.application.utils.DateTimeUtil

class AppoinmentAdapter(
  context: Activity?,
  private val onClickAppoinmentDetails: ClickAppoinmentDetails,
  private val appManager: AppManager
) :
  RecyclerView.Adapter<AppoinmentAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<AppoinmentList>? = ArrayList()
  var activity: Activity? = context
  var id: Int = 0
//  lateinit var orderProductsAdapter: OrdersShipmentsAdapter
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemPcrAppoinmnetListBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_pcr_appoinmnet_list,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemPcrAppoinmnetListBinding? = DataBindingUtil.bind(itemView)
  }


  fun refreshData(list: ArrayList<AppoinmentList>) {
    arrayList?.clear()
    arrayList?.addAll(list)
    notifyDataSetChanged()
  }


  fun setDataChanged(order: ArrayList<AppoinmentList>?) {
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


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

    val item = arrayList!![position]
//    val item = (arrayList ?: return)[position]
    holder.binding?.item = item
//    holder.binding?.click = onItemTapped
//    holder.binding?.tvDate?.text = ""
    holder.binding?.click = onClickAppoinmentDetails

    id = (holder.binding?.item?.id ?: return)
    Log.e(TAG, "onBindViewHolder:appoinment $id")


    holder.binding?.tvHead?.text=item.collection.name
//    holder.binding?.tvStatus?.text = item.status_label

    holder.binding?.tvApp?.text = "Appointment ID #${item.id}"
    var datemonth: String? =
      DateTimeUtil.getDayDAteFromStringWithoutYeRTimeZone(item.created_at)
    holder.binding?.tvPlacedon?.text = "Booked on $datemonth"

    var validity: String? =
      DateTimeUtil.getDayDAteFromStringWithoutTimeZone(item.start_time)
    holder.binding?.tvDate?.text = "Date:$validity"

    var start_time: String? =
      DateTimeUtil.getStartendtimeFromStringWithoutTimeZone(item.start_time)
    var end_time: String? =
      DateTimeUtil.getStartendtimeFromStringWithoutTimeZone(item.end_time)
    holder.binding?.tvStartTime?.text = "Time:$start_time- $end_time"

    holder.binding?.tvTotal?.text = " AED ${item.total}"


//    holder.binding?.transaction = item.payment
//    if (!item.transactions.isNullOrEmpty()) {
//      holder.binding?.transaction = item.transactions?.firstOrNull()
//    }
//    orderProductsAdapter = OrdersShipmentsAdapter(activity, onClickSubShipment, item.id.toString())
//    holder.binding?.rvShipments?.adapter = orderProductsAdapter
//    orderProductsAdapter.setDataChanged(item.subOrders)
  }

}