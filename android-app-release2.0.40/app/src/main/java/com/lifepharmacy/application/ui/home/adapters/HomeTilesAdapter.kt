package com.lifepharmacy.application.ui.home.adapters

import android.app.Activity
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.*
import com.lifepharmacy.application.model.*
import com.lifepharmacy.application.model.config.Title
import com.lifepharmacy.application.utils.universal.Extensions.intToNullSafeDouble
import kotlin.math.roundToInt


class HomeTilesAdapter(
  context: Activity,
  var textColor: String,
  var filView: Boolean = false,
) : RecyclerView.Adapter<HomeTilesAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<Title>? = ArrayList()
  var activity: Activity? = context
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemHomeTileBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_home_tile,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemHomeTileBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(list: ArrayList<Title>?) {
    arrayList?.clear()
    if (list != null) {
      arrayList?.addAll(list)
    }
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val item = arrayList!![position]
    holder.binding?.item = item
    holder.binding?.tvTitle?.setTextColor(Color.parseColor(textColor))
    when {
      filView -> {
        val layoutParams = holder.binding?.clMain?.layoutParams
        layoutParams?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        holder.binding?.clMain?.layoutParams = layoutParams
      }
      (arrayList ?: return).size == 3 -> {
        when (position) {
          0 -> {
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.getDefaultDisplay()?.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels.toDouble()
            val layoutParams = holder.binding?.clMain?.layoutParams
            layoutParams?.width = (screenWidth * 0.4).roundToInt()
            layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            holder.binding?.clMain?.layoutParams = layoutParams
          }
          1 -> {
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.getDefaultDisplay()?.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels.intToNullSafeDouble()
            val layoutParams = holder.binding?.clMain?.layoutParams
            layoutParams?.width = (screenWidth * 0.3).roundToInt()
            layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            holder.binding?.clMain?.layoutParams = layoutParams
          }
          2 -> {
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.getDefaultDisplay()?.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels.intToNullSafeDouble()
            val layoutParams = holder.binding?.clMain?.layoutParams
            layoutParams?.width = (screenWidth * 0.3).roundToInt()
            layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            holder.binding?.clMain?.layoutParams = layoutParams

          }
        }
      }
      else -> {
        val layoutParams = holder.binding?.clMain?.layoutParams
        layoutParams?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        holder.binding?.clMain?.layoutParams = layoutParams
      }
    }
  }
}