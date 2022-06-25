package com.lifepharmacy.application.ui.card

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemCardBinding
import com.lifepharmacy.application.model.payment.CardMainModel
import com.lifepharmacy.application.ui.utils.dailogs.AnimationDialog.Companion.TAG

import kotlin.collections.ArrayList

class CardsAdapter(context: Activity?, private val onItemTapped: ClickCard) :
  RecyclerView.Adapter<CardsAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<CardMainModel>? = ArrayList()
  private var activity: Activity? = context
  var selectedcardfirst: Boolean = false
  var rowIndex = -1
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemCardBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_card,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemCardBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(list: ArrayList<CardMainModel>?) {
    arrayList?.clear()
    if (list != null) {
      arrayList?.addAll(list)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = arrayList!![position]
    holder.binding?.item = item
    holder.binding?.position = position
    holder.binding?.click = onItemTapped
    if (!selectedcardfirst) {
      holder.binding?.selected = false
      selectedcardfirst = true
    } else {
      holder.binding?.selected = rowIndex == position
    }
//    holder.binding?.selected = false

    holder.binding?.pg = item.pg!!

//    holder.binding?.rgCard?.setOnClickListener {
//      onItemTapped.onCardSelect(position, item)
//      holder.binding!!.rgCard.setChecked(true)
//    }

  }

  fun selectedItem(position: Int?) {
    if (position != null) {
      rowIndex = position
      notifyDataSetChanged()
    }

  }

}