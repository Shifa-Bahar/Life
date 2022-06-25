package com.lifepharmacy.application.ui.address.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemAddressTypeBinding
import com.lifepharmacy.application.databinding.ItemTimeTypeBinding
import com.lifepharmacy.application.model.address.AddressTypeModel

import kotlin.collections.ArrayList

class TimeTypeTypeAdapter(context: Activity, private val onItemTapped: ClickItemAddressType) :
    RecyclerView.Adapter<TimeTypeTypeAdapter.ItemViewHolder>() {
    private var arrayList: ArrayList<AddressTypeModel>? = ArrayList()
    private var activity: Activity = context
    private var rowIndex = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ItemTimeTypeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.item_time_type,
            parent, false
        )
        return ItemViewHolder(binding.root)
    }


    override fun getItemCount(): Int {
        return if (null != arrayList) arrayList!!.size else 0
    }

    class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemTimeTypeBinding? = DataBindingUtil.bind(itemView)
    }

    fun setDataChanged(list: ArrayList<AddressTypeModel>?) {
        arrayList?.clear()
        if (list != null) {
            arrayList?.addAll(list)
        }
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var item = arrayList!![position]
        holder.binding?.item = item
        holder.binding?.click = onItemTapped

    }


}