package com.lifepharmacy.application.ui.prescription.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.*
import java.io.File

import kotlin.collections.ArrayList

class ImagesAdapter(
  context: Activity?,
  private val onItemTapped: ClickItemImage,
  var isEdit: Boolean = true
) :
  RecyclerView.Adapter<ImagesAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<String>? = ArrayList()
  var activity: Activity? = context


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemImagesBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_images,
      parent, false
    )
    return ItemViewHolder(binding.root)
  }


  override fun getItemCount(): Int {
    return if (null != arrayList) arrayList!!.size else 0
  }

  class ItemViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemImagesBinding? = DataBindingUtil.bind(itemView)
  }

  fun setDataChanged(list: ArrayList<String>?) {
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
    holder.binding?.isEdit = isEdit
  }

}