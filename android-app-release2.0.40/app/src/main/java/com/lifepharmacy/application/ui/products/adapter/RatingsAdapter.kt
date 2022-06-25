package com.lifepharmacy.application.ui.products.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemRatingBinding
import com.lifepharmacy.application.model.RatingModel

class RatingsAdapter(context: Activity?, private val onItemTapped: Ratings) :
    RecyclerView.Adapter<RatingsAdapter.ItemViewHolder>() {
    var arrayList: ArrayList<RatingModel>? = ArrayList()
    var activity: Activity? = context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ItemRatingBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.item_rating,
            parent, false
        )
        return ItemViewHolder(binding.root)
    }


    override fun getItemCount(): Int {
        return if (null != arrayList) arrayList!!.size else 0
    }

    class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemRatingBinding? = DataBindingUtil.bind(itemView)
    }

    fun setDataChanged(order: ArrayList<RatingModel>?) {
        arrayList?.clear()
        if (order != null) {
            arrayList?.addAll(order)
        }
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var item = arrayList!![position]

        holder.binding?.rating = item
        holder.binding?.click = onItemTapped
    }

}