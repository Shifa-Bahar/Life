package com.lifepharmacy.application.ui.transactions.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemWalletTransactionBinding
import com.lifepharmacy.application.model.payment.TransactionMainModel

class WalletTransactionsAdapter(context: Activity?, private val onItemTapped: ClickTransaction) :
  RecyclerView.Adapter<WalletTransactionsAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<TransactionMainModel>? = ArrayList()
  var activity: Activity? = context

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemWalletTransactionBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_wallet_transaction,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemWalletTransactionBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(items: ArrayList<TransactionMainModel>?) {
    arrayList?.clear()
    if (items != null) {
      arrayList?.addAll(items)
    }
    notifyDataSetChanged()
  }

  fun setNewData(order: ArrayList<TransactionMainModel>?) {
    var oldSize = arrayList?.size ?: 0
    if (order != null) {
      arrayList?.addAll(order)
      notifyItemRangeInserted(oldSize, order.size)
    }
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    var item = arrayList!![position]

    holder.binding?.item = item
    holder.binding?.click = onItemTapped

  }

}