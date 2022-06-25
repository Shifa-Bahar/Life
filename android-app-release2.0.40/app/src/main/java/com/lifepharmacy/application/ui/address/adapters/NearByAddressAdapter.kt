package com.lifepharmacy.application.ui.address.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.*
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.googleplaces.Result
import com.lifepharmacy.application.ui.address.dailog.ClickAddressFragment
import com.lifepharmacy.application.ui.productList.adapter.ProductAdapter

import kotlin.collections.ArrayList

class NearByAddressAdapter(
  context: Activity,
  private val onItemTapped: ClickItemNearByAddress,
) :
  RecyclerView.Adapter<NearByAddressAdapter.ItemViewHolder>() {
  private var arrayList: ArrayList<Result> = ArrayList()
  private var activity: Activity = context
  var rowSelected = -1
  var oldSelection = -1

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding: ItemNearByAddressBinding = DataBindingUtil.inflate(
      LayoutInflater.from(activity),
      R.layout.item_near_by_address,
      parent, false
    )
    return ItemViewHolder(binding.root, viewType)

  }


  override fun getItemCount(): Int {
    return arrayList.size
  }

  class ItemViewHolder internal constructor(itemView: View, int: Int) :
    RecyclerView.ViewHolder(itemView) {
    var binding: ItemNearByAddressBinding? = DataBindingUtil.bind(itemView)

  }

  fun setDataChanged(list: ArrayList<Result>?) {
    arrayList.clear()
    if (list != null) {
      arrayList.addAll(list)
    }
    rowSelected = -1
    oldSelection = -1
    notifyDataSetChanged()
  }

  fun itemRemoved(position: Int) {
    arrayList?.removeAt(position)
    notifyItemRemoved(position)
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

    holder.binding?.let { bindAddress(it, position) }
  }

  private fun bindAddress(binding: ItemNearByAddressBinding, position: Int) {
    var item = arrayList[position]
    binding.item = item
    binding.position = position
    binding.isSelected = position == rowSelected
    binding.click = onItemTapped
  }

  fun selectedItem(position: Int) {
    rowSelected = if (position != rowSelected) {
      position
    } else {
      -1
    }
    notifyItemChanged(rowSelected)
    notifyItemChanged(oldSelection)
    oldSelection = rowSelected

  }
}