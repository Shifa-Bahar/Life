package com.lifepharmacy.application.ui.dashboard.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemDiscountsBinding
import com.lifepharmacy.application.model.home.SectionData

import kotlin.collections.ArrayList

class DiscountAdapter(context: Activity?, private val onItemTapped: ClickHomeSubItem) :
    RecyclerView.Adapter<DiscountAdapter.ItemViewHolder>() {
    var arrayList: ArrayList<SectionData>? = ArrayList()
    var activity: Activity? = context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ItemDiscountsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.item_discounts,
            parent, false
        )
        return ItemViewHolder(binding.root)
    }


    override fun getItemCount(): Int {
        return if (null != arrayList) arrayList!!.size else 0
    }

    class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemDiscountsBinding? = DataBindingUtil.bind(itemView)
    }

    fun setDataChanged(order: ArrayList<SectionData>?) {
        arrayList?.clear()
        if (order != null) {
            arrayList?.addAll(order)
        }
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = arrayList!![position]
      holder.binding?.clMain?.animation =
        AnimationUtils.loadAnimation(activity, R.anim.mainfadein)
        holder.binding?.item = item
        holder.binding?.click = onItemTapped
    }

}