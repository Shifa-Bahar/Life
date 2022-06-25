package com.lifepharmacy.application.ui.dashboard.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemBottomIconBinding
import com.lifepharmacy.application.model.BottomBarModel

class BottomAdapter(context: Activity?, private val onItemTapped: ClickBottomBar) :
    RecyclerView.Adapter<BottomAdapter.ItemViewHolder>() {
    var arrayList: ArrayList<BottomBarModel>? = ArrayList()
    var activity: Activity? = context
    var rowSelected = 0
    var oldSelection = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ItemBottomIconBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.item_bottom_icon,
            parent, false
        )
        return ItemViewHolder(binding.root)
    }


    override fun getItemCount(): Int {
        return if (null != arrayList) arrayList!!.size else 0
    }

    class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemBottomIconBinding? = DataBindingUtil.bind(itemView)
    }

    fun setDataChanged(order: ArrayList<BottomBarModel>?) {
        arrayList?.clear()
        if (order != null) {
            arrayList?.addAll(order)
        }
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var item = arrayList!![position]
        holder.binding?.position = position
        holder.binding?.click = onItemTapped
        holder.binding?.tvTitle?.text= item.name
//        holder.binding?.animationView?.setAnimation(item.selectedAnimation)
//        if (oldSelection == position){
//            holder.binding?.animationView?.setAnimation(item.unselectedAnimation)
//            holder.binding?.animationView?.playAnimation()
//        }
        if (rowSelected == position){
            holder.binding?.animationView?.setAnimation(item.selectedAnimation)
            holder.binding?.animationView?.playAnimation()
        }else if (oldSelection == position) {
            holder.binding?.animationView?.setAnimation(item.unselectedAnimation)
            holder.binding?.animationView?.playAnimation()
        }else{
            holder.binding?.animationView?.setAnimation(item.selectedAnimation)
        }

    }
    fun setItemSelected(position: Int){
        rowSelected = position
        notifyItemChanged(position)
        notifyItemChanged(oldSelection)
        oldSelection = position
//        notifyDataSetChanged()
//        notifyItemChanged(rowSelected)
//        if (rowSelected != 0){
//            notifyItemChanged(rowSelected-1)
//        }

//        oldSelection = rowSelected
//        notifyDataSetChanged()
    }
}