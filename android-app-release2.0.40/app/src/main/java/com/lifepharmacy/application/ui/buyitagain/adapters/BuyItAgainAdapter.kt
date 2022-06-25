package com.lifepharmacy.application.ui.buyitagain.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemBuyAgainBinding
import com.lifepharmacy.application.model.ProductModel
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.ui.whishlist.adapters.ClickItemWish

class BuyItAgainAdapter(context: Activity?, private val onItemTapped: ClickItemWish) :
    RecyclerView.Adapter<BuyItAgainAdapter.ItemViewHolder>() {
    var arrayList: ArrayList<ProductDetails>? = ArrayList()
    var activity: Activity? = context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ItemBuyAgainBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.item_buy_again,
            parent, false
        )
        return ItemViewHolder(binding.root)
    }


    override fun getItemCount(): Int {
        return if (null != arrayList) arrayList!!.size else 0
    }

    class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemBuyAgainBinding? = DataBindingUtil.bind(itemView)
    }

    fun setDataChanged(order: ArrayList<ProductDetails>?) {
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