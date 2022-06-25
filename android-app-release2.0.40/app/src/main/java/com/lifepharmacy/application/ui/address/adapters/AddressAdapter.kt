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
import com.lifepharmacy.application.ui.address.dailog.ClickAddressFragment
import com.lifepharmacy.application.ui.productList.adapter.ProductAdapter

import kotlin.collections.ArrayList

class AddressAdapter(
  context: Activity,
  private val onItemTapped: ClickItemAddress,
  private val addressClicked: ClickAddressFragment
) :
  RecyclerView.Adapter<AddressAdapter.ItemViewHolder>() {
  private var arrayList: ArrayList<AddressModel> = ArrayList()
  private var activity: Activity = context
  var rowSelected = -1
  var oldSelection = -1
  var viewType = 0
  override fun getItemViewType(position: Int): Int {
    return when (position) {
      0 -> {
        0
      }
      (arrayList.size ?: 0) + 1 -> {
        2
      }
      else -> {
        1
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    this.viewType = viewType
    return if (viewType == 0) {
      val binding: LayoutRecyclerTitleBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_recycler_title,
        parent, false
      )
      ItemViewHolder(binding.root, viewType)
    } else if (viewType == 2) {
      val binding: LayoutAddAddressBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_add_address,
        parent, false
      )
      ItemViewHolder(binding.root, viewType)
    } else {
      val binding: ItemAddressBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.item_address,
        parent, false
      )
      return ItemViewHolder(binding.root, viewType)
    }


  }


  override fun getItemCount(): Int {
    return arrayList.size + 2
  }

  class ItemViewHolder internal constructor(itemView: View, int: Int) :
    RecyclerView.ViewHolder(itemView) {
    var bindingTitle: LayoutRecyclerTitleBinding? = null

    var bindingAddress: ItemAddressBinding? = null
    var bindingAddAddress: LayoutAddAddressBinding? = null

    init {
      when (int) {
        0 -> {
          bindingTitle = DataBindingUtil.bind(itemView)
        }
        2 -> {
          bindingAddAddress = DataBindingUtil.bind(itemView)
        }
        else -> {
          bindingAddress = DataBindingUtil.bind(itemView)
        }
      }
    }
  }

  fun setDataChanged(list: ArrayList<AddressModel>?) {
    arrayList.clear()
    if (list != null) {
      arrayList.addAll(list)
    }
    notifyDataSetChanged()
  }

  fun itemRemoved(position: Int) {
    arrayList?.removeAt(position)
    notifyItemRemoved(position)
  }


  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    val arrayPosition = position - 1
    when (viewType) {
      0 -> {
        holder.bindingTitle?.let { bindTitle(it) }
      }
      2 -> {
        holder.bindingAddAddress?.let { bindAddAddress(it) }
      }
      else -> {
        holder.bindingAddress?.let { bindAddress(it, arrayPosition, position) }
      }
    }

  }

  private fun bindTitle(binding: LayoutRecyclerTitleBinding) {
    binding.showEmpty = arrayList.isEmpty()
    binding.title = activity.getString(R.string.save_address)
  }

  private fun bindAddress(binding: ItemAddressBinding, arrayPosition: Int, position: Int) {
    if (arrayPosition >= 0) {
      if (!arrayList.isNullOrEmpty()) {
        val item = arrayList[arrayPosition]
        binding.item = item
        binding.click = onItemTapped
        binding.position = arrayPosition
        binding.isSelector = rowSelected == position
      }
    }
  }

  private fun bindAddAddress(binding: LayoutAddAddressBinding) {
    binding.click = addressClicked
  }

  fun selectedItem(position: Int?) {
    if (position != null) {
      viewType = 1
      val temp = position + 1
      rowSelected = temp
      if (rowSelected != -1) {
        notifyItemChanged(rowSelected)
      }
      if (oldSelection != -1 && oldSelection != 0) {
        notifyItemChanged(oldSelection)
      }
      oldSelection = rowSelected
    }

  }
}