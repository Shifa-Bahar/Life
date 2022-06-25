package com.lifepharmacy.application.ui.dashboard.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.*
import com.lifepharmacy.application.model.blog.BlogModel
import com.lifepharmacy.application.model.notifications.InAppNotificationMainModel
import com.lifepharmacy.application.model.notifications.StickyMessageModel
import java.lang.Exception

import kotlin.collections.ArrayList

class StickyMessageAdapter(context: Activity, val onClickStickyMessage: ClickStickyMessage) :
  RecyclerView.Adapter<StickyMessageAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<StickyMessageModel>? = ArrayList()
  var activity: Activity = context


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemStickMessageBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_stick_message,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
//    return 3
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemStickMessageBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<StickyMessageModel>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    holder.binding?.let { bindView(it, position) }
  }

  private fun bindView(binding: ItemStickMessageBinding, position: Int) {
    var item = arrayList?.get(position)
    binding.item = item
    binding.postion = position
    binding.click = onClickStickyMessage
  }

}