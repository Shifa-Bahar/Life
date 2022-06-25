package com.lifepharmacy.application.ui.documents.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifepharmacy.application.R
import com.lifepharmacy.application.databinding.*
import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.docs.DocumentModel
import com.lifepharmacy.application.ui.address.adapters.AddressAdapter
import com.lifepharmacy.application.ui.address.adapters.TimeTypeTypeAdapter
import com.lifepharmacy.application.ui.address.dailog.ClickAddressFragment
import com.lifepharmacy.application.ui.productList.adapter.ProductAdapter

import kotlin.collections.ArrayList

class DocsAdapter(
  context: Activity,
  private val onItemTapped: ClickItemDoc,
  private val isProfile: Boolean =false
) :
  RecyclerView.Adapter<DocsAdapter.ItemViewHolder>() {
  var arrayList: ArrayList<DocumentModel> = ArrayList()
  private var activity: Activity = context
  var rowSelected = -1
  var oldSelection = -1

  var viewType = 0
  override fun getItemViewType(position: Int): Int {
    return when (position) {
      0 -> {
        0
      }else -> {
        1
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    return if (viewType == 0) {
      val binding: LayoutRecyclerTitleBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.layout_recycler_title,
        parent, false
      )
      ItemViewHolder(binding.root, viewType)
    }else{
      val binding: ItemDocsBinding = DataBindingUtil.inflate(
        LayoutInflater.from(activity),
        R.layout.item_docs,
        parent, false
      )
      return ItemViewHolder(binding.root,viewType)
    }

  }


  override fun getItemCount(): Int {
    return arrayList.size+1
  }

  class ItemViewHolder internal constructor(itemView: View, int: Int) :
    RecyclerView.ViewHolder(itemView) {
    var bindingTitle: LayoutRecyclerTitleBinding? = null

    var bindingItems: ItemDocsBinding? = null
    init {
      when (int) {
        0 -> {
          bindingTitle = DataBindingUtil.bind(itemView)
        }
        else -> {
          bindingItems = DataBindingUtil.bind(itemView)
        }
      }
    }
  }

  fun setDataChanged(list: ArrayList<DocumentModel>?) {
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
    when (position) {
      0 -> {
        holder.bindingTitle?.let { bindTitle(it) }
      }
      else -> {
        holder.bindingItems?.let { bindDocs(it,arrayPosition,position) }
      }
    }

  }

  private fun bindTitle(binding: LayoutRecyclerTitleBinding) {
    binding.showEmpty = false
    binding.title = activity.getString(R.string.saved_docs)
  }
  private fun bindDocs(binding: ItemDocsBinding,arrayPosition: Int,position: Int) {
    binding.isProfile = isProfile
    binding.isSelector = rowSelected == position
    binding.position = arrayPosition
    binding.click = onItemTapped
    if (!arrayList.isNullOrEmpty()) {
      val item = arrayList[arrayPosition]
      binding.item = item
    }
  }
  fun selectedItem(position: Int?) {
    if (position != null) {
      viewType = 1
      val temp = position+1
      rowSelected = temp
      notifyItemChanged(oldSelection)
      notifyItemChanged(rowSelected)
      oldSelection = rowSelected
    }

  }
}