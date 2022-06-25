package com.lifepharmacy.application.ui.dashboard.adapter

import android.app.Activity
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.*
import com.lifepharmacy.application.model.*
import com.lifepharmacy.application.model.home.SectionData
import com.lifepharmacy.application.utils.universal.Extensions.intToNullSafeDouble

import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class PromotionsAdapter(context: Activity?, private val onItemTapped: ClickHomeSubItem) :
  RecyclerView.Adapter<PromotionsAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<SectionData>? = ArrayList()
  var activity: Activity? = context


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemTrendsBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_trends,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemTrendsBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(order: ArrayList<SectionData>?) {
    arrayList?.clear()
    if (order != null) {
      arrayList?.addAll(order)
    }
    notifyDataSetChanged()
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val displayMetrics = DisplayMetrics()
    activity?.windowManager?.getDefaultDisplay()?.getMetrics(displayMetrics)
    val screenWidth = displayMetrics.widthPixels.intToNullSafeDouble()
    val withPers = screenWidth * 0.35
    holder.binding?.clMain?.layoutParams?.width = withPers.roundToInt()
    holder.binding?.clMain?.layoutParams?.height = (withPers * 0.88).roundToInt()
    val item = arrayList!![position]
    holder.binding?.clMain?.animation =
      AnimationUtils.loadAnimation(activity, R.anim.mainfadein)
    holder.binding?.item = item
    holder.binding?.click = onItemTapped
  }

}