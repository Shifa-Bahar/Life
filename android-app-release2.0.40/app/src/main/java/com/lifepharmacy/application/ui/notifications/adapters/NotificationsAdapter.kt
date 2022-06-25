package com.lifepharmacy.application.ui.notifications.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemNotificationsBinding
import com.lifepharmacy.application.model.notifications.NotificationModel

class NotificationsAdapter(context: Activity?, private val onItemTapped: ClickItemNotification) :
    RecyclerView.Adapter<NotificationsAdapter.ItemViewHolder>() {
    var arrayList: ArrayList<NotificationModel>? = ArrayList()
    var activity: Activity? = context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ItemNotificationsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.item_notifications,
            parent, false
        )
        return ItemViewHolder(binding.root)
    }


    override fun getItemCount(): Int {
        return if (null != arrayList) arrayList!!.size else 0
    }

    class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemNotificationsBinding? = DataBindingUtil.bind(itemView)
    }

    fun setDataChanged(order: ArrayList<NotificationModel>?) {
        arrayList?.clear()
        if (order != null) {
            arrayList?.addAll(order)
        }
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var item = arrayList!![position]
        holder.binding?.item = item
        holder.binding?.click = onItemTapped
    }

}