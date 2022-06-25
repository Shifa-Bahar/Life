package com.lifepharmacy.application.ui.orders.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemShipmentDetailStatusBinding
import com.lifepharmacy.application.databinding.ItemShipmentKeyValueBinding
import com.lifepharmacy.application.model.orders.SubOrderItem
import com.lifepharmacy.application.model.orders.shipment.KeyValueModel
import com.lifepharmacy.application.model.orders.shipment.TimeLine
import com.lifepharmacy.application.model.product.ProductDetails
import okhttp3.internal.waitMillis

class KeyAdapterAdapter(
  context: Activity,
) :
  RecyclerView.Adapter<KeyAdapterAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<KeyValueModel>? = ArrayList()
  var activity: Activity = context

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemShipmentKeyValueBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_shipment_key_value,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemShipmentKeyValueBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(list: ArrayList<KeyValueModel>?) {
    arrayList?.clear()
    if (!list.isNullOrEmpty()) {
      arrayList?.addAll(list)

    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = arrayList!![position]
    holder.binding?.let { bind(it, item, position) }
  }

  private fun bind(binding: ItemShipmentKeyValueBinding, item: KeyValueModel, position: Int) {
    binding.item = item

  }

}