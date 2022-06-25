package com.lifepharmacy.application.ui.categories.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.ItemSectionProductCategoryBinding
import com.lifepharmacy.application.model.home.SectionData

class SectionProductsAdapter(context: Activity?, private val onItemTapped: ClickSectionProductsCategory) :
    RecyclerView.Adapter<SectionProductsAdapter.ItemViewHolder>() {
    var arrayList: ArrayList<SectionData>? = ArrayList()
    var activity: Activity? = context
    var rowSelected = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ItemSectionProductCategoryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.item_section_product_category,
            parent, false
        )
        return ItemViewHolder(binding.root)
    }


    override fun getItemCount(): Int {
        return if (null != arrayList) arrayList!!.size else 0
    }

    class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemSectionProductCategoryBinding? = DataBindingUtil.bind(itemView)
    }

    fun setDataChanged(order: ArrayList<SectionData>?) {
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
        holder.binding?.isSelected = rowSelected == position

    }
    fun setItemSelected(position: Int){
        rowSelected = position
        notifyDataSetChanged()
    }
}