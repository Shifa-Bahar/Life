package com.lifepharmacy.application.ui.cart.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemDelivoryOptionBinding
import com.lifepharmacy.application.model.DeliveryOptionModel

import kotlin.collections.ArrayList

class DeliveryOptionsAdapter(context: Activity?) : RecyclerView.Adapter<DeliveryOptionsAdapter.ItemViewHolder>() {
    var arrayList: ArrayList<DeliveryOptionModel>? = ArrayList()
    var activity: Activity? = context

    lateinit var deliveryItemAdapter:DeliveryOptionsItemsAdapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ItemDelivoryOptionBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.item_delivory_option,
            parent, false
        )
        return ItemViewHolder(binding.root)
    }



    override fun getItemCount(): Int {
        return if (null != arrayList) arrayList!!.size else 0
    }

    class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemDelivoryOptionBinding? = DataBindingUtil.bind(itemView)
    }
    fun setDataChanged(order: ArrayList<DeliveryOptionModel>?) {
        arrayList?.clear()
        if (order != null) {
            arrayList?.addAll(order)
        }
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var item = arrayList!![position]
        holder.binding?.item = item
        deliveryItemAdapter = DeliveryOptionsItemsAdapter(activity)
        holder.binding?.rvDeliveryItems?.adapter = deliveryItemAdapter
        deliveryItemAdapter.setDataChanged(item.listItem)
    }

}