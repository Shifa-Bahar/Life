package com.lifepharmacy.application.ui.card

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemWalletCardBinding
import com.lifepharmacy.application.model.payment.CardMainModel

class WalletCardsAdapter(
  context: Activity?,
  private val onItemTapped: ClickCard,
  private val type: String = "Wallet"
) :
  RecyclerView.Adapter<WalletCardsAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<CardMainModel>? = ArrayList()
  var activity: Activity? = context

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemWalletCardBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_wallet_card,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemWalletCardBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<CardMainModel>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    var item = arrayList!![position]

    holder.binding?.item = item
    holder.binding?.click = onItemTapped
    holder.binding?.type = type
    holder.binding?.pg = item.pg?.toInt()!!

  }

}