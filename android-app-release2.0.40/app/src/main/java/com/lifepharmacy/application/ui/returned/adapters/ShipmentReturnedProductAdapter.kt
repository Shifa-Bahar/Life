package com.lifepharmacy.application.ui.returned.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemReturnShipmentProductBinding
import com.lifepharmacy.application.model.ProductModel

class ShipmentReturnedProductAdapter(context: Activity?) :
    RecyclerView.Adapter<ShipmentReturnedProductAdapter.ItemViewHolder>() {
    var arrayList: ArrayList<ProductModel>? = ArrayList()
    var activity: Activity? = context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ItemReturnShipmentProductBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.item_return_shipment_product,
            parent, false
        )
        return ItemViewHolder(binding.root)
    }


    override fun getItemCount(): Int {
        return if (null != arrayList) arrayList!!.size else 0
    }

    class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemReturnShipmentProductBinding? = DataBindingUtil.bind(itemView)
    }

    fun setDataChanged(order: ArrayList<ProductModel>?) {
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