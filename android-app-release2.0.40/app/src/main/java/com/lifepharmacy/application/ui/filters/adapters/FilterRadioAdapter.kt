package com.lifepharmacy.application.ui.filters.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemFilterRadioBinding
import com.lifepharmacy.application.model.filters.FilterModel

class FilterRadioAdapter(context: Activity,private val type:String,private val parentPosition:Int,private val onItemTapped: ClickFilterRadio) :
    RecyclerView.Adapter<FilterRadioAdapter.ItemViewHolder>() {
    var arrayList: ArrayList<FilterModel>? = ArrayList()
    var activity: Activity = context
    var rowSelected = -1
    var oldSelection = -1
    var TAG = "FilterRadioAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ItemFilterRadioBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.item_filter_radio,
            parent, false
        )
        return ItemViewHolder(binding.root)
    }


    override fun getItemCount(): Int {
        return if (null != arrayList) arrayList!!.size else 0
    }

    class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemFilterRadioBinding? = DataBindingUtil.bind(itemView)
    }

    fun setDataChanged(order: ArrayList<FilterModel>?) {
        arrayList?.clear()
        if (order != null) {
            arrayList?.addAll(order)
        }
        notifyDataSetChanged()
    }
    fun replaceData(list: ArrayList<FilterModel>?){
        arrayList?.clear()
        if (list != null) {
            arrayList?.addAll(list)
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = arrayList!![position]
        holder.binding?.item = item
        holder.binding?.position = position
        holder.binding?.type = type
        holder.binding?.click = onItemTapped
        if (oldSelection == position){
            holder.binding?.oldItem = item
        }
        if(item.title != null){
            holder.binding?.title = item.title
        }else{
            holder.binding?.title = item.name
        }
        holder.binding?.parentPosition = parentPosition



    }
    fun setItemSelected(position: Int){
        rowSelected = position
        notifyItemChanged(rowSelected)
        if (oldSelection == -1){
            notifyDataSetChanged()
        }else{
            notifyItemChanged(oldSelection)
        }
        oldSelection = rowSelected
    }
}