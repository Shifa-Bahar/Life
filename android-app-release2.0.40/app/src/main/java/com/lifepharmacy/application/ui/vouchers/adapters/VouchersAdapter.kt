package com.lifepharmacy.application.ui.vouchers.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemVouchersBinding
import com.lifepharmacy.application.model.vouchers.VoucherModel

class VouchersAdapter(context: Activity?, private val onItemTapped: ClickItemVoucher) :
  RecyclerView.Adapter<VouchersAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<VoucherModel>? = ArrayList()
  var activity: Activity? = context
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemVouchersBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_vouchers,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }

  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
//        return 8
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemVouchersBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<VoucherModel>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = arrayList!![position]
    holder.binding?.item = item
    holder.binding?.click = onItemTapped
  }

}