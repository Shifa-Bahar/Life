package com.lifepharmacy.application.ui.orders.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemShipmentDetailStatusBinding
import com.lifepharmacy.application.model.orders.SubOrderItem
import com.lifepharmacy.application.model.orders.shipment.TimeLine
import com.lifepharmacy.application.model.product.ProductDetails

class ShipmentStatusAdapter(
  context: Activity,
  var subOrderItem: SubOrderItem,
  val isDetailed: Boolean = false,
  val filter: Boolean = false
) :
  RecyclerView.Adapter<ShipmentStatusAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<TimeLine>? = ArrayList()
  var activity: Activity = context

  var selectedItems = ArrayList<ProductDetails>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemShipmentDetailStatusBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_shipment_detail_status,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemShipmentDetailStatusBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(list: ArrayList<TimeLine>?) {
    arrayList?.clear()
    if (!list.isNullOrEmpty()) {
      if (filter) {
        if (subOrderItem.status == 0 || subOrderItem.status == 5 || subOrderItem.status == 6) {
          arrayList?.add(list.first())
          if (subOrderItem.status == 5 || subOrderItem.status == 0) {
            arrayList?.add(list[list.lastIndex - 1])
          }
          if (subOrderItem.status == 6) {
            arrayList?.add(list.last())
          }
        } else {
          arrayList?.add(list.first())
          if (list.last().happened != true) {
            val current = list.lastOrNull { item -> item.happened == true }
            current?.let {
              if (current != list.first()) {
                arrayList?.add(current)
              }
            }
          }
          val temp = list[list.lastIndex - 1]
          temp.timestamp = subOrderItem.expectedDate
          arrayList?.add(temp)
        }

      } else {
        if (subOrderItem.status != 6 && subOrderItem.status != 5) {
          list[list.lastIndex - 1].timestamp = subOrderItem.expectedDate
          arrayList?.addAll(list.subList(0,list.lastIndex))
        }else{
          if (subOrderItem.status == 6){
            arrayList?.addAll(list)
          }else{
            arrayList?.addAll(list.subList(0,list.lastIndex))
          }
        }
      }

    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = arrayList!![position]
    holder.binding?.let { bind(it, item, position) }
  }

  private fun bind(binding: ItemShipmentDetailStatusBinding, item: TimeLine, position: Int) {
    binding.isDetail = isDetailed
    binding.item = item
    binding.subOrderItem = subOrderItem

    if (filter){
      binding.isDeliveryStatus = position == arrayList?.lastIndex
    }else{
      if (subOrderItem.status == 6){
        binding.isDeliveryStatus = position == (arrayList?.lastIndex?.minus(1))
        binding.isStatusShipment = position == (arrayList?.lastIndex?.minus(2))
      }else{
        binding.isDeliveryStatus = position == arrayList?.lastIndex
        binding.isStatusShipment = position == (arrayList?.lastIndex?.minus(1))
      }

      val keyValueAdapter = KeyAdapterAdapter(activity)
      binding.rvKey.adapter = keyValueAdapter
      keyValueAdapter.setDataChanged(item.data)

    }
    if (position == (arrayList?.lastIndex)) {
      binding.isLast = true
    }
  }

}