package com.lifepharmacy.application.ui.cart.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemDelivoryItemBinding
import com.lifepharmacy.application.model.DeliveryOptionItemModel

import kotlin.collections.ArrayList

class DeliveryOptionsItemsAdapter(context: Activity?) : RecyclerView.Adapter<DeliveryOptionsItemsAdapter.ItemViewHolder>() {
    var arrayList: ArrayList<DeliveryOptionItemModel>? = ArrayList()
    var activity: Activity? = context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ItemDelivoryItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.item_delivory_item,
            parent, false
        )
        return ItemViewHolder(binding.root)
    }



    override fun getItemCount(): Int {
        return if (null != arrayList) arrayList!!.size else 0
    }

    class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemDelivoryItemBinding? = DataBindingUtil.bind(itemView)
    }
    fun setDataChanged(order: ArrayList<DeliveryOptionItemModel>?) {
        arrayList?.clear()
        if (order != null) {
            arrayList?.addAll(order)
        }
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var item = arrayList!![position]
        holder.binding?.item = item
    }

}